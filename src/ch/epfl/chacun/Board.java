package ch.epfl.chacun;

import java.util.*;

/**
 * Represents a game board with tiles, zones, occupants, and animals.
 *
 * @author Imad Bechlaghem (375401)
 * @author Tino Bianconi (379733)
 */
public final class Board {
    /**
     * An array of placed tiles on the board.
     */
    private final PlacedTile[] tiles;

    /**
     * An array of indices of tiles.
     */
    private final int[] index;

    /**
     * Represents the partitions of different zones on the board.
     */
    private final ZonePartitions placedPartitions;

    /**
     * A set of cancelled animals on the board.
     */
    private final Set<Animal> cancelledAnimals;

    /**
     * Maximum reach from the center of the board.
     */
    public static final int REACH = 12;
    private static final int BOARD_LENGTH = 25;
    private static final int BOARD_SIZE = 625;

    /**
     * Represents an empty board.
     */
    public static Board EMPTY = new Board(
            new PlacedTile[BOARD_SIZE],
            new int[0],
            ZonePartitions.EMPTY,
            Set.of());

    /**
     * Constructs a board with the provided components.
     *
     * @param tiles             An array of placed tiles.
     * @param index             An array of indices of tiles.
     * @param placedPartitions  Partitions of different zones on the board.
     * @param cancelledAnimals  Set of cancelled animals on the board.
     * @author Imad Bechlaghem (375401)
     */
    public Board(PlacedTile[] tiles, int[] index, ZonePartitions placedPartitions, Set<Animal> cancelledAnimals) {
        this.tiles = tiles;
        this.index = index;
        this.placedPartitions = placedPartitions;
        this.cancelledAnimals = cancelledAnimals;
    }

    /**
     * Retrieves the tile at a given position on the board.
     *
     * @param pos The position of the tile.
     * @return The tile at the specified position, or null if no tile exists.
     * @author Imad Bechlaghem (375401)
     */
    public PlacedTile tileAt(Pos pos) {
        if (! (0 <= posToIndex(pos) && posToIndex(pos) <= 624)) {
            return null;
        }
        return tiles[posToIndex(pos)];
    }

    /**
     * Retrieves the tile with the given ID.
     *
     * @param tileId The ID of the tile.
     * @return The tile with the specified ID.
     * @throws IllegalArgumentException If no tile exists with the given ID.
     * @author Imad Bechlaghem (375401)
     */
    public PlacedTile tileWithId(int tileId) {
        for (int i : index) {
            if (tiles[i].id() == tileId) {
                return tiles[i];
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Retrieves the set of animals that have been cancelled on the board.
     *
     * @return The set of cancelled animals on the board.
     * @author Imad Bechlaghem (375401)
     */
    public Set<Animal> cancelledAnimals() {
        return new HashSet<>(cancelledAnimals);
    }

    /**
     * Retrieves a set of occupants on the board.
     *
     * @return A set of occupants on the board.
     * @author Imad Bechlaghem (375401)
     */
    public Set<Occupant> occupants() {
        Set<Occupant> occupants = new HashSet<>();

        for (int i : index) {
            if (tiles[i].occupant() != null) {
                occupants.add(tiles[i].occupant());
            }
        }
        return occupants;
    }

    /**
     * Retrieves the area of the forest containing the specified forest zone.
     *
     * @param forest The forest zone.
     * @return The area containing the specified forest zone.
     * @author Imad Bechlaghem (375401)
     */
    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        return placedPartitions
                .forests()
                .areaContaining(forest);
    }

    /**
     * Retrieves the area of the meadow containing the specified meadow zone.
     *
     * @param meadow The meadow zone.
     * @return The area containing the specified meadow zone.
     * @author Imad Bechlaghem (375401)
     */
    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        return placedPartitions
                .meadows()
                .areaContaining(meadow);
    }

    /**
     * Retrieves the area of the river containing the specified river zone.
     *
     * @param riverZone The river zone.
     * @return The area containing the specified river zone.
     * @author Imad Bechlaghem (375401)
     */
    public Area<Zone.River> riverArea(Zone.River riverZone) {
        return placedPartitions
                .rivers()
                .areaContaining(riverZone);
    }

    /**
     * Retrieves the area of the water system containing the specified water zone.
     *
     * @param water The water zone.
     * @return The area containing the specified water zone.
     * @author Imad Bechlaghem (375401)
     */
    public Area<Zone.Water> riverSystemArea(Zone.Water water) {
        return placedPartitions
                .riverSystems()
                .areaContaining(water);
    }

    /**
     * Retrieves a set of areas of meadows on the board.
     *
     * @return A set of areas of meadows on the board.
     * @author Tino Bianconi (379733)
     */
    public Set<Area<Zone.Meadow>> meadowAreas() {
        return placedPartitions
                .meadows()
                .areas();
    }

    /**
     * Retrieves a set of areas of water systems on the board.
     *
     * @return A set of areas of water systems on the board.
     * @author Tino Bianconi (379733)
     */
    public Set<Area<Zone.Water>> riverSystemAreas() {
        return placedPartitions
                .riverSystems()
                .areas();
    }

    /**
     * Retrieves the area of the meadow adjacent to the specified position and containing the specified meadow zone.
     *
     * @param pos        The position to check for adjacency.
     * @param meadowZone The meadow zone to search for.
     * @return The area of the adjacent meadow containing the specified meadow zone.
     * @author Imad Bechlaghem (375401)
     */
    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        Set<PlacedTile> adjacentTiles = tilesAround(pos);
        Area<Zone.Meadow> areaWithMeadow = meadowArea(meadowZone);
        Set<Zone.Meadow> zones = new HashSet<>();

        for (Zone.Meadow meadow : areaWithMeadow.zones()) {
            for (PlacedTile potentialTile : adjacentTiles) {
                if (potentialTile != null && potentialTile.tile().zones().contains(meadow)) {
                    zones.add(meadow);
                }
            }
        }

        return new Area<>(
                zones,
                areaWithMeadow.occupants(),
                0);
    }

