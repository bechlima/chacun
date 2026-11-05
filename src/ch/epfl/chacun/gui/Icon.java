package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

/**
 * Utility class for creating SVG icons for players and occupants.
 *
 * @author Imad Bechlaghem (375401)
 */
public final class Icon {

    // Private constructor to prevent instantiation
    private Icon() {}

    /**
     * Creates a new SVG icon for a given player color and occupant kind.
     *
     * @param color The color of the player.
     * @param kind The kind of the occupant.
     * @return A Node representing the SVG icon.
     */
    public static Node newFor(PlayerColor color, Occupant.Kind kind) {
        SVGPath svgPath = new SVGPath();

        if (kind == Occupant.Kind.PAWN) {
            svgPath.setContent("M -10 10 H -4 L 0 2 L 6 10 H 12 L 5 0 L 12 -2 L 12 -4 L 6 -6 L 6 -10 L 0 -10 L -2 -4 L -6 -2 L -8 -10 L -12 -10 L -8 6 Z");
        } else {
            svgPath.setContent("M -8 10 H 8 V 2 H 12 L 0 -10 L -12 2 H -8 Z");
        }

        svgPath.setFill(ColorMap.fillColor(color));
        svgPath.setStroke(ColorMap.strokeColor(color));

        return svgPath;
    }
}
