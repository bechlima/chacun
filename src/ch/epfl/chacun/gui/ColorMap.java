package ch.epfl.chacun.gui;

import ch.epfl.chacun.PlayerColor;
import javafx.scene.paint.Color;

/**
 * Utility class for mapping player colors to fill and stroke colors.
 *
 * @author Imad Bechlaghem (375401)
 */
public final class ColorMap {

    // Private constructor to prevent instantiation
    private ColorMap() {}

    /**
     * Returns the fill color for a given player color.
     *
     * @param color The player color.
     * @return The corresponding fill color.
     */
    public static Color fillColor(PlayerColor color) {
        return switch (color) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case GREEN -> Color.LIME;
            case YELLOW -> Color.YELLOW;
            case PURPLE -> Color.PURPLE;
        };
    }

    /**
     * Returns the stroke color for a given player color.
     *
     * @param color The player color.
     * @return The corresponding stroke color.
     */
    public static Color strokeColor(PlayerColor color) {
        boolean colorCheck = (color == PlayerColor.YELLOW || color == PlayerColor.GREEN);
        return colorCheck ? fillColor(color).deriveColor(0, 1, 0.6, 1) : Color.WHITE;
    }
}
