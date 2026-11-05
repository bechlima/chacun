package ch.epfl.chacun;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a collection of decks containing different types of tiles: START, NORMAL, and MENHIR.
 *
 * @author Imad Bechlaghem (375401)
 * @author Tino Bianconi (379733)
 */
public record TileDecks(
        List<Tile> startTiles,
        List<Tile> normalTiles,
        List<Tile> menhirTiles) {

    /**
     * Constructs a TileDecks instance with immutable copies of the provided tile lists.
     *
     * @param startTiles  The list of start tiles.
     * @param normalTiles The list of normal tiles.
     * @param menhirTiles The list of menhir tiles.
     * @author Imad Bechlaghem (375401)
     */
    public TileDecks {
        startTiles = List.copyOf(startTiles);
        normalTiles = List.copyOf(normalTiles);
        menhirTiles = List.copyOf(menhirTiles);
    }

    /**
     * Returns a list of tiles of the specified kind.
     *
     * @param kind The kind of tiles to retrieve.
     * @return A list of tiles of the specified kind.
     * @throws IllegalArgumentException if the specified kind is unknown.
     * @author Imad Bechlaghem (375401)
     */
    private List<Tile> KindDecks(Tile.Kind kind) {
        return switch (kind) {
            case START -> startTiles;
            case NORMAL -> normalTiles;
            case MENHIR -> menhirTiles;
        };
    }

    /**
     * Retrieves the size of the specified deck.
     *
     * @param kind The kind of deck to query.
     * @return The size of the specified deck.
     * @author Imad Bechlaghem (375401)
     */
    public int deckSize(Tile.Kind kind) {
        return switch (kind) {
            case START -> startTiles.size();
            case NORMAL -> normalTiles.size();
            case MENHIR -> menhirTiles.size();
        };
    }

    /**
     * Retrieves the top tile from the specified deck.
     *
     * @param kind The kind of deck to draw from.
     * @return The top tile from the specified deck, or null if the deck is empty.
     * @author Imad Bechlaghem (375401)
     */
    public Tile topTile(Tile.Kind kind) {

        return switch (kind) {
            case START -> {
                if (startTiles.isEmpty()) {
                    yield null;
                }
                yield startTiles.get(0);
            }

            case NORMAL -> {
                if (normalTiles.isEmpty()) {
                    yield null;
                }
                yield normalTiles.get(0);
            }
            case MENHIR -> {
                if (menhirTiles.isEmpty()) {
                    yield null;
                }
                yield menhirTiles.get(0);
            }
        };
    }

    /**
     * Removes the top tile from the specified deck and returns a new TileDecks instance.
     *
     * @param kind The kind of deck to draw from.
     * @return A new TileDecks instance with the top tile removed from the specified deck.
     * @throws IllegalArgumentException if the specified deck is empty.
     * @author Tino Bianconi (379733)
     */
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        switch (kind) {
            case START -> {
                Preconditions.checkArgument(!startTiles.isEmpty());

                return new TileDecks(startTiles.subList(1, deckSize(kind)), normalTiles, menhirTiles);
            }
            case NORMAL -> {
                Preconditions.checkArgument(!normalTiles.isEmpty());

                return new TileDecks(startTiles, normalTiles.subList(1, deckSize(kind)), menhirTiles);
            }
            case MENHIR -> {
                Preconditions.checkArgument(!menhirTiles.isEmpty());

                return new TileDecks(startTiles, normalTiles, menhirTiles.subList(1, deckSize(kind)));
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Draws tiles from the specified deck until a predicate is satisfied and returns a new TileDecks instance.
     *
     * @param kind      The kind of deck to draw from.
     * @param predicate The predicate to satisfy.
     * @return A new TileDecks instance with the drawn tiles removed from the specified deck.
     * @author Tino Bianconi (379733)
     */
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        TileDecks updatedTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        while (!updatedTileDecks.KindDecks(kind).isEmpty() && !predicate.test(updatedTileDecks.topTile(kind))) {
            updatedTileDecks = updatedTileDecks.withTopTileDrawn(kind);
        }
        return updatedTileDecks;
    }


}

