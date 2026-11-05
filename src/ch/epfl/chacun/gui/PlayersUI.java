package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * Utility class for creating the Players UI component.
 *
 * @author Tino Bianconi (379733)
 */
public final class PlayersUI {

    // Private constructor to prevent instantiation
    private PlayersUI() {}

    /**
     * Creates a UI component displaying the players and their points.
     *
     * @param observableGameState The observable game state.
     * @param textGenerator The text generator for player names and points.
     * @return A Node containing the players UI.
     */
    public static Node create(ObservableValue<GameState> observableGameState, TextMaker textGenerator) {
        ObservableValue<Map<PlayerColor, Integer>> pointsMap = observableGameState.map(GameState::messageBoard)
                .map(MessageBoard::points);
        VBox vBox = new VBox();
        vBox.getStylesheets().add("/players.css");
        vBox.setId("players");

        for (PlayerColor player : observableGameState.getValue().players()) {
            Circle circle = new Circle(5.0, ColorMap.fillColor(player));
            ObservableValue<String> pointsTextO = pointsMap.map(p -> textGenerator.playerName(player) + " : " + textGenerator.points(p.getOrDefault(player, 0)) + "\n");
            Text pointsText = new Text();
            pointsText.textProperty().bind(pointsTextO);
            ObservableValue<PlayerColor> currentPlayerO = observableGameState.map(GameState::currentPlayer);
            TextFlow textFlow = new TextFlow(circle, pointsText);
            textFlow.getStyleClass().add("player");

            currentPlayerO.addListener((_, _, newPlayer) -> {
                if (newPlayer == player) {
                    textFlow.getStyleClass().add("current");
                } else {
                    textFlow.getStyleClass().remove("current");
                }
            });

            for (int i = 0; i < Occupant.occupantsCount(Occupant.Kind.HUT); i++) {
                Node pawnIcon = Icon.newFor(player, Occupant.Kind.HUT);
                int idx = i;
                pawnIcon.opacityProperty().bind(observableGameState.map(gameState -> (gameState.freeOccupantsCount(player, Occupant.Kind.HUT) > idx ? 1 : 0.1)));
                textFlow.getChildren().add(pawnIcon);
            }

            textFlow.getChildren().add(new Text("   "));

            for (int i = 0; i < Occupant.occupantsCount(Occupant.Kind.PAWN); i++) {
                Node pawnIcon = Icon.newFor(player, Occupant.Kind.PAWN);
                int idx = i;
                pawnIcon.opacityProperty().bind(observableGameState.map(gameState -> (gameState.freeOccupantsCount(player, Occupant.Kind.PAWN) > idx ? 1 : 0.1)));
                textFlow.getChildren().add(pawnIcon);
            }

            vBox.getChildren().add(textFlow);
        }

        return vBox;
    }
}
