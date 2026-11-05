package ch.epfl.chacun;

import java.util.List;

/**
 * Represents cardinal directions: North, East, South, West.
 *
 * @author Tino Bianconi (379733)
 */
public enum Direction {
    /**
     * North direction.
     */
    N,
    /**
     * East direction.
     */
    E,
    /**
     * South direction.
     */
    S,
    /**
     * West direction.
     */
    W;

    /**
     * A list containing all the cardinal directions.
     * This list is immutable.
     */
    public static final List<Direction> ALL = List.of(values());

    /**
     * The count of cardinal directions.
     */
    public static final int COUNT = ALL.size();

    /**
     * Rotates the direction based on the given rotation.
     *
     * @param rotation The rotation to apply.
     * @return The rotated direction.
     * @author Tino Bianconi (379733)
     */
    public Direction rotated(Rotation rotation) {
        return ALL.get((this.ordinal() + rotation.ordinal()) % COUNT);
    }

    /**
     * Returns the opposite direction.
     *
     * @return The opposite direction.
     * @author Tino Bianconi (379733)
     */
    public Direction opposite() {
        return rotated(Rotation.HALF_TURN);
    }
}
