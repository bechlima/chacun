package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.Tile;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.function.Consumer;

/**
 * Utility class for creating the Decks UI component.
 *
 * @author Tino Bianconi (379733)
 */
public final class DecksUI {

    // Private constructor to prevent instantiation
    private DecksUI() {}

    /**
     * Creates a UI component displaying the current tile, normal tiles deck, and menhir tiles deck.
     *
     * @param currentTileO The observable current tile.
     * @param normalTilesLeftO The observable number of normal tiles left.
     * @param menhirTilesLeftO The observable number of menhir tiles left.
     * @param textToPlaceO The observable text to place.
     * @param eventHandler The event handler for clicking on the tile text.
     * @return A Node containing the decks UI.
     */
    public static Node create(ObservableValue<Tile> currentTileO, ObservableValue<Integer> normalTilesLeftO, ObservableValue<Integer> menhirTilesLeftO, ObservableValue<String> textToPlaceO, Consumer<Occupant> eventHandler) {

        // VBox
        VBox vbox = new VBox();
        vbox.getStylesheets().add("/decks.css");

        // Tile to Place
        StackPane currentTileStack = new StackPane();
        currentTileStack.setId("next-tile");

        ImageView currentTileView = new ImageView();
        ObservableValue<Image> currentTileImageO = currentTileO.map(tile -> ImageLoader.largeImageForTile(tile.id()));
        currentTileView.setFitHeight(ImageLoader.LARGE_TILE_FIT_SIZE);
        currentTileView.setFitWidth(ImageLoader.LARGE_TILE_FIT_SIZE);
        currentTileView.imageProperty().bind(currentTileImageO);

        Text currentTileText = new Text();
        currentTileText.textProperty().bind(textToPlaceO);
        currentTileText.setWrappingWidth(ImageLoader.LARGE_TILE_FIT_SIZE * 0.8);
        currentTileView.visibleProperty().bind(currentTileText.visibleProperty().not());
        currentTileText.visibleProperty().bind(textToPlaceO.map(t -> !t.isEmpty()));
        currentTileText.setOnMouseClicked(_ -> eventHandler.accept(null));
        currentTileStack.getChildren().addAll(currentTileView, currentTileText);

        // HBox
        HBox hbox = new HBox();
        hbox.setId("decks");

        vbox.getChildren().addAll(hbox, currentTileStack);

        // Normal Tiles Deck
        StackPane normalDeckStack = new StackPane();
        ImageView normalTileImage = new ImageView();
        normalTileImage.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
        normalTileImage.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
        normalTileImage.setId("NORMAL");

        Text normalTileText = new Text();
        ObservableValue<String> normalTileNumberO = normalTilesLeftO.map(Object::toString);
        normalTileText.textProperty().bind(normalTileNumberO);
        normalDeckStack.getChildren().addAll(normalTileImage, normalTileText);

        // Menhir Tiles Deck
        StackPane menhirDeckStack = new StackPane();
        ImageView menhirTileImage = new ImageView();
        menhirTileImage.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
        menhirTileImage.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
        menhirTileImage.setId("MENHIR");

        Text menhirTileText = new Text();
        ObservableValue<String> menhirTileNumberO = menhirTilesLeftO.map(Object::toString);
        menhirTileText.textProperty().bind(menhirTileNumberO);
        menhirDeckStack.getChildren().addAll(menhirTileImage, menhirTileText);

        hbox.getChildren().addAll(normalDeckStack, menhirDeckStack);

        return vbox;
    }
}