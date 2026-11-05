package ch.epfl.chacun;

/**
 * Represents a position with coordinates (x, y).
 *
 * @author Imad Bechlaghem (375401)
 */
public record Pos(
        int x,
        int y
) {

    /**
     * The origin position (0, 0).
     */
    public static final Pos ORIGIN = new Pos(0, 0);

    /**
     * Translates the position by the specified amounts in the x and y directions.
     *
     * @param dX The amount to translate in the x direction.
     * @param dY The amount to translate in the y direction.
     * @return The translated position.
     * @author Imad Bechlaghem (375401)
     */
    public Pos translated(int dX, int dY) {
        return new Pos(this.x() + dX, this.y() + dY);
    }

    /**
     * Returns the neighbor position in the specified direction.
     *
     * @param direction The direction of the neighbor position.
     * @return The neighbor position.
     * @author Imad Bechlaghem (375401)
     */
    public Pos neighbor(Direction direction) {
        return switch (direction) {
            case N -> translated(0, -1);
            case E -> translated(1, 0);
            case S -> translated(0, 1);
            case W -> translated(-1, 0);
        };
    }
}
