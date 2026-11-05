package ch.epfl.chacun;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MyTextMakerFrTest {
    @Test
    void playerClosedForestWithMenhirWorks(){
       String testString = "Claude a remporté 6 points en tant qu'occupant·e majoritaire d'une forêt composée de 3 tuiles.";
       TextMakerFr textMakerFr = new TextMakerFr(Map.of(PlayerColor.BLUE, "Claude"));
       assertEquals(textMakerFr.playersScoredForest(Set.of(PlayerColor.BLUE), 6, 0, 3), testString);
    }
}
