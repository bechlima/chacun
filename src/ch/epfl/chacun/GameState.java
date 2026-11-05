package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the state of the game including players, tile decks, current tile to place, game board,
 * next action, and message board.
 *
 * @author Tino Bianconi (379733)
 */
public record GameState(
        List<PlayerColor> players,
        TileDecks tileDecks,
        Tile tileToPlace,
        Board board,
        Action nextAction,
        MessageBoard messageBoard
) {

    /**
     * Enumerates possible actions in the game.
     *
     * @author Tino Bianconi (379733)
     */
    public enum Action {
        START_GAME,
        PLACE_TILE,
        RETAKE_PAWN,
        OCCUPY_TILE,
        END_GAME
    }

    /**
     * Constructs a new GameState object with the given parameters.
     *
     * @param players      List of player colors.
     * @param tileDecks    Tile decks for the game.
     * @param tileToPlace  Tile to be placed.
     * @param board        Current game board.
     * @param nextAction   Next action to be performed.
     * @param messageBoard Message board for game messages.
     * @author Tino Bianconi (379733)
     */
    public GameState {
        Preconditions.checkArgument(players.size() >= 2);
        Preconditions.checkArgument(nextAction == Action.PLACE_TILE ^ tileToPlace == null);
        Objects.requireNonNull(tileDecks);
        Objects.requireNonNull(board);
        Objects.requireNonNull(nextAction);
        Objects.requireNonNull(messageBoard);
        players = List.copyOf(players);
    }

    /**
     * Creates an initial game state with the provided players, tile decks, and text maker.
     *
     * @param players   List of player colors.
     * @param tileDecks Tile decks for the game.
     * @param textMaker Text maker for creating messages.
     * @return Initial game state.
     * @author Tino Bianconi (379733)
     */
    public static GameState initial(List<PlayerColor> players, TileDecks tileDecks, TextMaker textMaker) {
        return new GameState(players, tileDecks, null, Board.EMPTY, Action.START_GAME, new MessageBoard(textMaker, List.of()));
    }

    /**
     * Returns the current player color.
     *
     * @return Current player color or null if the game is in START_GAME or END_GAME state.
     * @author Tino Bianconi (379733)
     */
    public PlayerColor currentPlayer() {
        if (nextAction == Action.START_GAME || nextAction == Action.END_GAME) {
            return null;
        } else {
            return players.getFirst();
        }
    }

    /**
     * Calculates the count of free occupants of a specific kind for a player.
     *
     * @param player Player color.
     * @param kind   Kind of the occupant.
     * @return Count of free occupants of the specified kind for the player.
     * @author Tino Bianconi (379733)
     */
    public int freeOccupantsCount(PlayerColor player, Occupant.Kind kind) {
        return Occupant.occupantsCount(kind) - board.occupantCount(player, kind);
    }

    /**
     * Retrieves the set of potential occupants for the last placed tile.
     *
     * @return Set of potential occupants for the last placed tile.
     * @author Tino Bianconi (379733)
     */
    public Set<Occupant> lastTilePotentialOccupants() {
        Preconditions.checkArgument(board != Board.EMPTY);
        Set<Occupant> potentialOccupants = new HashSet<>();
        for (Occupant occupant : board.lastPlacedTile().potentialOccupants()) {
            if (freeOccupantsCount(currentPlayer(), occupant.kind()) >= 1) {
                Zone zone = board.lastPlacedTile().zoneWithId((occupant.zoneId()));
                switch (zone) {
                    case Zone.Forest forestZone when !board.forestArea(forestZone).isOccupied() ->
                            potentialOccupants.add(occupant);

                    case Zone.Meadow meadowZone when !board.meadowArea(meadowZone).isOccupied() ->
                            potentialOccupants.add(occupant);

                    case Zone.River riverZone when occupant.kind() == Occupant.Kind.PAWN -> {
                        if (!board.riverArea(riverZone).isOccupied()) {
                            potentialOccupants.add(occupant);
                        }
                    }
                    case Zone.Water waterZone when board.occupantCount(currentPlayer(), Occupant.Kind.HUT) < 3
                            && !board.riverSystemArea(waterZone).isOccupied() ->
                            potentialOccupants.add(occupant);

                    default -> {
                    }
                }
            }
        }
        return potentialOccupants;
    }

    /**
     * Updates the game state with the starting tile placed.
     *
     * @return Updated game state with the starting tile placed.
     * @author Tino Bianconi (379733)
     */
    public GameState withStartingTilePlaced() {
        Preconditions.checkArgument(nextAction == Action.START_GAME);

        Tile startTile = tileDecks.topTile(Tile.Kind.START);
        PlacedTile placedStartTile = new PlacedTile(startTile, currentPlayer(), Rotation.NONE, Pos.ORIGIN);

        TileDecks newTileDeck = tileDecks.withTopTileDrawn(Tile.Kind.START);
        Tile newTileToPlace = newTileDeck.topTile(Tile.Kind.NORMAL);
        newTileDeck = newTileDeck.withTopTileDrawn(Tile.Kind.NORMAL);
        Board newBoard = board.withNewTile(placedStartTile);

        return new GameState(players, newTileDeck, newTileToPlace, newBoard, Action.PLACE_TILE, messageBoard);
    }

    /**
     * Updates the game state with the given tile placed on the board.
     *
     * @param tile Tile to be placed on the board.
     * @return Updated game state with the tile placed.
     * @author Tino Bianconi (379733)
     */
    public GameState withPlacedTile(PlacedTile tile) {
        Preconditions.checkArgument(nextAction == Action.PLACE_TILE && tile.occupant() == null && board.lastPlacedTile() != null);
        Board boardWithPlacedTile = board.withNewTile(tile);
        MessageBoard newMessageBoard = messageBoard;
        Zone powerZone = tile.specialPowerZone();
        if (powerZone != null) {
            switch (powerZone.specialPower()) {
                case SHAMAN -> {
                    if (boardWithPlacedTile.occupantCount(currentPlayer(), Occupant.Kind.PAWN) > 0) {
                        return new GameState(players, tileDecks, null, boardWithPlacedTile, Action.RETAKE_PAWN, newMessageBoard);
                    }
                }
                case HUNTING_TRAP -> {
                    Area<Zone.Meadow> adjacentMeadows = boardWithPlacedTile.adjacentMeadow(boardWithPlacedTile.tileWithId(powerZone.tileId()).pos(), (Zone.Meadow) powerZone);
                    Set<Animal> totalAnimals = Area.animals(adjacentMeadows, Set.of());
                    boardWithPlacedTile = boardWithPlacedTile.withMoreCancelledAnimals(totalAnimals);
                    newMessageBoard = newMessageBoard.withScoredHuntingTrap(currentPlayer(), adjacentMeadows, cancelledDeer(totalAnimals));
                }
                case LOGBOAT ->
                        newMessageBoard = newMessageBoard.withScoredLogboat(currentPlayer(), boardWithPlacedTile.riverSystemArea((Zone.Water) powerZone));

            }
        }

        return new GameState(players, tileDecks, null, boardWithPlacedTile, Action.OCCUPY_TILE, newMessageBoard).withTurnFinishedIfOccupationImpossible();
    }


    /**
     * Updates the game state with the specified occupant removed.
     *
     * @param occupant Occupant to be removed.
     * @return Updated game state with the occupant removed.
     * @author Tino Bianconi (379733)
     */
    public GameState withOccupantRemoved(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.RETAKE_PAWN
                && (occupant == null || occupant.kind() == Occupant.Kind.PAWN)
                && board.occupantCount(currentPlayer(), Occupant.Kind.PAWN) >= 1);

        Board newBoard = occupant != null ? board.withoutOccupant(occupant) : board;

        return new GameState(players,
                tileDecks, 
                tileToPlace,
                newBoard,
                Action.OCCUPY_TILE,
                messageBoard).withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Updates the game state with a new occupant added.
     *
     * @param occupant New occupant to be added.
     * @return Updated game state with the new occupant added.
     * @author Tino Bianconi (379733)
     */
    public GameState withNewOccupant(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.OCCUPY_TILE);

        Board newBoard = occupant != null ? board.withOccupant(occupant) : board;

        return new GameState(players, tileDecks, tileToPlace, newBoard, Action.OCCUPY_TILE, messageBoard).withTurnFinished();
    }

    /**
     * Returns a new GameState instance with the turn finished.
     * This method handles various game logic such as scoring closed forests and rivers,
     * checking if players can play a menhir, and preparing for the next turn.
     *
     * @return A new GameState instance with the turn finished.
     * @author Tino Bianconi (379733)
     */
    private GameState withTurnFinished() {
        Tile newTileToPlace;
        TileDecks newTileDecks = tileDecks;
        MessageBoard newMessageBoard = messageBoard;
        Board newBoard = board;
        Set<Area<Zone.Forest>> closedForests = board.forestsClosedByLastTile();
        Set<Area<Zone.River>> closedRivers = board.riversClosedByLastTile();
        boolean canPlayMenhir = false;

        for (Area<Zone.River> riverArea : closedRivers) {
            newMessageBoard = newMessageBoard.withScoredRiver(riverArea);
        }

        for (Area<Zone.Forest> forestArea : closedForests) {
            newMessageBoard = newMessageBoard.withScoredForest(forestArea);

            if (Area.hasMenhir(forestArea) && newBoard.lastPlacedTile().kind() == Tile.Kind.NORMAL && !newTileDecks.menhirTiles().isEmpty()) {
                newMessageBoard = newMessageBoard.withClosedForestWithMenhir(currentPlayer(), forestArea);

                if (!newTileDecks.menhirTiles().isEmpty()) {
                    canPlayMenhir = true;
                }
            }
        }
        newBoard = newBoard.withoutGatherersOrFishersIn(closedForests, closedRivers);

        if (canPlayMenhir) {
            newTileDecks = newTileDecks.withTopTileDrawnUntil(Tile.Kind.MENHIR, newBoard::couldPlaceTile);
            newTileToPlace = newTileDecks.topTile(Tile.Kind.MENHIR);
            newTileDecks = newTileDecks.withTopTileDrawn(Tile.Kind.MENHIR);
        } else {
            newTileDecks = newTileDecks.withTopTileDrawnUntil(Tile.Kind.NORMAL, newBoard::couldPlaceTile);
            if (newTileDecks.normalTiles().isEmpty() && nextAction == Action.OCCUPY_TILE) {
                return new GameState(players, newTileDecks, null, newBoard, Action.END_GAME, newMessageBoard).withFinalPointsCounted();
            } else  {
                newTileToPlace = newTileDecks.topTile(Tile.Kind.NORMAL);
                newTileDecks = newTileDecks.withTopTileDrawn(Tile.Kind.NORMAL);
            }
        }
        return new GameState(players, newTileDecks, newTileToPlace, newBoard, Action.PLACE_TILE, newMessageBoard).nextPlayer();
    }


    /**
     * Returns a new GameState instance with the turn finished if it's impossible to occupy any tile with the last placed tile.
     * Otherwise, it returns the current state with the turn finished.
     *
     * @return A new GameState instance with the turn finished or the current state.
     * @author Tino Bianconi (379733)
     */
    private GameState withTurnFinishedIfOccupationImpossible() {
        return !lastTilePotentialOccupants().isEmpty() ? new GameState(this.players, this.tileDecks, this.tileToPlace, this.board, Action.OCCUPY_TILE, this.messageBoard) : withTurnFinished();
    }


    /**
     * Returns a new GameState instance with the final points counted.
     * This method handles the calculation of final points, including scoring meadows, rivers, and determining the winners.
     *
     * @return A new GameState instance with the final points counted.
     * @author Tino Bianconi (379733)
     */
    private GameState withFinalPointsCounted() {
        MessageBoard newMessageBoard = messageBoard;
        Board newBoard = board;
        Set<Animal> cancelledAnimals = board.cancelledAnimals();
        Set<Animal> newCancelledAnimals = new HashSet<>();

        for (Area<Zone.Meadow> meadowArea : newBoard.meadowAreas()) {
            Set<Animal> totalAnimals = Area.animals(meadowArea, cancelledAnimals);
            Set<Animal> deerInMeadow = totalAnimals
                    .stream()
                    .filter(animal -> animal.kind() == Animal.Kind.DEER)
                    .collect(Collectors.toSet());
            int tigerCount = (int) totalAnimals
                    .stream()
                    .filter(animal -> animal.kind() == Animal.Kind.TIGER)
                    .count();

            Zone zoneWithWildFire = meadowArea.zoneWithSpecialPower(Zone.SpecialPower.WILD_FIRE);
            Zone zoneWithPitTrap = meadowArea.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP);

            if (zoneWithWildFire == null) {
                if (zoneWithPitTrap instanceof Zone.Meadow meadowWithPitTrap) {
                    Area<Zone.Meadow> adjacentMeadow = newBoard.adjacentMeadow(newBoard.tileWithId(meadowWithPitTrap.tileId()).pos(), meadowWithPitTrap);
                    Set<Animal> animalInAdjacentMeadow = Area.animals(adjacentMeadow, newBoard.cancelledAnimals());
                    Set<Animal> deerInAdjacentMeadow = deerInMeadow
                            .stream()
                            .filter(animalInAdjacentMeadow::contains)
                            .collect(Collectors.toSet());
                    //Cancel the deer out of the adjacents meadows
                    for (Animal deer : deerInMeadow) {
                        if (!deerInAdjacentMeadow.contains(deer) && tigerCount > 0) {
                            newCancelledAnimals.add(deer);
                            tigerCount--;
                        }
                    }
                    //cancel the deer in the adjacents meadows
                    for (Animal deer : deerInAdjacentMeadow) {
                        if (tigerCount > 0) {
                            newCancelledAnimals.add(deer);
                            tigerCount--;
                        }
                    }
                    newBoard = newBoard.withMoreCancelledAnimals(newCancelledAnimals);
                    newMessageBoard = newMessageBoard
                            .withScoredMeadow(meadowArea, newBoard.cancelledAnimals())
                            .withScoredPitTrap(adjacentMeadow, newBoard.cancelledAnimals());

                } else {
                    newCancelledAnimals.addAll(cancelledDeer(totalAnimals));

                    newBoard = newBoard
                            .withMoreCancelledAnimals(newCancelledAnimals);
                    newMessageBoard = newMessageBoard
                            .withScoredMeadow(meadowArea, newBoard.cancelledAnimals());

                }

            } else {
                if (zoneWithPitTrap instanceof Zone.Meadow meadowWithPitTrap) {
                    Area<Zone.Meadow> adjacentMeadow = newBoard.adjacentMeadow(newBoard.tileWithId(meadowWithPitTrap.tileId()).pos(), meadowWithPitTrap);
                    newMessageBoard = newMessageBoard
                            .withScoredMeadow(meadowArea, newBoard.cancelledAnimals())
                            .withScoredPitTrap(adjacentMeadow, newBoard.cancelledAnimals());
                } else {
                    newMessageBoard = newMessageBoard
                            .withScoredMeadow(meadowArea, newBoard.cancelledAnimals());
                }
            }
        }

        for (Area<Zone.Water> waterArea : board().riverSystemAreas()) {
            newMessageBoard = newMessageBoard.withScoredRiverSystem(waterArea);

            if (waterArea.zoneWithSpecialPower(Zone.SpecialPower.RAFT) != null) {
                newMessageBoard = newMessageBoard.withScoredRaft(waterArea);
            }
        }

        int maxPoints;
        Map<PlayerColor, Integer> playersPoints = newMessageBoard.points();
        if (!playersPoints.values().isEmpty()) {
            maxPoints = Collections
                    .max(playersPoints.values());
        } else {
            maxPoints = 0;
        }
        Set<PlayerColor> winners = playersPoints.entrySet()
                .stream()
                .filter(points -> points.getValue() == maxPoints)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        newMessageBoard = newMessageBoard
                .withWinners(winners, maxPoints);
        return new GameState(this.players, this.tileDecks, null, newBoard, Action.END_GAME, newMessageBoard);
    }

    /**
     * Returns a set of animals consisting of the specified number of cancelled deer from the given set of animals.
     * If the number of cancelled deer is greater than the total number of deer, all deer are returned.
     *
     * @param animals The set of animals.
     * @return A set of cancelled deer.
     * @author Tino Bianconi (379733)
     */
    private Set<Animal> cancelledDeer(Set<Animal> animals) {
        int tigerCount = (int) animals
                .stream()
                .filter(animal -> animal.kind() == Animal.Kind.TIGER)
                .count();
        Set<Animal> deer = animals
                .stream()
                .filter(animal -> animal.kind() == Animal.Kind.DEER)
                .collect(Collectors.toSet());
        if (tigerCount >= deer.size()) {
            return deer;
        } else {
            return deer
                    .stream()
                    .limit(tigerCount)
                    .collect(Collectors.toSet());
        }
    }


    /**
     * Returns a new GameState instance with the next player's turn.
     * The next player is determined based on the current state and the last placed tile.
     *
     * @return A new GameState instance with the next player's turn.
     * @author Tino Bianconi (379733)
     */
    private GameState nextPlayer() {
        LinkedList<PlayerColor> turnPlayer = new LinkedList<>(players);
        if (tileToPlace.kind() != Tile.Kind.MENHIR) {
            turnPlayer.offerLast(turnPlayer.pollFirst());
        }
        return new GameState(turnPlayer, tileDecks, tileToPlace, board, nextAction, messageBoard);
    }


}


