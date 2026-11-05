package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.*;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Main class for the ChaCuN game application.
 *
 * @author Tino Bianconi (379733)
 */
public final class Main extends Application {

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        List<Tile> tiles = new ArrayList<>(Tiles.TILES);

        List<String> playerNamesList = getParameters().getUnnamed();

        Map<String, String> seed = getParameters().getNamed();

         Map<PlayerColor, String> playerNames = IntStream.range(0, playerNamesList.size())
         .boxed()
         .collect(HashMap::new, (map, i) -> map.put(PlayerColor.ALL.get(i), playerNamesList.get(i)), HashMap::putAll);

        RandomGenerator randomSeedGenerator;

        if (seed.get("seed") != null) {
            long longSeed = Long.parseUnsignedLong(seed.get("seed"));
            randomSeedGenerator = RandomGeneratorFactory.getDefault().create(longSeed);
        } else {
            randomSeedGenerator = RandomGeneratorFactory.getDefault().create();
        }

        Collections.shuffle(tiles, randomSeedGenerator);

        Map<Tile.Kind, List<Tile>> tilesByKind = tiles.stream()
                .collect(Collectors.groupingBy(Tile::kind));

        List<PlayerColor> playerColors = playerNames.keySet().stream().sorted().toList();

        List<Tile> startDeck = tilesByKind.get(Tile.Kind.START);
        List<Tile> normalDeck = tilesByKind.get(Tile.Kind.NORMAL);
        List<Tile> menhirDeck = tilesByKind.get(Tile.Kind.MENHIR);

        TileDecks tileDecks = new TileDecks(startDeck, normalDeck, menhirDeck);
        var textMaker = new TextMakerFr(playerNames);

        GameState gameState =
                GameState.initial(playerColors,
                        tileDecks,
                        textMaker);

        ObjectProperty<GameState> gameStateO = new SimpleObjectProperty<>(gameState);

        //---------------------------PlayersUI---------------------------//

        Node playersNode = PlayersUI.create(
                gameStateO,
                gameStateO
                        .getValue()
                        .messageBoard()
                        .textMaker());

        //---------------------------ActionUI---------------------------//

        ObjectProperty<List<String>> actionsListO = new SimpleObjectProperty<>(new ArrayList<>());

        Consumer<String> actionHandler = string -> {
            if (Base32.isValid(string)) {
                ActionEncoder.StateAction stateAction = ActionEncoder.decodeAndApply(gameStateO.getValue(), string);
                if (stateAction != null) {
                    List<String> newList = new ArrayList<>(actionsListO.getValue());
                    newList.add(string);
                    actionsListO.set(newList);
                    gameStateO.set(stateAction.gameState());
                }
            }
        };

        Node actionNode = ActionUI.create(actionsListO, actionHandler);

        //---------------------------BoardUI---------------------------//

        ObjectProperty<Rotation> tileRotationO = new SimpleObjectProperty<>(Rotation.NONE);

        ObservableValue<Set<Occupant>> visibleOccupantsO = gameStateO.map(g -> {
            Set<Occupant> occupants = new HashSet<>(g.board().occupants());
            if (g.nextAction() == GameState.Action.OCCUPY_TILE) {
                occupants.addAll(g.lastTilePotentialOccupants());
            }
            return occupants;
        });

        ObjectProperty<Set<Integer>> tileIDsO = new SimpleObjectProperty<>(Set.of());

        Consumer<Rotation> rotationHandler = rotation -> {
            if (gameStateO.getValue().nextAction() == GameState.Action.PLACE_TILE) {
                tileRotationO.setValue(tileRotationO.getValue().add(rotation));
            }
        };

        Consumer<Pos> positionHandler = pos -> {
            PlacedTile tile = new PlacedTile(
                    gameStateO.getValue().tileToPlace(),
                    gameStateO.getValue().currentPlayer(),
                    tileRotationO.getValue(),
                    pos);

            if (gameStateO.getValue().board().canAddTile(tile) && gameStateO.getValue().nextAction() == GameState.Action.PLACE_TILE) {
                ActionEncoder.StateAction stateAction = ActionEncoder.withPlacedTile(gameStateO.getValue(), tile);
                gameStateO.setValue(stateAction.gameState());
                List<String> newList = new ArrayList<>(actionsListO.getValue());
                newList.add(stateAction.encoded32BitAction());
                actionsListO.setValue(newList);
                tileRotationO.setValue(Rotation.NONE);
            }
        };

