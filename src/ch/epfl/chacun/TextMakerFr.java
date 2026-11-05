package ch.epfl.chacun;

import java.util.*;

/**
 * @author Imad Bechlaghem (375401)
 */

public final class TextMakerFr implements TextMaker{

    private final Map<PlayerColor, String> playerNameMap;

    public TextMakerFr(Map<PlayerColor , String> playerNameMap){
        this.playerNameMap = Map.copyOf(playerNameMap);
    }

    @Override
    public String playerName(PlayerColor playerColor) {
        if (PlayerColor.ALL.contains(playerColor)) {
            return playerNameMap.get(playerColor);
        } else {
            return null;
        }
    } //OK

    @Override
    public String points(int points) {
        return  points > 1 ? points + " points" : points + " point";
    }
    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return STR."\{playerName(player)} a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
    }

    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {

        StringBuilder s = new StringBuilder(STR."\{listedPlayers(scorers)} \{beginningString(scorers)} \{points(points)} en tant qu'\{ endString(scorers) } d'une forêt composée de \{ tileCount } tuiles");

        if(mushroomGroupCount > 0) {
            String mushroomString = mushroomGroupCount > 1 ? STR. "et de \{ mushroomGroupCount } groupes de champignons" : " et de 1 groupe de champignons";
            s.append(mushroomString);
        }
        return s.append(".").toString();
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        StringBuilder s = new StringBuilder(STR."\{ listedPlayers(scorers) } \{ beginningString(scorers) } \{ points(points) } en tant qu'\{ endString(scorers) } d'une rivière composée de \{ tileCount } tuiles");

        if(fishCount > 0) {s.append(STR." et contenant \{fishString(fishCount)}");}

        return s.append(".").toString();
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        StringBuilder s = new StringBuilder(STR."\{playerName(scorer)} a remporté \{points(points)} en plaçant la fosse à pieux dans un pré");

        for(Map.Entry<Animal.Kind, Integer> entry: animals.entrySet()){
            if (entry.getValue() > 0) {
                s.append( STR." dans lequel elle est entourée de \{listedAnimals(animals)}");
                break;
            }

        }
        return s.append(".").toString();
    }

    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        return STR."\{playerName(scorer)} a remporté \{points(points)} en plaçant la pirogue dans un réseau hydrographique contenant \{lakeString(lakeCount)}.";
    }

    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        StringBuilder s = new StringBuilder(STR."\{listedPlayers(scorers)} \{beginningString(scorers)} \{points(points)} en tant qu'\{endString(scorers)} d'un pré");

        for(Map.Entry<Animal.Kind, Integer> entry: animals.entrySet()){
            if (entry.getValue() > 0) {
                s.append( STR." contenant \{listedAnimals(animals)}");
                break;
            }
        }
        return s.append(".").toString();
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        StringBuilder s = new StringBuilder(STR."\{listedPlayers(scorers)} \{beginningString(scorers)} \{points(points)} en tant qu'\{endString(scorers)} d'un réseau hydrographique");

        if (fishCount > 0) {s.append( STR." contenant \{fishString(fishCount)}");}

        return s.append(".").toString();
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        StringBuilder s = new StringBuilder(STR."\{listedPlayers(scorers)} \{beginningString(scorers)} \{points(points)} en tant qu'\{endString(scorers)} d'un pré contenant la grande fosse à pieux");

        for(Map.Entry<Animal.Kind, Integer> entry: animals.entrySet()){
            if (entry.getValue() > 0) {
                s.append( STR." entourée de \{listedAnimals(animals)}");
                break;
            }
        }
        return s.append(".").toString();
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        return STR."\{listedPlayers(scorers)} \{beginningString(scorers)} \{points(points)} en tant qu'\{endString(scorers)} d'un réseau hydrographique contenant le radeau et \{lakeString(lakeCount)}.";
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        return STR."\{listedPlayers(winners)} \{beginningString(winners)} la partie avec \{points(points)} !";
    }

    @Override
    public String clickToOccupy() {
        return "Cliquez sur le pion ou la hutte que vous désirez placer, ou ici pour ne pas en placer.";
    }

    @Override
    public String clickToUnoccupy() {
        return "Cliquez sur le pion que vous désirez reprendre, ou ici pour ne pas en reprendre.";
    }

    /**
     * Returns a formatted string listing player names sorted by their color.
     *
     * @param playerColors A set of PlayerColor representing the colors of the players.
     * @return A string listing player names sorted by their color.
     */
    private String listedPlayers(Set<PlayerColor> playerColors) {
        List<PlayerColor> playerColorList = new ArrayList<>(playerColors);
        Collections.sort(playerColorList);

        StringBuilder playerString = new StringBuilder();

        playerString.append(playerName(playerColorList.get(0)));

        if (playerColorList.size() == 1) {
            return playerString.toString();
        }

        for (int i = 1; i < playerColorList.size() - 1; ++i) {
            playerString.append(", ").append(playerName(playerColorList.get(i)));
        }

        playerString.append(" et ").append(playerName(playerColorList.get(playerColorList.size() - 1)));

        return playerString.toString();
    }

    /**
     * Returns a formatted string listing animals and their counts, excluding tigers and animals with a count of zero.
     *
     * @param animals A map of Animal.Kind to Integer representing the kinds and counts of animals.
     * @return A string listing animals and their counts.
     */
    private String listedAnimals(Map<Animal.Kind, Integer> animals) {
        StringBuilder stringBuilder = new StringBuilder();

        ArrayList<Map.Entry<Animal.Kind, Integer>> animalsList = new ArrayList<>(animals.entrySet());
        animalsList.sort(Map.Entry.comparingByKey());
        animalsList.removeIf(animal -> animal.getKey() == Animal.Kind.TIGER);
        animalsList.removeIf(animal -> animal.getValue() == 0);

        if (animalsList.isEmpty()) {
            return "";
        }

        stringBuilder.append(animalString(animalsList.get(0)));

        if (animalsList.size() == 1) {
            return stringBuilder.toString();
        }

        for (int i = 1; i < animalsList.size() - 1; ++i) {
            stringBuilder.append(", ").append(animalString(animalsList.get(i)));
        }

        stringBuilder.append(" et ").append(animalString(animalsList.get(animalsList.size() - 1)));

        return stringBuilder.toString();
    }

    /**
     * Returns a formatted string representing an animal and its count.
     *
     * @param entry A Map.Entry of Animal.Kind to Integer representing the kind and count of an animal.
     * @return A string representing the animal and its count.
     */
    private String animalString(Map.Entry<Animal.Kind, Integer> entry) {
        return switch (entry.getKey()) {
            case MAMMOTH -> entry.getValue() > 1 ? entry.getValue() + " mammouths" : "1 mammouth";
            case AUROCHS -> entry.getValue() > 1 ? entry.getValue() + " aurochs" : "1 auroch";
            case DEER -> entry.getValue() > 1 ? entry.getValue() + " cerfs" : "1 cerf";
            case TIGER -> null; // This case will not be reached because of the removeIf condition
        };
    }

    /**
     * Returns a string indicating whether multiple scorers have won or a single scorer has won.
     *
     * @param scorers A set of PlayerColor representing the scorers.
     * @return A string indicating the winning status.
     */
    private String beginningString(Set<PlayerColor> scorers) {
        return scorers.size() > 1 ? "ont remporté" : "a remporté";
    }

    /**
     * Returns a string indicating whether multiple players are the majority occupants or a single player is.
     *
     * @param scorers A set of PlayerColor representing the scorers.
     * @return A string indicating the majority occupant status.
     */
    private String endString(Set<PlayerColor> scorers) {
        return scorers.size() > 1 ? "occupant·e·s majoritaires" : "occupant·e majoritaire";
    }

    /**
     * Returns a formatted string representing the count of fish.
     *
     * @param fishCount An integer representing the count of fish.
     * @return A string representing the count of fish.
     */
    private String fishString(int fishCount) {
        return fishCount > 1 ? fishCount + " poissons" : fishCount == 1 ? "1 poisson" : "0 poisson"; // Handling 0 fish case
    }

    /**
     * Returns a formatted string representing the count of lakes.
     *
     * @param lakeCount An integer representing the count of lakes.
     * @return A string representing the count of lakes.
     */
    private String lakeString(int lakeCount){
        return lakeCount > 1 ? lakeCount + " lacs" : lakeCount == 1 ? "1 lac" : "0 lac"; // Handling 0 lake case
    }
}
