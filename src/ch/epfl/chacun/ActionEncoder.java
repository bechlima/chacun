package ch.epfl.chacun;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for encoding and decoding game actions into Base32 strings.
 *
 * @author Imad Bechlaghem (375401)
 */
public final class ActionEncoder {

    private static final String NO_OCCUPANT = "7";

    // Private constructor to prevent instantiation
    private ActionEncoder() {}

    /**
     * Record representing a game state and its encoded 32-bit action.
     */
    public record StateAction(GameState gameState, String encoded32BitAction) {}

    /**
     * Encodes an action of placing a tile into the game state.
     *
     * @param gameState The current game state.
     * @param tile The tile to be placed.
     * @return A new StateAction with the updated game state and the encoded action.
     */
    public static StateAction withPlacedTile(GameState gameState, PlacedTile tile) {
        GameState newGameState = gameState.withPlacedTile(tile);

        List<Pos> sortedBoardPositions = insertionPositions(gameState);

        int posBits = sortedBoardPositions.indexOf(tile.pos());
        int rotationBits = tile.rotation().ordinal();

        int i = posBits << 2;
        i = i | rotationBits;
        return new StateAction(newGameState, Base32.encodeBits10(i));
    }

    /**
     * Encodes an action of adding a new occupant into the game state.
     *
     * @param gameState The current game state.
     * @param occupant The occupant to be added.
     * @return A new StateAction with the updated game state and the encoded action.
     */
    public static StateAction withNewOccupant(GameState gameState, Occupant occupant) {
        GameState newGameState = gameState.withNewOccupant(occupant);

        if (occupant == null) {
            return new StateAction(newGameState, Base32.encodeBits5(0b11111));
        }

        int zoneBits = Zone.localId(occupant.zoneId());
        int kindBit = occupant.kind().ordinal() << 4;

        int i = zoneBits;
        i = i | kindBit;

        return new StateAction(newGameState, Base32.encodeBits5(i));
    }

    /**
     * Encodes an action of removing an occupant from the game state.
     *
     * @param gameState The current game state.
     * @param occupant The occupant to be removed.
     * @return A new StateAction with the updated game state and the encoded action.
     */
    public static StateAction withOccupantRemoved(GameState gameState, Occupant occupant) {
        GameState newGameState = gameState.withOccupantRemoved(occupant);

        ArrayList<Occupant> occupantsList = occupantsList(gameState);

        if (occupant == null) {
            return new StateAction(newGameState, Base32.encodeBits5(0b11111));
        }

        int i = occupantsList.indexOf(occupant);

        return new StateAction(newGameState, Base32.encodeBits5(i));
    }

    /**
     * Decodes and applies an encoded action to the game state.
     *
     * @param gameState The current game state.
     * @param encodedString The encoded action string.
     * @return A new StateAction with the updated game state and the encoded action, or null if decoding fails.
     */
    public static StateAction decodeAndApply(GameState gameState, String encodedString) {
        try {
            return decoder(gameState, encodedString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Decodes an encoded action string and applies it to the game state.
     *
     * @param gameState The current game state.
     * @param encodedString The encoded action string.
     * @return A new StateAction with the updated game state and the encoded action.
     * @throws IllegalActionException if the encoded action is invalid.
     */
    private static StateAction decoder(GameState gameState, String encodedString) {
        if (!Base32.isValid(encodedString)) {
            throw new IllegalActionException();
        }
        int decodedString = Base32.decode(encodedString);

        switch (gameState.nextAction()) {
            case PLACE_TILE -> {
                if (encodedString.length() == 2) {
                    int tileIndex = decodedString >> 2;
                    int rotationIndex = decodedString & 0b11;

                    List<Pos> insertionPositions = insertionPositions(gameState);

                    if (tileIndex >= insertionPositions.size() || tileIndex < 0) {
                        throw new IllegalActionException();
                    }

                    Pos tileCoords = insertionPositions.get(tileIndex);

                    PlacedTile tile = new PlacedTile(gameState.tileToPlace(),
                            gameState.currentPlayer(),
                            Rotation.ALL.get(rotationIndex),
                            tileCoords);

                    if (!gameState.board().canAddTile(tile)) {
                        throw new IllegalActionException();
                    }

                    GameState newGameState = gameState.withPlacedTile(tile);

                    return new StateAction(newGameState, encodedString);
                }
            }
            case OCCUPY_TILE -> {
                if (encodedString.length() == 1) {
                    if (encodedString.equals(NO_OCCUPANT)) {
                        GameState newGameState = gameState.withNewOccupant(null);
                        return new StateAction(newGameState, encodedString);
                    }

                    int occupantKind = decodedString >> 4;
                    int zoneIndex = decodedString & 0b1111;

                    Occupant occupant = gameState.lastTilePotentialOccupants().stream()
                            .filter(o -> Zone.localId(o.zoneId()) == zoneIndex && o.kind().equals(Occupant.Kind.values()[occupantKind]))
                            .findFirst()
                            .orElseThrow(IllegalActionException::new);

                    GameState newGameState = gameState.withNewOccupant(occupant);
                    return new StateAction(newGameState, encodedString);
                }
            }
            case RETAKE_PAWN -> {
                if (encodedString.length() == 1) {
                    if (encodedString.equals(NO_OCCUPANT)) {
                        GameState newGameState = gameState.withOccupantRemoved(null);
                        return new StateAction(newGameState, encodedString);
                    }

                    List<Occupant> occupants = occupantsList(gameState);

                    if (decodedString >= occupants.size() || decodedString < 0) {
                        throw new IllegalActionException();
                    }

                    Occupant occupant = occupants.get(decodedString);
                    if (gameState.board().tileWithId(Zone.tileId(occupant.zoneId())).placer() != gameState.currentPlayer()) {
                        throw new IllegalActionException();
                    }

                    GameState newGameState = gameState.withOccupantRemoved(occupant);

                    return new StateAction(newGameState, encodedString);
                }
            }

            default -> {}
        }
        throw new IllegalActionException();
    }

    /**
     * Retrieves the sorted list of insertion positions on the game board.
     *
     * @param gameState The current game state.
     * @return A sorted list of insertion positions.
     */
    private static List<Pos> insertionPositions(GameState gameState) {
        return gameState.board().insertionPositions()
                .stream().sorted(Comparator.comparing(Pos::x).thenComparing(Pos::y)).toList();
    }

    /**
     * Retrieves the sorted list of PAWN occupants on the game board.
     *
     * @param gameState The current game state.
     * @return A sorted list of PAWN occupants.
     */
    private static ArrayList<Occupant> occupantsList(GameState gameState) {
        ArrayList<Occupant> occupantsList = new ArrayList<>(gameState.
                board()
                .occupants()
                .stream()
                .filter(occupant1 -> occupant1.kind() == Occupant.Kind.PAWN)
                .toList());
        occupantsList.sort(Comparator.comparing(Occupant::zoneId));

        return occupantsList;
    }

    /**
     * Exception thrown when an illegal action is performed.
     */
    private static class IllegalActionException extends IllegalArgumentException {}
}

