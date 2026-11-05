package ch.epfl.chacun;

import java.util.ArrayList;
import java.util.List;

/**
 * A sealed interface representing different types of sides on a tile.
 * Each type of side can contain specific zones.
 *
 * @author Imad Bechlaghem (375401)
 */
public sealed interface TileSide permits TileSide.Forest, TileSide.Meadow, TileSide.River {

    /**
     * Retrieves the list of zones contained in this side.
     *
     * @return The list of zones contained in this side.
     * @author Imad Bechlaghem (375401)
     */
    List<Zone> zones();

    /**
     * Checks if this side is of the same kind as the specified side.
     *
     * @param that The other TileSide to compare.
     * @return True if both sides are of the same kind, false otherwise.
     * @author Imad Bechlaghem (375401)
     */
    boolean isSameKindAs(TileSide that);

    /**
     * Represents a side of a tile containing a forest zone.
     *
     * @author Tino Bianconi (379733)
     */
    record Forest(Zone.Forest forest) implements TileSide {

        /**
         * Retrieves the list of zones contained in this forest side.
         *
         * @return The list containing the forest zone.
         * @author Tino Bianconi (379733)
         */
        @Override
        public List<Zone> zones() {
            return List.of(forest);
        }

        /**
         * Checks if this forest side is of the same kind as the specified side.
         *
         * @param that The other TileSide to compare.
         * @return True if both sides are forest sides, false otherwise.
         * @author Tino Bianconi (379733)
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Forest;
        }
    }

    /**
     * Represents a side of a tile containing a meadow zone.
     *
     * @author Imad Bechlaghem (375401)
     */
    record Meadow(Zone.Meadow meadow) implements TileSide {

        /**
         * Retrieves the list of zones contained in this meadow side.
         *
         * @return The list containing the meadow zone.
         * @author Imad Bechlaghem (375401)
         */
        @Override
        public List<Zone> zones() {
            return List.of(meadow);
        }

        /**
         * Checks if this meadow side is of the same kind as the specified side.
         *
         * @param that The other TileSide to compare.
         * @return True if both sides are meadow sides, false otherwise.
         * @author Imad Bechlaghem (375401)
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Meadow;
        }
    }

    /**
     * Represents a side of a tile containing a river zone surrounded by meadows.
     *
     * @author Imad Bechlaghem (375401)
     */
    record River(Zone.Meadow meadow1, Zone.River river, Zone.Meadow meadow2) implements TileSide {

        /**
         * Retrieves the list of zones contained in this riverside.
         *
         * @return The list containing the meadow and river zones.
         * @author Imad Bechlaghem (375401)
         */
        @Override
        public List<Zone> zones() {
            return List.of(meadow1, river, meadow2);
        }

        /**
         * Checks if this riverside is of the same kind as the specified side.
         *
         * @param that The other TileSide to compare.
         * @return True if both sides are river sides, false otherwise.
         * @author Imad Bechlaghem (375401)
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof River;
        }
    }
}


