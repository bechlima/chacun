package ch.epfl.chacun.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.TextFormatter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/**
 * Utility class for creating the Actions UI component.
 *
 * @author Imad Bechlaghem (375401)
 */
public final class ActionUI {

    // Private constructor to prevent instantiation
    private ActionUI() {}

    /**
     * Creates a UI component for displaying actions and an input field for new actions.
     *
     * @param actionsO        The observable list of actions.
     * @param actionsHandler  The handler for processing new actions.
     * @return A Node containing the actions UI.
     */
    public static Node create(ObservableValue<List<String>> actionsO,
                              Consumer<String> actionsHandler) {

        HBox hBox = new HBox();
        hBox.setId("actions");
        hBox.getStylesheets().add("/actions.css");

        Text actionsText = new Text();
        actionsO.addListener((_, _, newValue) -> {
            int totalActions = newValue.size();

            List<String> displayedActionsList = newValue.stream()
                    .skip(Math.max(0, totalActions - 4))
                    .toList();

            String displayedActions = IntStream.range(0, displayedActionsList.size())
                    .mapToObj(i -> (totalActions - displayedActionsList.size() + i + 1)
                            + " : "
                            + displayedActionsList.get(i))
                    .collect(Collectors.joining(",  "));

            actionsText.setText(displayedActions);
        });

        TextField actionField = new TextField();
        actionField.setId("action-field");

        actionField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getText().toUpperCase().replaceAll("^[0-18-9]+$", "");
            change.setText(newText);

            if (actionField.getText().length() >= 2) {
                change.setText("");
            }
            return change;
        }));

        actionField.setOnAction(_ -> {
            if (!actionField.getText().isEmpty()) {
                actionsHandler.accept(actionField.getText());
                actionField.clear();
            }
        });

        hBox.getChildren().addAll(actionsText, actionField);

        return hBox;
    }
}