    /**
     * Retrieves the count of a specific type of occupant owned by a specific player on the board.
     *
     * @param player       The player color.
     * @param occupantKind The kind of occupant.
     * @return The count of the specified type of occupant owned by the player.
     * @author Imad Bechlaghem (375401)
     */
    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        int count = 0;

        for (int i : index) {
            if (tiles[i].occupant() != null
                    && tiles[i].occupant().kind().equals(occupantKind)
                    && tiles[i].placer().equals(player)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retrieves the set of insertion positions available on the board.
     *
     * @return A set of positions where tiles can be inserted.
     * @author Imad Bechlaghem (375401)
     */
    public Set<Pos> insertionPositions() {
        Set<Pos> insertionPositions = new HashSet<>();

        for (PlacedTile tile : tiles) {
            if (tile != null) {
                for (Direction direction : Direction.ALL) {
                    Pos neighborPos = tile.pos().neighbor(direction);
                    PlacedTile neighbor = tileAt(neighborPos);

                    if (neighbor == null
                            && Math.abs(neighborPos.x()) <= REACH
                            && Math.abs(neighborPos.y()) <= REACH) {

                        insertionPositions.add(neighborPos);
                    }
                }
            }
        }
        return insertionPositions;
    }

    /**
     * Retrieves the last placed tile on the board.
     *
     * @return The last placed tile on the board, or null if no tile has been placed yet.
     * @author Tino Bianconi (379733)
     */
    public PlacedTile lastPlacedTile() {
        if (index.length != 0) {
            return tiles[index[index.length - 1]];
        }
        return null;
    }

    /**
     * Retrieves a set of forest areas closed by the last placed tile on the board.
     *
     * @return A set of forest areas closed by the last placed tile.
     * @author Imad Bechlaghem (375401)
     */
    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        PlacedTile lastTile = lastPlacedTile();
        Set<Area<Zone.Forest>> forests = new HashSet<>();

        if (lastTile == null) {
            return Set.of();
        }

        for (Zone.Forest forest : lastTile.forestZones()) {
            Area<Zone.Forest> forestArea = forestArea(forest);
            if (forestArea.isClosed()) {
                forests.add(forestArea);
            }
        }
        return forests;
    }

    /**
     * Retrieves a set of river areas closed by the last placed tile on the board.
     *
     * @return A set of river areas closed by the last placed tile.
     * @author Imad Bechlaghem (375401)
     */
    public Set<Area<Zone.River>> riversClosedByLastTile() {
        Set<Area<Zone.River>> rivers = new HashSet<>();

        if (lastPlacedTile() == null) {
            return rivers;
        }

        for (Zone.River river : lastPlacedTile().riverZones()) {
            Area<Zone.River> riverArea = riverArea(river);
            if (riverArea.isClosed()) {
                rivers.add(riverArea);
            }
        }
        return rivers;
    }

    /**
     * Checks if a tile can be added to the board.
     *
     * @param tile The tile to be added.
     * @return True if the tile can be added, false otherwise.
     * @author Imad Bechlaghem (375401)
     */
    public boolean canAddTile(PlacedTile tile) {
        PlacedTile upTile = tileAt(new Pos(tile.pos().x(), tile.pos().y() - 1));
        PlacedTile downTile = tileAt(new Pos(tile.pos().x(), tile.pos().y() + 1));
        PlacedTile leftTile = tileAt(new Pos(tile.pos().x() - 1, tile.pos().y()));
        PlacedTile rightTile = tileAt(new Pos(tile.pos().x() + 1, tile.pos().y()));

        TileSide upTileSide = upTile == null ? null : upTile.side(Direction.S);
        TileSide downTileSide = downTile == null ? null : downTile.side(Direction.N);
        TileSide leftTileSide = leftTile == null ? null : leftTile.side(Direction.E);
        TileSide rightTileSide = rightTile == null ? null : rightTile.side(Direction.W);

        boolean sidesConnect = ((upTileSide == null || upTileSide.isSameKindAs(tile.side(Direction.N))))
                && ((downTileSide == null || downTileSide.isSameKindAs(tile.side(Direction.S))))
                && ((leftTileSide == null || leftTileSide.isSameKindAs(tile.side(Direction.W))))
                && ((rightTileSide == null || rightTileSide.isSameKindAs(tile.side(Direction.E))));

        return (insertionPositions()
                .contains(tile.pos()) && sidesConnect);
    }

    /**
     * Checks if a tile could be placed on the board.
     *
     * @param tile The tile to be placed.
     * @return True if the tile could be placed, false otherwise.
     * @author Imad Bechlaghem (375401)
     */
    public boolean couldPlaceTile(Tile tile) {
        for (Pos pos : insertionPositions()) {
            for (Rotation rotation : Rotation.ALL) {
                PlacedTile potentialTile = new PlacedTile(tile, null, rotation, pos);

                if (canAddTile(potentialTile)) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Adds a new tile to the board.
     *
     * @param tile The tile to be added.
     * @return A new board with the added tile.
     * @author Imad Bechlaghem (375401)
     */
    public Board withNewTile(PlacedTile tile) {
        Preconditions.checkArgument(index.length == 0 || canAddTile(tile));

        PlacedTile[] newTiles = tiles.clone();
        int[] newIndex = Arrays.copyOf(index, index.length + 1);

        newTiles[posToIndex(tile.pos())] = tile;

        ZonePartitions.Builder builder = new ZonePartitions.Builder(placedPartitions);
        builder.addTile(tile.tile());

        for (Direction direction : Direction.ALL) {
            PlacedTile neighbor = tileAt(tile
                    .pos()
                    .neighbor(direction));

            if (neighbor != null) {
                builder.connectSides(tile.side(direction), neighbor.side(direction.opposite()));
            }
        }
        newIndex[newIndex.length - 1] = posToIndex(tile.pos());

        return new Board(
                newTiles,
                newIndex,
                builder.build(),
                cancelledAnimals);
    }


    /**
     * Adds an occupant to the board.
     *
     * @param occupant The occupant to be added.
     * @return A new board with the added occupant.
     * @throws IllegalArgumentException If the specified tile already has an occupant.
     * @author Imad Bechlaghem (375401)
     */
    public Board withOccupant(Occupant occupant) {
        Preconditions.checkArgument(tileWithId(Zone.tileId(occupant.zoneId())).occupant() == null);
        PlacedTile[] newTiles = tiles.clone();
        ZonePartitions.Builder builder = new ZonePartitions.Builder(placedPartitions);

        PlacedTile oldTile = tileWithId(Zone.tileId(occupant.zoneId()));
        PlacedTile newTile = oldTile.withOccupant(occupant);

        newTiles[tileIndex(oldTile, index)] = newTile;
        builder.addInitialOccupant(newTile.placer(), occupant.kind(), newTile.zoneWithId(occupant.zoneId()));

        return new Board(
                newTiles,
                index,
                builder.build(),
                cancelledAnimals);
    }

    /**
     * Removes an occupant from the board.
     *
     * @param occupant The occupant to be removed.
     * @return A new board without the specified occupant.
     * @author Imad Bechlaghem (375401)
     */
    public Board withoutOccupant(Occupant occupant) {
        PlacedTile[] newTiles = tiles.clone();
        ZonePartitions.Builder builder = new ZonePartitions.Builder(placedPartitions);

        PlacedTile oldTile = tileWithId(Zone.tileId(occupant.zoneId()));
        PlacedTile newTile = oldTile.withNoOccupant();

        newTiles[tileIndex(oldTile, index)] = newTile;
        builder.removePawn(oldTile.placer(), newTile.zoneWithId(occupant.zoneId()));

        return new Board(
                newTiles,
                index,
                builder.build(),
                cancelledAnimals);
    }

    /**
     * Removes gatherers or fishers from specified forest and river areas respectively.
     *
     * @param forests The set of forest areas from which gatherers will be removed.
     * @param rivers  The set of river areas from which fishers will be removed.
     * @return A new board without the specified gatherers or fishers.
     * @author Tino Bianconi (379733)
     */
    public Board withoutGatherersOrFishersIn(Set<Area<Zone.Forest>> forests, Set<Area<Zone.River>> rivers) {
        PlacedTile[] tilesModified = Arrays.copyOf(tiles, tiles.length);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(placedPartitions);

        for (Area<Zone.River> river : rivers) {
            builder.clearFishers(river);
            for (Zone.River zone : river.zones()) {
                PlacedTile newPlacedTile = tileWithId(zone.tileId());
                if ((newPlacedTile.occupant() != null) && (newPlacedTile.occupant().zoneId() == zone.id() && newPlacedTile.occupant().kind() != Occupant.Kind.HUT)) {
                    tilesModified[posToIndex(newPlacedTile.pos())] = newPlacedTile.withNoOccupant();
                }
            }
        }
        for (Area<Zone.Forest> forest : forests) {
            builder.clearGatherers(forest);
            for (Zone.Forest zone : forest.zones()) {
                PlacedTile newPlacedTile = tileWithId(zone.tileId());
                if ((newPlacedTile.occupant() != null) && (newPlacedTile.occupant().zoneId() == zone.id()) && newPlacedTile.occupant().kind() != Occupant.Kind.HUT) {
                    tilesModified[posToIndex(newPlacedTile.pos())] = newPlacedTile.withNoOccupant();
                }
            }
        }
        return new Board(
                tilesModified,
                index,
                builder.build(),
                cancelledAnimals);
    }


    /**
     * Adds more cancelled animals to the board.
     *
     * @param newlyCancelledAnimals The set of newly cancelled animals to be added.
     * @return A new board with the added cancelled animals.
     *
     * @author Tino Bianconi (379733)
     */
    public Board withMoreCancelledAnimals(Set<Animal> newlyCancelledAnimals) {
        PlacedTile[] newTiles = tiles.clone();
        int[] newIndex = index.clone();
        ZonePartitions.Builder newBuilder = new ZonePartitions.Builder(placedPartitions);
        Set<Animal> newCancelledAnimals = new HashSet<>(cancelledAnimals);

        newCancelledAnimals.addAll(newlyCancelledAnimals);

        return new Board(
                newTiles,
                newIndex,
                newBuilder.build(),
                Collections.unmodifiableSet(newCancelledAnimals));
    }

    /**
     * Checks if two boards are equal.
     *
     * @param object The object to compare.
     * @return True if the boards are equal, false otherwise.
     * @author Imad Bechlaghem (375401)
     */
    public boolean equals(Object object) {
        if (object instanceof Board newObject) {
            return Arrays.equals(tiles, newObject.tiles)
                    && Arrays.equals(index, newObject.index)
                    && placedPartitions.equals(newObject.placedPartitions)
                    && cancelledAnimals.equals(newObject.cancelledAnimals);
        } else {
            return false;
        }
    }

    /**
     * Calculates the hash code of the board.
     *
     * @return The hash code of the board.
     * @author Imad Bechlaghem (375401)
     */
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(tiles),
                Arrays.hashCode(index),
                placedPartitions,
                cancelledAnimals);
    }

    /**
     * Retrieves the index of a tile within the tile array.
     *
     * @param tile The tile to find the index of.
     * @param index  The array of tiles to search in.
     * @return The index of the tile within the array.
     * @throws IllegalArgumentException If the tile is not found in the array.
     * @author Imad Bechlaghem (375401)
     */
    private int tileIndex(PlacedTile tile, int[] index) {
        for (int i : index) {
            if (i == posToIndex(tile.pos())) {
                return i;
            }
        }
        throw new IllegalArgumentException("Tile not found in the array.");
    }

    /**
     * Retrieves a set of tiles surrounding a given position.
     *
     * @param pos The position to check for surrounding tiles.
     * @return A set of tiles surrounding the given position.
     * @author Imad Bechlaghem (375401)
     */
    private Set<PlacedTile> tilesAround(Pos pos) {
        Set<PlacedTile> tileSet = new HashSet<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                tileSet.add(tileAt(new Pos(pos.x() + x , pos.y() + y )));
            }
        }

        return tileSet;
    }

    /**
     * Converts a position to its corresponding index in the tile array.
     *
     * @param pos The position to convert.
     * @return The index of the position in the tile array.
     * @author Imad Bechlaghem (375401)
     */
    private int posToIndex(Pos pos) {
        return (pos.x() + REACH) + BOARD_LENGTH * (pos.y() + REACH);
    }


}

