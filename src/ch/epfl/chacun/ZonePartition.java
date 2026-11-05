package ch.epfl.chacun;

import java.util.*;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents a partition of zones, where each zone is associated with one or more areas.
 *
 * @param <Z> The type of zone associated with this partition.
 * @author Imad Bechlaghem (375401)
 * @author Tino Bianconi (379733)
 */

public record ZonePartition<Z extends Zone>(Set<Area<Z>> areas) {

    /**
     * Constructor of the ZonePartition class.
     *
     * @param areas The areas of this partition.
     * @author Tino Bianconi (379733)
     */
    public ZonePartition {
        areas = Set.copyOf(areas);
    }

    /**
     * Default constructor of the ZonePartition class, initializing an empty partition.
     *
     * @author Tino Bianconi (379733)
     */
    public ZonePartition() {
        this(new HashSet<>());
    }

    /**
     * Returns the area containing the specified zone.
     *
     * @param zone The zone to search for the area.
     * @return The area containing the specified zone.
     * @throws IllegalArgumentException if no area contains the specified zone.
     * @author Tino Bianconi (379733)
     */

    public Area<Z> areaContaining(Z zone) {
        for (Area<Z> area : areas) {
            if (area.zones().contains(zone)) {
                return area;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Searches for the area containing the specified zone from a set of areas.
     *
     * @param zone  The zone to search for the area.
     * @param areas The set of areas to search within.
     * @return The area containing the specified zone.
     * @throws IllegalArgumentException if no area contains the specified zone.
     * @author Tino Bianconi (379733)
     */

    private static <Z extends Zone> Area<Z> areaContaining(Z zone, Set<Area<Z>> areas) {
        return new ZonePartition<>(areas).areaContaining(zone);
    }

    /**
     * Builder for creating and manipulating a partition of zones.
     *
     * @param <Z> The type of zone associated with this partition.
     * @author Tino Bianconi (379733)
     * @author Imad Bechlaghem (375401)
     */

    public static final class Builder<Z extends Zone> {
        private final Set<Area<Z>> areas;

        /**
         * Builder constructor taking an existing partition as a model.
         *
         * @param zonePartition The existing zone partition to use as a model.
         * @author Tino Bianconi (379733)
         */

        public Builder(ZonePartition<Z> zonePartition) {
            this.areas = new HashSet<>(zonePartition.areas);
        }

        /**
         * Adds a zone as a single area to the partition.
         *
         * @param zone            The zone to add.
         * @param openConnections The number of open connections of the area.
         * @author Imad Bechlaghem (375401)
         */

        public void addSingleton(Z zone, int openConnections) {
            Set<Z> zones = Set.of(zone);
            areas.add(new Area<>(zones, new ArrayList<>(), openConnections));
        }

        /**
         * Adds an initial occupant to a specified zone in one of the areas of the partition.
         *
         * @param zone  The zone to add the initial occupant to.
         * @param color The color of the initial occupant.
         * @author Imad Bechlaghem (375401)
         */

        public void addInitialOccupant(Z zone, PlayerColor color) {
            Area<Z> newAreaWithOccupant = areaContaining(zone, areas).withInitialOccupant(color);
            areas.remove(areaContaining(zone, areas));
            areas.add(newAreaWithOccupant);
        }

        /**
         * Removes a specific occupant from a specified zone in one of the areas of the partition.
         *
         * @param zone  The zone from which the occupant should be removed.
         * @param color The color of the occupant to remove.
         * @author Tino Bianconi (379733)
         */

        public void removeOccupant(Z zone, PlayerColor color) {
            Area<Z> newAreaWithoutOccupant = areaContaining(zone, areas).withoutOccupant(color);
            areas.remove(areaContaining(zone, areas));
            areas.add(newAreaWithoutOccupant);
        }

        /**
         * Removes all occupants from a specified area of the partition.
         *
         * @param area The area from which the occupants should be removed.
         * @throws IllegalArgumentException if the specified area does not exist in the partition.
         * @author Tino Bianconi (379733)
         */

        public void removeAllOccupantsOf(Area<Z> area) {
            checkArgument(areas.contains(area));
            Area<Z> newAreaWithoutOccupant = area.withoutOccupants();
            areas.remove(area);
            areas.add(newAreaWithoutOccupant);
        }

        /**
         * Merges two areas containing the specified zones into a single area in the partition.
         *
         * @param zone1 The first zone to merge.
         * @param zone2 The second zone to merge.
         * @author Tino Bianconi (379733)
         */

        public void union(Z zone1, Z zone2) {
            Area<Z> area1 = areaContaining(zone1, areas);
            Area<Z> area2 = areaContaining(zone2, areas);
            Area<Z> newArea = area1.connectTo(area2);
            areas.remove(area1);
            areas.remove(area2);
            areas.add(newArea);
        }

        /**
         * Constructs the partition of zones from the configured areas.
         *
         * @return The constructed partition of zones.
         * @author Tino Bianconi (379733)
         */
        public ZonePartition<Z> build() {
            return new ZonePartition<>(areas);
        }
    }
}
