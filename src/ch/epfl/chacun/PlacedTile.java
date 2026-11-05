package ch.epfl.chacun;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a tile placed on the game board by a player.
 *
 * @author Tino Bianconi (379733)
 * @author Imad Bechlaghem (375401)
 */
public record PlacedTile(
        Tile tile,
        PlayerColor placer,
        Rotation rotation,
        Pos pos,
        Occupant occupant) {

    /**
     * Constructs a PlacedTile with non-null tile, rotation, and position.
     *
     * @param tile     The tile to place.
     * @param rotation The rotation of the placed tile.
     * @param pos      The position where the tile is placed.
     * @throws NullPointerException if tile, rotation, or pos is null.
     * @author Tino Bianconi (379733)
     */
    public PlacedTile {
        Objects.requireNonNull(tile);
        Objects.requireNonNull(rotation);
        Objects.requireNonNull(pos);
    }

    /**
     * Constructs a PlacedTile with non-null tile, rotation, position, and optional occupant.
     *
     * @param tile     The tile to place.
     * @param placer   The player who placed the tile.
     * @param rotation The rotation of the placed tile.
     * @param pos      The position where the tile is placed.
     * @author Tino Bianconi (379733)
     */
    public PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos) {
        this(tile, placer, rotation, pos, null);
    }

    /**
     * Retrieves the ID of the tile.
     *
     * @return The ID of the tile.
     * @author Tino Bianconi (379733)
     */
    public int id() {
        return tile.id();
    }

    /**
     * Retrieves the kind of the tile.
     *
     * @return The kind of the tile.
     * @author Tino Bianconi (379733)
     */
    public Tile.Kind kind() {
        return tile.kind();
    }

    /**
     * Retrieves the side of the tile in the specified direction after applying the rotation.
     *
     * @param direction The direction of the side to retrieve.
     * @return The side of the tile in the specified direction.
     * @author Tino Bianconi (379733)
     */
    public TileSide side(Direction direction) {
        return tile.sides().get(direction.rotated(rotation.negated()).ordinal());
    }

    /**
     * Retrieves the zone with the specified ID on the tile.
     *
     * @param id The ID of the zone to retrieve.
     * @return The zone with the specified ID.
     * @throws IllegalArgumentException if no zone with the specified ID is found.
     * @author Tino Bianconi (379733)
     */
    public Zone zoneWithId(int id) {
        for (Zone zone : tile().zones()) {
            if (zone.id() == id) {
                return zone;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Retrieves the zone with a special power on the tile, if any.
     *
     * @return The zone with a special power, or null if none.
     * @author Tino Bianconi (379733)
     */
    public Zone specialPowerZone() {
        for (Zone zone : tile.zones()) {
            if (zone.specialPower() != null) {
                return zone;
            }
        }
        return null;
    }

    /**
     * Retrieves all forest zones on the tile.
     *
     * @return A set containing all forest zones on the tile.
     * @author Tino Bianconi (379733)
     */
    public Set<Zone.Forest> forestZones() {
        Set<Zone.Forest> forests = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.Forest forest) {
                forests.add(forest);
            }
        }
        return forests;
    }

    /**
     * Retrieves all meadow zones on the tile.
     *
     * @return A set containing all meadow zones on the tile.
     * @author Tino Bianconi (379733)
     */
    public Set<Zone.Meadow> meadowZones() {
        Set<Zone.Meadow> meadows = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.Meadow meadow) {
                meadows.add(meadow);
            }
        }
        return meadows;
    }

    /**
     * Retrieves all river zones on the tile.
     *
     * @return A set containing all river zones on the tile.
     * @author Tino Bianconi (379733)
     */
    public Set<Zone.River> riverZones() {
        Set<Zone.River> rivers = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.River river) {
                rivers.add(river);
            }
        }
        return rivers;
    }

    /**
     * Retrieves the potential occupants (like huts or pawns) for the tile based on its zones.
     *
     * @return A set containing the potential occupants for the tile.
     * @author Imad Bechlaghem (375401)
     */
    public Set<Occupant> potentialOccupants() {
        Set<Occupant> occupants = new HashSet<>();
        if (placer == null) {
            return occupants;
        } else {
            for (Zone sideZone : tile.sideZones()) {
                occupants.add(new Occupant(Occupant.Kind.PAWN, sideZone.id()));
                if (sideZone instanceof Zone.River river) {
                    occupants.add(new Occupant(Occupant.Kind.HUT, river.hasLake()
                            ? river.lake().id()
                            : river.id()));
                }
            }
        }
        return occupants;
    }

    /**
     * Creates a new PlacedTile with the specified occupant added.
     *
     * @param occupant The occupant to add.
     * @return A new PlacedTile with the specified occupant.
     * @throws IllegalArgumentException if the tile already has an occupant.
     */
    public PlacedTile withOccupant(Occupant occupant) {
        if (this.occupant == null) {
            return new PlacedTile(tile, placer, rotation, pos, occupant);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a new PlacedTile with no occupant.
     *
     * @return A new PlacedTile with no occupant.
     * @author Tino Bianconi (379733)
     */
    public PlacedTile withNoOccupant() {
        return new PlacedTile(tile, placer, rotation, pos);
    }

    /**
     * Retrieves the ID of the zone occupied by the specified kind of occupant.
     *
     * @param occupantKind The kind of occupant to check.
     * @return The ID of the zone occupied by the specified kind of occupant, or -1 if not occupied.
     * @author Tino Bianconi (379733)
     */
    public int idOfZoneOccupiedBy(Occupant.Kind occupantKind) {
        return (occupant != null && occupantKind == occupant.kind())
                ? occupant.zoneId()
                : -1;
    }
}