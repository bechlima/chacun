package ch.epfl.chacun.MyTest;

import ch.epfl.chacun.PlayerColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import java.util.Map;
import java.util.Set;
import ch.epfl.chacun.*;
import static ch.epfl.chacun.PlayerColor.*;
import static ch.epfl.chacun.PlayerColor.BLUE;

import javafx.scene.paint.Color;

import ch.epfl.chacun.gui.ColorMap;


public class TextMakerFrTest {
    @Test
    void testFillColor() {
        assertEquals(Color.RED, ColorMap.fillColor(PlayerColor.RED));
        assertEquals(Color.BLUE, ColorMap.fillColor(PlayerColor.BLUE));
        assertEquals(Color.LIME, ColorMap.fillColor(PlayerColor.GREEN));
        assertEquals(Color.YELLOW, ColorMap.fillColor(PlayerColor.YELLOW));
        assertEquals(Color.PURPLE, ColorMap.fillColor(PlayerColor.PURPLE));
    }


    private Map<PlayerColor, String> mapPlayers(Map<PlayerColor, String> playerNameMap){
        playerNameMap.put(RED, "Dalia");
        playerNameMap.put(BLUE, "Claude");
        playerNameMap.put(GREEN, "Bachir");
        playerNameMap.put(YELLOW, "Alice");
        return playerNameMap;
    }
    private final Map<PlayerColor, String> playerNameMap = mapPlayers(new HashMap<>());

    private final TextMakerFr text = new TextMakerFr(playerNameMap);

    private Map<Animal.Kind, Integer> animalsMap(int auroch, int deer, int mammoth, int tiger){
        Map<Animal.Kind, Integer> animals = new HashMap<>();
        animals.put(Animal.Kind.AUROCHS, auroch);
        animals.put(Animal.Kind.DEER, deer);
        animals.put(Animal.Kind.MAMMOTH, mammoth);
        animals.put(Animal.Kind.TIGER, tiger);
        return animals;
    }

