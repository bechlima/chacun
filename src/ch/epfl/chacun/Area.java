package ch.epfl.chacun;

import java.util.*;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents an area containing specific zones, occupants, and open connections.
 *
 * @param <Z> The type of zone contained within this area.
 * @author Imad Bechlaghem (375401)
 * @author Tino Bianconi (379733)
 */
public record Area<Z extends Zone>(
        Set<Z> zones,
        List<PlayerColor> occupants,
        int openConnections
) {

    /**
     * Constructor of the Area class.
     *
     * @param zones           The zones contained within this area.
     * @param occupants       The occupants of these zones.
     * @param openConnections The number of open connections.
     * @throws IllegalArgumentException if the number of open connections is negative.
     * @author Imad Bechlaghem (375401)
     */
    public Area {
        Preconditions.checkArgument(openConnections >= 0);

        zones = Set.copyOf(zones);
        List<PlayerColor> sortedOccupants = new ArrayList<>(occupants);
        Collections.sort(sortedOccupants);
        occupants = List.copyOf(sortedOccupants);
    }

    /**
     * Checks if a forest area contains a menhir.
     *
     * @param forest The forest area to check.
     * @return true if the area contains a menhir, otherwise false.
     * @author Imad Bechlaghem (375401)
     */
    public static boolean hasMenhir(Area<Zone.Forest> forest) {
        for (Zone.Forest forestZone : forest.zones) {
            if (forestZone.kind() == Zone.Forest.Kind.WITH_MENHIR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the number of mushroom groups in a forest area.
     *
     * @param forest The forest area to inspect.
     * @return The number of mushroom groups.
     * @author Imad Bechlaghem (375401)
     */
    public static int mushroomGroupCount(Area<Zone.Forest> forest) {
        int mushroomCount = 0;
        for (Zone.Forest forestZone : forest.zones) {
            if (forestZone.kind() == Zone.Forest.Kind.WITH_MUSHROOMS) {
                ++mushroomCount;
            }
        }
        return mushroomCount;
    }

    /**
     * Returns the set of animals present in a meadow area.
     *
     * @param meadow           The meadow area to inspect.
     * @param cancelledAnimals The cancelled animals to exclude.
     * @return The set of present animals.
     * @author Imad Bechlaghem (375401)
     */
    public static Set<Animal> animals(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animalSet = new HashSet<>();
        for (Zone.Meadow meadowZone : meadow.zones) {
            for (Animal animal : meadowZone.animals()) {
                if (!cancelledAnimals.contains(animal)) {
                    animalSet.add(animal);
                }
            }
        }
        return animalSet;
    }

    /**
     * Counts the total number of fish in a river area.
     *
     * @param river The river area to inspect.
     * @return The total number of fish.
     * @author Imad Bechlaghem (375401)
     */
    public static int riverFishCount(Area<Zone.River> river) {
        int fishCount = 0;
        Set<Zone.Lake> lakeSet = new HashSet<>();
        for (Zone.River riverZone : river.zones) {
            fishCount += riverZone.fishCount();
            if (riverZone.hasLake() && lakeSet.add(riverZone.lake())) {
                fishCount += riverZone.lake().fishCount();
            }
        }
        return fishCount;
    }

    /**
     * Counts the total number of fish in a river system.
     *
     * @param riverSystem The river system to inspect.
     * @return The total number of fish.
     * @author Imad Bechlaghem (375401)
     */
    public static int riverSystemFishCount(Area<Zone.Water> riverSystem) {
        int fishCount = 0;

        for (Zone.Water water : riverSystem.zones) {
            fishCount += water.fishCount();
        }
        return fishCount;
    }

    /**
     * Counts the number of lakes in a river system.
     *
     * @param riverSystem The river system to inspect.
     * @return The number of lakes.
     * @author Imad Bechlaghem (375401)
     */
    public static int lakeCount(Area<Zone.Water> riverSystem) {
        int lakeCount = 0;
        for (Zone.Water water : riverSystem.zones) {
            if (water instanceof Zone.Water.Lake) {
                lakeCount += 1;
            }
        }
        return lakeCount;
    }

    /**
     * Checks if the area is closed (i.e., without open connections).
     *
     * @return true if the area is closed, otherwise false.
     * @author Tino Bianconi (379733)
     */
    public boolean isClosed() {
        return (this.openConnections == 0);
    }

    /**
     * Checks if the area is occupied by players.
     *
     * @return true if the area is occupied, otherwise false.
     * @author Tino Bianconi (379733)
     */
    public boolean isOccupied() {
        return !this.occupants.isEmpty();
    }

    /**
     * Returns the set of majority occupants in the zone.
     *
     * @return The set of majority occupants.
     * @author Tino Bianconi (379733)
     */
    public Set<PlayerColor> majorityOccupants() {
        Set<PlayerColor> majorityOccupants = new HashSet<>();

        List<Integer> colorTab = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0));
        for (PlayerColor player : occupants) {
            int ordinal = player.ordinal();
            colorTab.set(ordinal, colorTab.get(ordinal) + 1);
        }
        int maximum = Collections.max(colorTab);

        for (int i = 0; i < colorTab.size(); i++) {
            if (colorTab.get(i) == maximum && maximum > 0) {
                majorityOccupants.add(PlayerColor.ALL.get(i));
            }
        }
        return majorityOccupants;
    }

    /**
     * Connects this area to another area.
     *
     * @param that The other area to connect to this area.
     * @return The connected area.
     * @author Tino Bianconi (379733)
     */
    public Area<Z> connectTo(Area<Z> that) {
        Area<Z> connectedArea;

        if (this.equals(that)) {
            connectedArea = new Area<>(
                    this.zones,
                    this.occupants,
                    this.openConnections - 2);
        } else {
            Set<Z> zones = new HashSet<>(this.zones);
            List<PlayerColor> occupants = new ArrayList<>(this.occupants);
            zones.addAll(that.zones);
            occupants.addAll(that.occupants);

            connectedArea = new Area<>(
                    zones,
                    occupants,
                    that.openConnections + this.openConnections - 2);
        }
        return connectedArea;
    }

    /**
     * Returns a copy of this area with an initial occupant.
     *
     * @param occupant The initial occupant to add.
     * @return A copy of this area with the initial occupant added.
     * @throws IllegalArgumentException if the area is already occupied.
     * @author Tino Bianconi (379733)
     */
    public Area<Z> withInitialOccupant(PlayerColor occupant) {
        checkArgument(!this.isOccupied());

        return new Area<>(
                this.zones,
                List.of(occupant),
                this.openConnections);
    }

    /**
     * Returns a copy of this area without a specific occupant.
     *
     * @param occupant The occupant to remove.
     * @return A copy of this area without the specified occupant.
     * @throws IllegalArgumentException if the area does not contain the specified occupant.
     * @author Tino Bianconi (379733)
     */
    public Area<Z> withoutOccupant(PlayerColor occupant) {
        checkArgument(occupants.contains(occupant));

        List<PlayerColor> updatedList = new ArrayList<>(this.occupants);
        updatedList.remove(occupant);

        return new Area<>(
                this.zones,
                updatedList,
                this.openConnections);
    }

    /**
     * Returns a copy of this area without occupants.
     *
     * @return A copy of this area without occupants.
     * @author Tino Bianconi (379733)
     */
    public Area<Z> withoutOccupants() {
        return new Area<>(
                this.zones,
                new ArrayList<>(),
                this.openConnections);
    }

    /**
     * Returns the set of tile identifiers in this area.
     *
     * @return The set of tile identifiers.
     * @author Tino Bianconi (379733)
     */
    public Set<Integer> tileIds() {
        Set<Integer> ids = new HashSet<>();
        for (Zone zone : zones) {
            ids.add(zone.tileId());
        }
        return ids;
    }

    /**
     * Returns the first area with a specified special power.
     *
     * @param specialPower The special power to search for.
     * @return The first area with the specified special power, or null if none is found.
     * @author Tino Bianconi (379733)
     */
    public Zone zoneWithSpecialPower(Zone.SpecialPower specialPower) {
        for (Zone zoneWithSpecialPower : zones) {
            if (zoneWithSpecialPower.specialPower() == specialPower) {
                return zoneWithSpecialPower;
            }
        }
        return null;
    }
}