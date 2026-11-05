package ch.epfl.chacun;

/**
 * Represents an animal with an ID and a kind.
 *
 * @author Tino Bianconi (379733)
 */
public record Animal(int id, Kind kind) {

    /**
     * Enum representing the kind of animal.
     */
    public enum Kind {
        /**
         * A mammoth.
         */
        MAMMOTH,
        /**
         * An aurochs.
         */
        AUROCHS,
        /**
         * A deer.
         */
        DEER,
        /**
         * A tiger.
         */
        TIGER
    }

    /**
     * Calculates and returns the tile ID of the animal.
     *
     * @return The tile ID of the animal.
     * @author Tino Bianconi (379733)
     */
    public int tileId() {
        return Zone.tileId(id/10);
    }
}