    @Test
    void playerNameWorks() {

    }
    @Test
    void pointsWorks() {
        String phrase1 = text.points(6);
        String phrase2 = text.points(1);
        String phrase3 = text.points(69);
        String phrase4 = text.points(77);

        String expected1 = "6 points";
        String expected2 = "1 point";
        String expected3 = "69 points";
        String expected4 = "77 points";

        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);
        assertEquals(expected3, phrase3);
        assertEquals(expected4, phrase4);

    }
    @Test
    void playerClosedForestWithMenhirWorks() {
        String phrase1 = text.playerClosedForestWithMenhir(RED);
        String phrase2 = text.playerClosedForestWithMenhir(BLUE);
        String phrase3 = text.playerClosedForestWithMenhir(GREEN);
        String phrase4 = text.playerClosedForestWithMenhir(YELLOW);

        String expected1 = "Dalia a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
        String expected2 = "Claude a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
        String expected3 = "Bachir a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
        String expected4 = "Alice a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);
        assertEquals(expected3, phrase3);
        assertEquals(expected4, phrase4);
    }
    @Test
    void playersScoredForestWorks() {
        Set<PlayerColor> actors1 = Set.of(BLUE);
        String phrase1 = text.playersScoredForest(actors1, 6, 0,3);
        String expected1 = "Claude a remporté 6 points en tant qu'occupant·e majoritaire d'une forêt composée de 3 tuiles.";
        Set<PlayerColor> actors2 = Set.of(RED, YELLOW);
        String phrase2 = text.playersScoredForest(actors2, 9, 1, 3);
        String expected2 = "Dalia et Alice ont remporté 9 points en tant qu'occupant·e·s majoritaires d'une forêt composée de 3 tuiles et de 1 groupe de champignons.";
        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);
    }
    @Test
    void playersScoredRiverWorks() {
        Set<PlayerColor> actors1 = Set.of(BLUE, GREEN);
        String phrase1 = text.playersScoredRiver(actors1, 3, 0, 3);
        String expected1 = "Claude et Bachir ont remporté 3 points en tant qu'occupant·e·s majoritaires d'une rivière composée de 3 tuiles.";
        Set<PlayerColor> actors2 = Set.of(YELLOW);
        String phrase2 = text.playersScoredRiver(actors2, 8, 5, 3);
        String expected2 = "Alice a remporté 8 points en tant qu'occupant·e majoritaire d'une rivière composée de 3 tuiles et contenant 5 poissons.";
        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);
    }
    @Test
    void playerScoredHuntingTrapWorks() {
        String phrase1 = text.playerScoredHuntingTrap(GREEN, 10, animalsMap(2,3,1,69));
        String expected1 = "Bachir a remporté 10 points en plaçant la fosse à pieux dans un pré dans lequel elle est entourée de 1 mammouth, 2 aurochs et 3 cerfs.";
        String phrase2 = text.playerScoredHuntingTrap(BLUE, 8, animalsMap(0, 6, 2, 96));
        String expected2 = "Claude a remporté 8 points en plaçant la fosse à pieux dans un pré dans lequel elle est entourée de 2 mammouths et 6 cerfs.";
        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);

    }
    @Test
    void playerScoredLogboatWorks(){
        String phrase1 = text.playerScoredLogboat(YELLOW, 8, 4);
        String expected1 = "Alice a remporté 8 points en plaçant la pirogue dans un réseau hydrographique contenant 4 lacs.";
        String phrase2 = text.playerScoredLogboat(GREEN, 37, 0);
        String expected2 = "Bachir a remporté 37 points en plaçant la pirogue dans un réseau hydrographique contenant 0 lac.";

        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);
    }
    @Test
    void playersScoredMeadowWorks(){
        Set<PlayerColor> actors2 = Set.of(RED);
        String phrase1 = text.playersScoredMeadow(actors2, 1, animalsMap(0, 1, 0, 66));
        String expected1 = "Dalia a remporté 1 point en tant qu'occupant·e majoritaire d'un pré contenant 1 cerf.";

        Set<PlayerColor> actors1 = Set.of(BLUE, GREEN);
        String phrase2 = text.playersScoredMeadow(actors1, 5, animalsMap(0, 2, 1, 8));
        String expected2 = "Claude et Bachir ont remporté 5 points en tant qu'occupant·e·s majoritaires d'un pré contenant 1 mammouth et 2 cerfs.";
        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);
    }
    @Test
    void playersScoredRiverSystemWorks(){

        String phrase1 = text.playersScoredRiverSystem(Set.of(YELLOW), 9, 9);
        String expected1 = "Alice a remporté 9 points en tant qu'occupant·e majoritaire d'un réseau hydrographique contenant 9 poissons.";

        String phrase2 = text.playersScoredRiverSystem(Set.of(RED, GREEN, BLUE), 1, 1);
        String expected2 = "Dalia, Claude et Bachir ont remporté 1 point en tant qu'occupant·e·s majoritaires d'un réseau hydrographique contenant 1 poisson.";

        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);
    }
    @Test
    void playersScoredPitTrapWorks(){

        String phrase1 = text.playersScoredPitTrap(Set.of(YELLOW, GREEN), 12, animalsMap(2, 2, 2, 3));
        String expected1 = "Bachir et Alice ont remporté 12 points en tant qu'occupant·e·s majoritaires d'un pré contenant la grande fosse à pieux entourée de 2 mammouths, 2 aurochs et 2 cerfs.";
        String phrase2 = text.playersScoredPitTrap(Set.of(RED), 2, animalsMap(1, 0, 0, 2));
        String expected2= "Dalia a remporté 2 points en tant qu'occupant·e majoritaire d'un pré contenant la grande fosse à pieux entourée de 1 auroch.";

        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);

    }
    @Test
    void playersScoredRaftWorks(){
        String phrase1 = text.playersScoredRaft(Set.of(RED,BLUE), 10, 10);
        String expected1 = "Dalia et Claude ont remporté 10 points en tant qu'occupant·e·s majoritaires d'un réseau hydrographique contenant le radeau et 10 lacs.";
        String phrase2 = text.playersScoredRaft(Set.of(YELLOW), 1, 1);
        String expected2 = "Alice a remporté 1 point en tant qu'occupant·e majoritaire d'un réseau hydrographique contenant le radeau et 1 lac.";

        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);

    }
    @Test
    void playersWonWorks(){
        String phrase1 = text.playersWon(Set.of(GREEN), 111);
        String expected1= "Bachir a remporté la partie avec 111 points !";
        String phrase2 = text.playersWon(Set.of(RED, YELLOW), 123);
        String expected2 = "Dalia et Alice ont remporté la partie avec 123 points !";

        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);

    }
    @Test
    void clickToOccupyAndClickToUnoccupyWorks(){
        String phrase1 = text.clickToOccupy();
        String expected1 = "Cliquez sur le pion ou la hutte que vous désirez placer, ou ici pour ne pas en placer.";
        String phrase2 = text.clickToUnoccupy();
        String expected2 = "Cliquez sur le pion que vous désirez reprendre, ou ici pour ne pas en reprendre.";

        assertEquals(expected1, phrase1);
        assertEquals(expected2, phrase2);

    }
}


