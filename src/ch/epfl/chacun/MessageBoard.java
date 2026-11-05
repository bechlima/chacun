package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the message board in the game.
 *
 * @author Imad Bechlaghem (375401)
 * @author Tino Bianconi (379733)
 */
public record MessageBoard(
        TextMaker textMaker,
        List<Message> messages) {

    /**
     * Ensures the immutability
     * Constructs a new MessageBoard object with the specified list of messages.
     *
     * @param messages The list of messages to be included in the message board.
     * @author Imad Bechlaghem (375401)
     */
    public MessageBoard {
        messages = List.copyOf(messages);
    }

    /**
     * Calculates the points awarded to each player.
     *
     * @return A map associating each player with their total score.
     * @author Imad Bechlaghem (375401)
     */
    public Map<PlayerColor, Integer> points() {
        Map<PlayerColor, Integer> map = new HashMap<>();

        for (Message message : messages) {
            for (PlayerColor scorer : message.scorers) {
                map.merge(scorer, message.points, Integer::sum);
            }
        }
        return map;
    }

    /**
     * Adds a message describing a closed forest, along with the associated scores and the players who contributed to the score.
     *
     * @param forest The closed forest.
     * @return The updated message board.
     * @author Imad Bechlaghem (375401)
     */
    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        Set<Integer> forestIds = forest.tileIds();
        int score = Points
                .forClosedForest(forestIds.size(), Area.mushroomGroupCount(forest));
        Set<PlayerColor> scorers = forest.majorityOccupants();
        List<Message> messageList = new ArrayList<>(messages);

        if (forest.isOccupied()) {
            String text = textMaker
                    .playersScoredForest(scorers, score, Area.mushroomGroupCount(forest), forestIds.size());
            Message newMessage = new Message(text, score, scorers, forestIds);
            messageList.add(newMessage);

            return new MessageBoard(textMaker, messageList);

        }
        return this;
    }

    /**
     * Adds a message describing a closed forest with a menhir, along with the player who closed the forest.
     *
     * @param player The player who closed the forest.
     * @param forest The closed forest.
     * @return The updated message board.
     * @author Tino Bianconi (379733)
     */
    public MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {

        String text = textMaker
                .playerClosedForestWithMenhir(player);
        List<Message> messageList = new ArrayList<>(messages);

        Message newMessage = new Message(text, 0, Set.of(), forest.tileIds());
        messageList.add(newMessage);

        return new MessageBoard(textMaker, messageList);
    }

    /**
     * Adds a message describing a closed river, along with the associated scores and the players who contributed to the score.
     *
     * @param river The closed river.
     * @return The updated message board.
     * @author Tino Bianconi (379733)
     */
    public MessageBoard withScoredRiver(Area<Zone.River> river) {

        Set<Integer> riverIds = river.tileIds();
        int Score = Points
                .forClosedRiver(riverIds.size(), Area.riverFishCount(river));
        Set<PlayerColor> scorers = river.majorityOccupants();
        List<Message> messageList = new ArrayList<>(messages);

        System.out.println(river.isOccupied());

        if (river.isOccupied()) {
            String text = textMaker
                    .playersScoredRiver(scorers, Score, Area.riverFishCount(river), riverIds.size());
            Message newMessage = new Message(text, Score, scorers, riverIds);
            messageList.add(newMessage);

            return new MessageBoard(textMaker, messageList);

        }
        return this;
    }

    /**
     * Adds a message describing a hunting trap placed by a player, along with the associated scores.
     *
     * @param scorer         The player who placed the trap.
     * @param adjacentMeadow The meadow adjacent to the trap.
     * @return The updated message board.
     * @author Imad Bechlaghem (375401)
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow , Set<Animal> cancelledDeer) {

        int score = animalPointsCount(adjacentMeadow, cancelledDeer);
        List<Message> messageList = new ArrayList<>(messages);

        if (score > 0) {
            Map<Animal.Kind, Integer> animalMap = mapMaker(adjacentMeadow, cancelledDeer);
            String text = textMaker
                    .playerScoredHuntingTrap(scorer, score, animalMap);

            Message newMessage = new Message(
                    text,
                    score,
                    Set.of(scorer),
                    adjacentMeadow.tileIds());

            messageList.add(newMessage);

            return new MessageBoard(textMaker, messageList);

        }
        return this;
    }

    /**
     * Adds a message describing a logboat placed by a player, along with the associated scores.
     *
     * @param scorer      The player who placed the logboat.
     * @param riverSystem The river system related to the logboat.
     * @return The updated message board.
     * @author Imad Bechlaghem (375401)
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {

        int lakeCount = Area
                .lakeCount(riverSystem);
        int score = Points
                .forLogboat(Area.lakeCount(riverSystem));
        String text = textMaker
                .playerScoredLogboat(scorer, score, lakeCount);
        List<Message> messageList = new ArrayList<>(messages);

        Message newMessage = new Message(text, score, Set.of(scorer), riverSystem.tileIds());
        messageList.add(newMessage);

        return new MessageBoard(textMaker, messageList);
    }

    /**
     * Adds a message describing a meadow, along with the associated scores and the players who contributed to the score.
     *
     * @param meadow           The meadow.
     * @param cancelledAnimals The cancelled animals.
     * @return The updated message board.
     * @author Tino Bianconi (379733)
     */
    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        int Score = animalPointsCount(meadow, cancelledAnimals);
        List<Message> messageList = new ArrayList<>(messages);


        if (meadow.isOccupied() && Score > 0) {
            Set<PlayerColor> scorers = meadow
                    .majorityOccupants();

            Map<Animal.Kind, Integer> animalMap = mapMaker(meadow, cancelledAnimals);

            String text = textMaker
                    .playersScoredMeadow(scorers, Score, animalMap);

            Message newMessage = new Message(text, Score, scorers, meadow.tileIds());
            messageList.add(newMessage);

            return new MessageBoard(textMaker, messageList);

        }
        return this;
    }

    /**
     * Adds a message describing a river system, along with the associated scores and the players who contributed to the score.
     *
     * @param riverSystem The river system.
     * @return The updated message board.
     * @author Imad Bechlaghem (375401)
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        int fishCount = Area
                .riverSystemFishCount(riverSystem);
        int score = Points
                .forRiverSystem(fishCount);
        Set<PlayerColor> scorers = riverSystem
                .majorityOccupants();
        List<Message> messageList = new ArrayList<>(messages);

        if (riverSystem.isOccupied() && score > 0) {
            String text = textMaker
                    .playersScoredRiverSystem(scorers, score, fishCount);
            Message newMessage = new Message(text, score, scorers, riverSystem.tileIds());
            messageList.add(newMessage);

            return new MessageBoard(textMaker, messageList);
        }

        return this;
    }

    /**
     * Adds a message describing a pit trap placed by a player, along with the associated scores.
     *
     * @param adjacentMeadow   The meadow adjacent to the trap.
     * @param cancelledAnimals The cancelled animals.
     * @return The updated message board.
     * @author Imad Bechlaghem (375401)
     */
    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {

        int points = animalPointsCount(adjacentMeadow, cancelledAnimals);
        List<Message> messageList = new ArrayList<>(messages);

        if (adjacentMeadow.isOccupied() && points > 0) {
            Map<Animal.Kind, Integer> animalMap = mapMaker(adjacentMeadow, cancelledAnimals);
            Set<PlayerColor> scorers = adjacentMeadow
                    .majorityOccupants();
            String text = textMaker
                    .playersScoredPitTrap(scorers, points, animalMap);

            Message newMessage = new Message(text, points, scorers, adjacentMeadow.tileIds());
            messageList.add(newMessage);

            return new MessageBoard(textMaker, messageList);
        }

        return this;
    }

    /**
     * Adds a message describing a raft placed by a player, along with the associated scores.
     *
     * @param riverSystem The river system related to the raft.
     * @return The updated message board.
     * @author Tino Bianconi (379733)
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {

        int lakeCount = Area
                .lakeCount(riverSystem);
        int score = Points
                .forRaft(lakeCount);
        Set<PlayerColor> scorers = riverSystem
                .majorityOccupants();
        List<Message> messageList = new ArrayList<>(messages);

        if (riverSystem.isOccupied()) {
            String text = textMaker
                    .playersScoredRaft(scorers, score, Area.lakeCount(riverSystem));
            Message newMessage = new Message(text, score, scorers, riverSystem.tileIds());
            messageList.add(newMessage);

            return new MessageBoard(textMaker, messageList);
        }
        return this;
    }

    /**
     * Adds a message describing the winners of the game, along with the associated scores.
     *
     * @param winners The winners.
     * @param points  The total score of the winners.
     * @return The updated message board.
     * @author Imad Bechlaghem (375401)
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        List<Message> messageList = new ArrayList<>(messages);
        Message newMessage = new Message(
                textMaker.playersWon(winners, points),
                0, Set.of(),
                Set.of());
        messageList.add(newMessage);

        return new MessageBoard(textMaker, messageList);
    }


    /**
     * Auxiliary method to count the points awarded by the animals in a meadow.
     *
     * @param meadow           The meadow.
     * @param cancelledAnimals The animals to disregard.
     * @return The number of points gained.
     * @author Imad Bechlaghem (375401)
     */
    private int animalPointsCount(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        int mammothCount = 0, aurochsCount = 0, deerCount = 0;

        for (Animal animal : Area.animals(meadow, cancelledAnimals)) {
            switch (animal.kind()) {
                case MAMMOTH -> ++mammothCount;
                case AUROCHS -> ++aurochsCount;
                case DEER -> ++deerCount;
            }
        }
        return Points.forMeadow(mammothCount, aurochsCount, deerCount);
    }

    /**
     * Auxiliary method that creates a map associating the type of animal with its number of occurrences in a meadow.
     *
     * @param area             The meadow containing the animals.
     * @param cancelledAnimals The cancelled animals to disregard.
     * @return A map associating the type of animal with its number of occurrences.
     * @author Imad Bechlaghem (375401)
     */
    private Map<Animal.Kind, Integer> mapMaker(Area<Zone.Meadow> area, Set<Animal> cancelledAnimals) {
        Map<Animal.Kind, Integer> map = new HashMap<>();
        int mammothCount = 0, aurochsCount = 0, deerCount = 0, tigerCount = 0;

        for (Animal animal : Area.animals(area, cancelledAnimals)) {
            switch (animal.kind()) {
                case AUROCHS -> ++aurochsCount;
                case DEER -> ++deerCount;
                case MAMMOTH -> ++mammothCount;
                case TIGER -> ++tigerCount;
            }
        }

        if (aurochsCount != 0) map.put(Animal.Kind.AUROCHS, aurochsCount);
        if (deerCount != 0) map.put(Animal.Kind.DEER, deerCount);
        if (mammothCount != 0) map.put(Animal.Kind.MAMMOTH, mammothCount);
        if (tigerCount != 0) map.put(Animal.Kind.TIGER, tigerCount);

        return map;
    }

    /**
     * Represents a message in the message board.
     *
     * @author Imad Bechlaghem (375401)
     */
    public record Message(
            String text,
            int points,
            Set<PlayerColor> scorers,






















































































































































































            Set<Integer> tileIds) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             


        /**
         * Constructor of the Message class.
         * Ensures immutability and ensures that the assigned points and text are correct.
         *
         * @author Imad Bechlaghem (375401)
         */
        public Message {
            Objects.requireNonNull(text);
            Preconditions.checkArgument(points >= 0);
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }
    }
}
