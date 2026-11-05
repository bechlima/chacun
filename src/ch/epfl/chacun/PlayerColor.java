package ch.epfl.chacun;

import java.util.List;

/**
 * Represents colors that can be associated with players.
 *
 * @author Tino Bianconi (379733)
 */
public enum PlayerColor {
    /**
     * The red color.
     */
    RED,
    /**
     * The blue color.
     */
    BLUE,
    /**
     * The green color.
     */
    GREEN,
    /**
     * The yellow color.
     */
    YELLOW,
    /**
     * The purple color.
     */
    PURPLE;

    /**
     * A list containing all player colors.
     * This list is immutable.
     */
    public static final List<PlayerColor> ALL = List.of(values());
}