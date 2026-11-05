package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import java.util.*;
import java.util.function.Consumer;

/**
 * Utility class for creating the Board UI component.
 *
 * @author Tino Bianconi (379733)
 */
public final class BoardUI {

    // Private constructor to prevent instantiation
    private BoardUI() {}

    /**
     * Creates a UI component for displaying the game board with tiles, animals, and occupants.
     *
     * @param boardRange       The range of the board.
     * @param gameStateO       The observable game state.
     * @param tileRotationO    The observable tile rotation.
     * @param visibleOccupantsO The observable set of visible occupants.
     * @param tileIDsO         The observable set of tile IDs.
     * @param rotationHandler  The handler for processing rotation events.
     * @param positionHandler  The handler for processing position events.
     * @param occupantHandler  The handler for processing occupant events.
     * @return A Node containing the board UI.
     */
    public static Node create(
            int boardRange,
            ObservableValue<GameState> gameStateO,
            ObservableValue<Rotation> tileRotationO,
            ObservableValue<Set<Occupant>> visibleOccupantsO,
            ObservableValue<Set<Integer>> tileIDsO,
            Consumer<Rotation> rotationHandler,
            Consumer<Pos> positionHandler,
            Consumer<Occupant> occupantHandler) {

        Preconditions.checkArgument(boardRange > 0);

        WritableImage emptyTileImage = new WritableImage(1, 1);
        emptyTileImage
                .getPixelWriter()
                .setColor(0, 0, Color.gray(0.98));

        GridPane gridPane = new GridPane();
        gridPane.setId("board-grid");

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setId("board-scroll-pane");
        scrollPane.getStylesheets().add("/board.css");

        for (int x = -boardRange; x <= boardRange; x++) {
            for (int y = -boardRange; y <= boardRange; y++) {
                Pos pos = new Pos(x, y);

                ImageView tileImage = new ImageView();
                ObservableValue<PlacedTile> boardTileO = gameStateO.map(gameState -> gameState.board().tileAt(pos));
                ObservableValue<Image> currentTileImageO = boardTileO.map(placedTile -> ImageLoader.normalImageForTile(placedTile.id()));
                ObservableValue<Set<Animal>> cancelledAnimalO = gameStateO.map(g -> g.board().cancelledAnimals());
                tileImage.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
                tileImage.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
                tileImage.imageProperty().setValue(emptyTileImage);
                Group group = new Group(tileImage);

                // Informations concernant chaque cellule
                ObservableValue<CellData> cellO = Bindings.createObjectBinding(() -> {
                            Image image = emptyTileImage;
                            PlacedTile tile = boardTileO.getValue();
                            Color veilColor = Color.TRANSPARENT;
                            Rotation rotation = (tile != null) ? tile.rotation() : tileRotationO.getValue();

                            if (tile != null) {
                                image = CellData.imageMap.get(tile.id());
                                if (!tileIDsO.getValue().contains(tile.id()) && !tileIDsO.getValue().isEmpty()) {
                                    veilColor = Color.BLACK;
                                }
                            } else if (gameStateO.getValue().nextAction() == GameState.Action.PLACE_TILE) {
                                if (gameStateO.getValue().board().insertionPositions().contains(pos) && gameStateO.getValue().tileToPlace() != null) {
                                    if (!group.isHover()) {
                                        veilColor = ColorMap.fillColor(gameStateO.getValue().currentPlayer());
                                    } else {
                                        veilColor = Color.TRANSPARENT;
                                        image = CellData.imageMap.computeIfAbsent(gameStateO.getValue().tileToPlace().id(), ImageLoader::normalImageForTile);
                                        if (!gameStateO.getValue().board().canAddTile(new PlacedTile(gameStateO.getValue().tileToPlace(), gameStateO.getValue().currentPlayer(), tileRotationO.getValue(), pos))) {
                                            veilColor = Color.WHITE;
                                        }
                                    }
                                }
                            }
                            return new CellData(image, rotation, veilColor);
                        },
                        group.hoverProperty(),
                        tileRotationO,
                        gameStateO,
                        tileIDsO,
                        boardTileO);

                // Construction de chaque case
                boardTileO.addListener((_, _, newPlacedTile) -> {
                    List<Animal> animalOnTile = new ArrayList<>();
                    newPlacedTile.meadowZones().forEach(zone -> animalOnTile.addAll(zone.animals()));

                    // Jetons d'annulation
                    animalOnTile.forEach(a -> {
                        ImageView cancelToken = new ImageView();
                        cancelToken.setId("marker_" + a.id());
                        cancelToken.getStyleClass().add("marker");
                        cancelToken.setFitWidth(ImageLoader.MARKER_FIT_SIZE);
                        cancelToken.setFitHeight(ImageLoader.MARKER_FIT_SIZE);
                        cancelToken.visibleProperty().bind(cancelledAnimalO.map(cancelledA -> cancelledA.contains(a)));
                        group.getChildren().add(cancelToken);
                    });

                    // Occupant
                    newPlacedTile.potentialOccupants().forEach(o -> {
                        Node occupant = Icon.newFor(newPlacedTile.placer(), o.kind());
                        if (o.kind() == Occupant.Kind.PAWN) {
                            occupant.setId("pawn_" + o.zoneId());
                        } else {
                            occupant.setId("hut_" + o.zoneId());
                        }
                        occupant.visibleProperty().bind(visibleOccupantsO.map(visibleOccupantSet -> visibleOccupantSet.contains(o)));
                        occupant.rotateProperty().bind(boardTileO.map(placedTile -> placedTile.rotation().negated().degreesCW()));
                        occupant.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            if (event.getButton() == MouseButton.PRIMARY
                                    && (gameStateO.getValue().nextAction() == GameState.Action.OCCUPY_TILE
                                    || gameStateO.getValue().nextAction() == GameState.Action.RETAKE_PAWN)) {
                                occupantHandler.accept(o);
                            }
                        });
                        group.getChildren().add(occupant);
                    });

                    // Image de fond
                    if (boardTileO.getValue() != null) {
                        tileImage.imageProperty().bind(currentTileImageO);
                        CellData.imageMap.put(newPlacedTile.id(), currentTileImageO.getValue());
                    }
                });

                // Voile
                ObservableValue<Color> veilColorO = cellO.map(CellData::color);
                ColorInput colorInput = new ColorInput();
                colorInput.setHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
                colorInput.setWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
                colorInput.paintProperty().bind(veilColorO);
                tileImage.imageProperty().bind(cellO.map(CellData::background));
                group.rotateProperty().bind(cellO.map(c -> c.tileRotation().degreesCW()));
                group.effectProperty().bind(cellO.map(_ -> {
                    Blend veilBlend = new Blend(BlendMode.SRC_OVER, null, colorInput);
                    veilBlend.opacityProperty().set(0.5);
                    return veilBlend;
                }));

                group.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.isStillSincePress()) {
                        if (event.getButton() == MouseButton.SECONDARY) {
                            if (event.isAltDown()) {
                                rotationHandler.accept(Rotation.LEFT);
                            } else {
                                rotationHandler.accept(Rotation.RIGHT);
                            }
                        }
                        if (event.getButton() == MouseButton.PRIMARY && gameStateO.getValue().nextAction() == GameState.Action.PLACE_TILE) {
                            positionHandler.accept(pos);
                        }
                    }
                });

                gridPane.add(group, x + boardRange, y + boardRange);
            }
        }

        scrollPane.setVvalue(0.5);
        scrollPane.setHvalue(0.5);

        return scrollPane;
    }

    private record CellData(Image background, Rotation tileRotation, Color color) {
        static Map<Integer, Image> imageMap = new HashMap<>();
    }
}

