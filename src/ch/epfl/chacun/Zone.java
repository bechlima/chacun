package ch.epfl.chacun;

import java.util.List;

/**
 * A sealed interface representing different zones in the game.
 *
 * @author Tino Bianconi (379733)
 */
public sealed interface Zone {

    /**
     * Enum representing special powers associated with zones.
     */
    enum SpecialPower {
        /**
         * Shaman special power.
         */
        SHAMAN,
        /**
         * Logboat special power.
         */
        LOGBOAT,
        /**
         * Hunting trap special power.
         */
        HUNTING_TRAP,
        /**
         * Pit trap special power.
         */
        PIT_TRAP,
        /**
         * Wildfire special power.
         */
        WILD_FIRE,
        /**
         * Raft special power.
         */
        RAFT
    }

    /**
     * Returns the tile ID for the given zone ID.
     *
     * @param zoneId The zone ID.
     * @return The tile ID.
     * @author Tino Bianconi (379733)
     */
    static int tileId(int zoneId) {
        return zoneId / 10;
    }

    /**
     * Returns the local ID for the given zone ID.
     *
     * @param zoneId The zone ID.
     * @return The local ID.
     * @author Tino Bianconi (379733)
     */
    static int localId(int zoneId) {
        return (zoneId % 10);
    }

    /**
     * Gets the ID of the zone.
     *
     * @return The ID of the zone.
     * @author Tino Bianconi (379733)
     */
    int id();

    /**
     * Gets the tile ID of the zone.
     *
     * @return The tile ID of the zone.
     * @author Tino Bianconi (379733)
     */
    default int tileId() {
        return tileId(id());
    }

    /**
     * Gets the local ID of the zone.
     *
     * @return The local ID of the zone.
     * @author Tino Bianconi (379733)
     */
    default int localId() {
        return localId(id());
    }

    /**
     * Gets the special power associated with the zone.
     *
     * @return The special power, or null if none.
     * @author Tino Bianconi (379733)
     */
    default SpecialPower specialPower() {
        return null;
    }

    /**
     * Represents a forest zone.
     *
     * @author Imad Bechlaghem (375401)
     */
    record Forest(int id, Kind kind) implements Zone {

        /**
         * Enum representing the kind of forest.
         */
        public enum Kind {
            /**
             * Plain forest.
             */
            PLAIN,
            /**
             * Forest with menhir.
             */
            WITH_MENHIR,
            /**
             * Forest with mushrooms.
             */
            WITH_MUSHROOMS
        }
    }

    /**
     * Represents a meadow zone.
     *
     * @author Tino Bianconi (379733)
     */
    record Meadow(int id, List<Animal> animals, SpecialPower specialPower) implements Zone {

        /**
         * Constructs a meadow with the specified animals and special power.
         *
         * @param id           ID of the meadow;
         * @param animals      The animals in the meadow.
         * @param specialPower The special power associated with the meadow.
         * @author Tino Bianconi (379733)
         */
        public Meadow {
            animals = List.copyOf(animals);
        }

    }

    /**
     * Represents a water zone.
     *
     * @author Imad Bechlaghem (375401)
     */
    sealed interface Water extends Zone {
        /**
         * Gets the count of fish in the water zone.
         *
         * @author Imad Bechlaghem (375401)
         */
        int fishCount();
    }

    /**
     * Represents a lake zone.
     *
     * @author Tino Bianconi (379733)
     */
    record Lake(int id, int fishCount, SpecialPower specialPower) implements Water {
    }

    /**
     * Represents a river zone.
     *
     * @author Tino Bianconi (379733)
     */
    record River(int id, int fishCount, Lake lake) implements Water {

        /**
         * Checks if the river has a lake.
         *
         * @return True if the river has a lake, otherwise false.
         * @author Tino Bianconi (379733)
         */
        public boolean hasLake() {
            return lake != null;
        }

    }
}
