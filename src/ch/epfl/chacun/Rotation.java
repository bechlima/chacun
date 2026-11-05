package ch.epfl.chacun;

import java.util.List;

/**
 * Represents different types of rotations.
 *
 * @author Imad Bechlaghem (375401)
 */
public enum Rotation {
    /**
     * No rotation.
     */
    NONE,
    /**
     * Right rotation (clockwise).
     */
    RIGHT,
    /**
     * Half-turn rotation (180 degrees).
     */
    HALF_TURN,
    /**
     * Left rotation (counterclockwise).
     */
    LEFT;

    /**
     * A list containing all rotation types.
     */
    public static final List<Rotation> ALL = List.of(values());

    /**
     * The count of rotation types.
     */
    public static final int COUNT = ALL.size();

    /**
     * Adds another rotation to this rotation.
     *
     * @param that The rotation to add.
     * @return The resulting rotation.
     */
    public Rotation add(Rotation that) {
        return ALL.get((this.ordinal() + that.ordinal()) % COUNT);
    }

    /**
     * Negates this rotation.
     *
     * @return The negated rotation.
     */
    public Rotation negated() {
        return ALL.get((COUNT - this.ordinal()) % COUNT);
    }

    /**
     * Returns the number of quarter-turns clockwise for this rotation.
     *
     * @return The number of quarter-turns clockwise.
     */
    public int quarterTurnsCW() {
        return this.ordinal();
    }

    /**
     * Returns the number of degrees clockwise for this rotation.
     *
     * @return The number of degrees clockwise.
     */
    public int degreesCW() {
        return this.ordinal() * 90;
    }
}
