package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Set;

/**
 * Utility class for creating the Message Board UI component.
 *
 * @author Tino Bianconi (379733)
 */
public final class MessageBoardUI {

    // Private constructor to prevent instantiation
    private MessageBoardUI() {}

    /**
     * Creates a UI component displaying a message board with messages.
     *
     * @param observableListOfMessage The observable list of messages.
     * @param highlightTiles The property for highlighting tiles.
     * @return A Node containing the message board UI.
     */
    public static Node create(ObservableValue<List<MessageBoard.Message>> observableListOfMessage, ObjectProperty<Set<Integer>> highlightTiles) {
        VBox vBox = new VBox();
        ScrollPane messageScrollPane = new ScrollPane(vBox);
        messageScrollPane.setId("message-board");
        messageScrollPane.getStyleClass().add("message-board.css");

        observableListOfMessage.addListener(((_, oldValue, newValue) -> {
            for (MessageBoard.Message message : newValue.stream().skip(oldValue.size()).toList()) {
                Text text = new Text(message.text());
                text.setWrappingWidth(ImageLoader.LARGE_TILE_FIT_SIZE);
                vBox.getChildren().add(text);
                text.setOnMouseEntered((_ -> highlightTiles.setValue(message.tileIds())));
                text.setOnMouseExited((_ -> highlightTiles.setValue(Set.of())));
            }
        }));

        messageScrollPane.layout();
        messageScrollPane.setVvalue(1);

        return messageScrollPane;
    }
}