        Consumer<Occupant> occupantHandler = occupant -> {
            if (gameStateO.getValue().nextAction() == GameState.Action.OCCUPY_TILE) {
                List<String> newList = new ArrayList<>(actionsListO.getValue());
                ActionEncoder.StateAction stateAction = ActionEncoder.withNewOccupant(gameStateO.getValue(), occupant);
                gameStateO.setValue(stateAction.gameState());
                newList.add(stateAction.encoded32BitAction());
                actionsListO.setValue(newList);
            } else if (gameStateO.getValue().nextAction() == GameState.Action.RETAKE_PAWN) {
                List<String> newList = new ArrayList<>(actionsListO.getValue());
                if (occupant == null
                        || (occupant.kind() == Occupant.Kind.PAWN
                        && gameStateO.getValue().currentPlayer() == gameStateO.getValue().board().tileWithId(Zone.tileId(occupant.zoneId())).placer())) {
                    ActionEncoder.StateAction stateAction = ActionEncoder.withOccupantRemoved(gameStateO.getValue(), occupant);
                    gameStateO.setValue(stateAction.gameState());
                    newList.add(stateAction.encoded32BitAction());
                }
                actionsListO.setValue(newList);
            }
        };

        Node boardNode = BoardUI.create(
                Board.REACH,
                gameStateO,
                tileRotationO,
                visibleOccupantsO,
                tileIDsO,
                rotationHandler,
                positionHandler,
                occupantHandler);

        //---------------------------DecksUI---------------------------//

        ObjectProperty<Tile> tileO = new SimpleObjectProperty<>(gameStateO.getValue().tileToPlace());
        tileO.bind(gameStateO.map(GameState::tileToPlace));

        ObjectProperty<Integer> normalTilesLeftO = new SimpleObjectProperty<>(gameStateO.getValue().tileDecks().normalTiles().size());
        normalTilesLeftO.bind(gameStateO.map(g -> g.tileDecks().normalTiles().size()));

        ObjectProperty<Integer> menhirTilesLeftO = new SimpleObjectProperty<>(menhirDeck.size());
        menhirTilesLeftO.bind(gameStateO.map(g -> g.tileDecks().menhirTiles().size()));

        ObjectProperty<String> textToPlaceO = new SimpleObjectProperty<>();
        textToPlaceO.bind(gameStateO.map(g -> switch (g.nextAction()) {
            case OCCUPY_TILE -> textMaker.clickToOccupy();
            case RETAKE_PAWN -> textMaker.clickToUnoccupy();
            default -> null;
        }));

        Node decksNode = DecksUI.create(
                tileO,
                normalTilesLeftO,
                menhirTilesLeftO,
                textToPlaceO,
                occupantHandler);

        //---------------------------MessageBoardUI---------------------------//

        ObservableValue<List<MessageBoard.Message>> messageListO = gameStateO.map(g -> g.messageBoard().messages());

        Node messageBoardNode = MessageBoardUI.create(messageListO, tileIDsO);

        //---------------------------Game Construction---------------------------//

        gameStateO.set(gameStateO.get().withStartingTilePlaced());

        Node vbox = new VBox(actionNode, decksNode);
        BorderPane secondBoarderPane = new BorderPane(messageBoardNode);
        secondBoarderPane.setBottom(vbox);
        secondBoarderPane.setTop(playersNode);
        BorderPane rootNode = new BorderPane(boardNode);
        rootNode.setRight(secondBoarderPane);

        primaryStage.setScene(new Scene(rootNode));

        primaryStage.setWidth(1440);
        primaryStage.setHeight(1080);

        primaryStage.setTitle("ChaCuN");
        primaryStage.show();
    }
}

