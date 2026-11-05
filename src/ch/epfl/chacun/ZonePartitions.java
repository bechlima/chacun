package ch.epfl.chacun;


/**
 * Represents the partitions of zones in the game.
 *
 * @author Imad Bechlaghem (375401)
 * @author Tino Bianconi (379733)
 */
public record ZonePartitions(ZonePartition<Zone.Forest> forests, ZonePartition<Zone.Meadow> meadows,
                             ZonePartition<Zone.River> rivers, ZonePartition<Zone.Water> riverSystems) {

    /**
     * Static instance representing an empty ZonePartitions.
     */
    public static final ZonePartitions EMPTY = new ZonePartitions(
            new ZonePartition<>(),
            new ZonePartition<>(),
            new ZonePartition<>(),
            new ZonePartition<>());

    /**
     * Inner class for constructing ZonePartitions.
     *
     * @author Imad Bechlaghem (375401)
     * @author Tino Bianconi (379733)
     */
    public static final class Builder {
        private final ZonePartition.Builder<Zone.Forest> forestBuilder;
        private final ZonePartition.Builder<Zone.Meadow> meadowBuilder;
        private final ZonePartition.Builder<Zone.River> riverBuilder;
        private final ZonePartition.Builder<Zone.Water> waterBuilder;

        /**
         * Constructor taking an initial instance of ZonePartitions.
         *
         * @param initial The initial partition of zones.
         * @author Imad Bechlaghem (375401)
         */

        public Builder(ZonePartitions initial) {
            forestBuilder = new ZonePartition.Builder<>(initial.forests);
            meadowBuilder = new ZonePartition.Builder<>(initial.meadows);
            riverBuilder = new ZonePartition.Builder<>(initial.rivers);
            waterBuilder = new ZonePartition.Builder<>(initial.riverSystems);
        }

        /**
         * Adds the areas corresponding to the zones of the given tile to the four partitions.
         *
         * @param tile The tile to add.
         * @author Tino Bianconi (379733)
         */

        public void addTile(Tile tile) {

            int[] zoneTab = new int[10];

            for (Zone zone : tile.zones()) {

                for (TileSide tileside : tile.sides()) {
                    if (tileside.zones().contains(zone)) {
                        zoneTab[zone.localId()]++;
                    }
                }

                if (zone instanceof Zone.River river && river.hasLake()) {
                    zoneTab[zone.localId()]++;

                    zoneTab[river.lake().localId()]++;
                }
            }

            for (Zone zone : tile.zones()) {

                switch (zone) {
                    case Zone.Forest forestZone -> forestBuilder.addSingleton(forestZone, zoneTab[zone.localId()]);
                    case Zone.Meadow meadowZone -> meadowBuilder.addSingleton(meadowZone, zoneTab[zone.localId()]);
                    case Zone.River riverZone -> {
                        if (riverZone.hasLake()) {
                            riverBuilder.addSingleton(riverZone, zoneTab[zone.localId()] - 1);
                        } else {
                            riverBuilder.addSingleton(riverZone, zoneTab[zone.localId()]);
                        }
                        waterBuilder.addSingleton(riverZone, zoneTab[zone.localId()]);
                    }
                    case Zone.Lake waterZone -> waterBuilder.addSingleton(waterZone, zoneTab[zone.localId()]);
                }
            }

            for (Zone zone : tile.zones()) {
                if (zone instanceof Zone.River river && river.hasLake()) {
                    waterBuilder.union(river, river.lake());
                }
            }
        }

        /**
         * Connects the two given tile edges.
         *
         * @param s1 The first edge.
         * @param s2 The second edge.
         * @author Imad Bechlaghem (375401)
         */
        public void connectSides(TileSide s1, TileSide s2) {

            switch (s1) {
                case TileSide.Meadow(Zone.Meadow ms1) when s2 instanceof TileSide.Meadow(Zone.Meadow ms2) ->
                        meadowBuilder.union(ms1, ms2);
                case TileSide.Forest(Zone.Forest fs1) when s2 instanceof TileSide.Forest(Zone.Forest fs2) ->
                        forestBuilder.union(fs1, fs2);
                case TileSide.River(Zone.Meadow L1, Zone.River rs1, Zone.Meadow R1) when s2 instanceof TileSide.River(
                        Zone.Meadow L2, Zone.River rs2, Zone.Meadow R2
                ) -> {
                    meadowBuilder.union(L1, R2);
                    meadowBuilder.union(L2, R1);
                    riverBuilder.union(rs1, rs2);
                    waterBuilder.union(rs1, rs2);
                }
                default -> throw new IllegalArgumentException();

            }
        }

        /**
         * Adds an initial occupant to a zone.
         *
         * @param player       The player.
         * @param occupantKind The kind of occupant.
         * @param occupiedZone The occupied zone.
         * @author Imad Bechlaghem (375401)
         */
        public void addInitialOccupant(PlayerColor player, Occupant.Kind occupantKind, Zone occupiedZone) {
            switch (occupiedZone) {
                case Zone.Forest forestZone when occupantKind == Occupant.Kind.PAWN ->
                        forestBuilder.addInitialOccupant(forestZone, player);
                case Zone.Meadow meadowZone when occupantKind == Occupant.Kind.PAWN ->
                        meadowBuilder.addInitialOccupant(meadowZone, player);
                case Zone.River riverZone when occupantKind == Occupant.Kind.PAWN ->
                        riverBuilder.addInitialOccupant(riverZone, player);
                case Zone.River riverZone when occupantKind == Occupant.Kind.HUT -> {
                    Preconditions.checkArgument(!riverZone.hasLake());
                    waterBuilder.addInitialOccupant(riverZone, player);
                }
                case Zone.Lake lakeZone when occupantKind == Occupant.Kind.HUT ->
                        waterBuilder.addInitialOccupant(lakeZone, player);
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Removes a pawn from a zone.
         *
         * @param player       The player.
         * @param occupiedZone The occupied zone.
         * @author Imad Bechlaghem (375401)
         */
        public void removePawn(PlayerColor player, Zone occupiedZone) {
            switch (occupiedZone) {
                case Zone.Forest forestZone -> forestBuilder.removeOccupant(forestZone, player);
                case Zone.Meadow meadowZone -> meadowBuilder.removeOccupant(meadowZone, player);
                case Zone.River riverZone -> riverBuilder.removeOccupant(riverZone, player);
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Clears all gatherers from a forest zone.
         *
         * @param forest The forest zone.
         * @author Tino Bianconi (379733)
         */
        public void clearGatherers(Area<Zone.Forest> forest) {
            forestBuilder.removeAllOccupantsOf(forest);
        }

        /**
         * Clears all fishers from a river zone.
         *
         * @param river The river zone.
         * @author Tino Bianconi (379733)
         */

        public void clearFishers(Area<Zone.River> river) {
            riverBuilder.removeAllOccupantsOf(river);
        }

        /**
         * Constructs the partitions of zones.
         *
         * @return The constructed partitions of zones.
         * @author Tino Bianconi (379733)
         */

        public ZonePartitions build() {
            return new ZonePartitions(
                    forestBuilder.build(),
                    meadowBuilder.build(),
                    riverBuilder.build(),
                    waterBuilder.build());
        }
    }
}
