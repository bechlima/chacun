package ch.epfl.chacun.MyTest;

import ch.epfl.chacun.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MyActionEncoderTest {

    private final List<PlayerColor> playersList = List.of(PlayerColor.RED, PlayerColor.BLUE);

    private final Tile tile0 = Tiles.TILES.getFirst();
    private final Tile tile1 = Tiles.TILES.get(1);
    private final Tile tile2 = Tiles.TILES.get(2);
    private final Tile tile25 = Tiles.TILES.get(25);
    private final Tile tile27 = Tiles.TILES.get(27);
    private final Tile tile31 = Tiles.TILES.get(31);
    private final Tile tile33 = Tiles.TILES.get(33);
    private final Tile tile35 = Tiles.TILES.get(35);
    private final Tile tile37 = Tiles.TILES.get(37);
    private final Tile tile44 = Tiles.TILES.get(44);
    private final Tile tile45 = Tiles.TILES.get(45);
    private final Tile tile47 = Tiles.TILES.get(47);
    private final Tile tile49 = Tiles.TILES.get(49);
    private final Tile tile54 = Tiles.TILES.get(54);
    private final Tile tile56 = Tiles.TILES.get(56);
    private final Tile tile62 = Tiles.TILES.get(62);
    private final Tile tile65 = Tiles.TILES.get(65);
    private final Tile tile71 = Tiles.TILES.get(71);
    private final Tile tile72 = Tiles.TILES.get(72);
    private final Tile tile88 = Tiles.TILES.get(88);
    private final Tile tile90 = Tiles.TILES.get(90);

    @Test
    void withPlacedTileWorks() {
        //TODO edge cases ?
        var start = List.of(tile56);
        var normal = List.of(tile0, tile1, tile2);
        var menhir = List.of(tile88, tile90);
        var tileDecks = new TileDecks(start, normal, menhir);
        Map<PlayerColor, String> players = new HashMap<>();
        players.put(PlayerColor.RED, "Rose");
        players.put(PlayerColor.BLUE, "Bernard");
        var textMaker = new TextMakerFr(players);
        var gameState = GameState.initial(playersList, tileDecks, textMaker);

        var pt0 = new PlacedTile(tile0, PlayerColor.RED, Rotation.NONE, new Pos(1, 0));
        var pt1 = new PlacedTile(tile1, PlayerColor.BLUE, Rotation.RIGHT, new Pos(1, -1));

        var gameState1 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt0)
                .withNewOccupant(null);
        var gameState2 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt0)
                .withNewOccupant(null);

        var expectedGS1 = gameState1.withPlacedTile(pt1);
        var expectedGS2 = gameState2.withPlacedTile(pt1);

        var value = 0b00000_01101;
        var expectedAction = Base32.encodeBits10(value);
        var expectedSA1 = new ActionEncoder.StateAction(expectedGS1, expectedAction);
        var expectedSA2 = new ActionEncoder.StateAction(expectedGS2, "AN");

        assertAll(
                () -> assertEquals(expectedSA1, ActionEncoder.withPlacedTile(gameState1, pt1)),
                () -> assertEquals(expectedSA2, ActionEncoder.withPlacedTile(gameState2, pt1))
        );
    }

    @Test
    void withNewOccupantWorksWithNullOccupant() {
        var start = List.of(tile56);
        var normal = List.of(tile0, tile1, tile2);
        var menhir = List.of(tile88, tile90);
        var tileDecks = new TileDecks(start, normal, menhir);
        Map<PlayerColor, String> players = new HashMap<>();
        players.put(PlayerColor.RED, "Rose");
        players.put(PlayerColor.BLUE, "Bernard");
        var textMaker = new TextMakerFr(players);
        var gameState = GameState.initial(playersList, tileDecks, textMaker);

        var pt0 = new PlacedTile(tile0, PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var gameState1 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt0);
        var expectedGS = gameState.withStartingTilePlaced()
                .withPlacedTile(pt0)
                .withNewOccupant(null);

        var expectedSA1 = new ActionEncoder.StateAction(expectedGS, Base32.encodeBits5(0b11111));
        var expectedSA2 = new ActionEncoder.StateAction(expectedGS, "7");

        assertAll(
                () -> assertEquals(expectedSA1, ActionEncoder.withNewOccupant(gameState1, null)),
                () -> assertEquals(expectedSA2, ActionEncoder.withNewOccupant(gameState1, null))
        );
    }

    @Test
    void withNewOccupantWorksWithNonNullOccupant() {
        var pawn010 = new Occupant(Occupant.Kind.PAWN, 10);
        var hut018 = new Occupant(Occupant.Kind.HUT, 18);

        var start = List.of(tile56);
        var normal = List.of(tile0, tile1, tile2);
        var menhir = List.of(tile88, tile90);
        var tileDecks = new TileDecks(start, normal, menhir);
        Map<PlayerColor, String> players = new HashMap<>();
        players.put(PlayerColor.RED, "Rose");
        players.put(PlayerColor.BLUE, "Bernard");
        var textMaker = new TextMakerFr(players);
        var gameState = GameState.initial(playersList, tileDecks, textMaker);

        var pt0 = new PlacedTile(tile0, PlayerColor.RED, Rotation.NONE, new Pos(1, 0));
        var pt1 = new PlacedTile(tile1, PlayerColor.BLUE, Rotation.RIGHT, new Pos(1, -1));

        var gameState1 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt0)
                .withNewOccupant(null)
                .withPlacedTile(pt1);
        var gameState2 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt0)
                .withNewOccupant(null)
                .withPlacedTile(pt1);

        var expectedGS1 = gameState1.withNewOccupant(pawn010);
        var expectedGS2 = gameState2.withNewOccupant(hut018);

        var expectedSA1 = new ActionEncoder.StateAction(expectedGS1, Base32.encodeBits5(0));
        var expectedSA2 = new ActionEncoder.StateAction(expectedGS2, Base32.encodeBits5(0b11000));

        assertAll(
                () -> assertEquals(expectedSA1, ActionEncoder.withNewOccupant(gameState1, pawn010)),
                () -> assertEquals(expectedSA2, ActionEncoder.withNewOccupant(gameState2, hut018))
        );
    }

    @Test
    void withOccupantRemovedWorksOnNullOccupant() {
        var start = List.of(tile56);
        var normal = List.of(tile0, tile1, tile2);
        var menhir = List.of(tile88, tile90);
        var tileDecks = new TileDecks(start, normal, menhir);
        Map<PlayerColor, String> players = new HashMap<>();
        players.put(PlayerColor.RED, "Rose");
        players.put(PlayerColor.BLUE, "Bernard");
        var textMaker = new TextMakerFr(players);
        var gameState = GameState.initial(playersList, tileDecks, textMaker);

        var pt0 = new PlacedTile(tile0, PlayerColor.RED, Rotation.NONE, new Pos(1, 0));
        var pt1 = new PlacedTile(tile1, PlayerColor.BLUE, Rotation.HALF_TURN, new Pos(0, 1));
        var pt88 = new PlacedTile(tile88, PlayerColor.BLUE, Rotation.NONE, new Pos(-1, 0));

        var pawn002 = new Occupant(Occupant.Kind.PAWN, 2);
        var pawn014 = new Occupant(Occupant.Kind.PAWN, 14);

        var gameState1 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt0).withNewOccupant(pawn002)
                .withPlacedTile(pt1).withNewOccupant(pawn014)
                .withPlacedTile(pt88);

        var expectedGS1 = gameState1.withOccupantRemoved(pawn002);
        var expectedGS2 = gameState1.withOccupantRemoved(pawn014);
        var expectedGS3 = gameState1.withOccupantRemoved(null);
        var action1 = Base32.encodeBits5(0);
        var action2 = Base32.encodeBits5(1);
        var action3 = Base32.encodeBits5(0b11111);

        var expectedSA1 = new ActionEncoder.StateAction(expectedGS1, action1);
        var expectedSA2 = new ActionEncoder.StateAction(expectedGS2, action2);
        var expectedSA3 = new ActionEncoder.StateAction(expectedGS3, action3);

        assertAll(
                () -> assertEquals(expectedSA1, ActionEncoder.withOccupantRemoved(gameState1, pawn002)),
                () -> assertEquals(expectedSA2, ActionEncoder.withOccupantRemoved(gameState1, pawn014)),
                () -> assertEquals(expectedSA3, ActionEncoder.withOccupantRemoved(gameState1, null))
        );
    }

    @Test
    void onBigBoard() {
        var list = List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.PURPLE);
        var start = List.of(tile56);
        var normal = List.of(tile54, tile65, tile35, tile33, tile37, tile47, tile49, tile27, tile31, tile71, tile72, tile45);
        var menhir = List.of(tile88, tile90);
        var tileDecks = new TileDecks(start, normal, menhir);
        Map<PlayerColor, String> players = new HashMap<>();
        players.put(PlayerColor.RED, "Alice");
        players.put(PlayerColor.BLUE, "Bob");
        players.put(PlayerColor.GREEN, "Carol");
        players.put(PlayerColor.YELLOW, "David");
        players.put(PlayerColor.PURPLE, "Elsa");
        var textMaker = new TextMakerFr(players);
        var gameState = GameState.initial(list, tileDecks, textMaker);

        var pt54 = new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var pt65 = new PlacedTile(tile65, PlayerColor.BLUE, Rotation.NONE, new Pos(1, 0));
        var pt35 = new PlacedTile(tile35, PlayerColor.GREEN, Rotation.NONE, new Pos(1, 1));
        var pt33 = new PlacedTile(tile33, PlayerColor.YELLOW, Rotation.HALF_TURN, new Pos(1, 2));
        var pt37 = new PlacedTile(tile37, PlayerColor.PURPLE, Rotation.HALF_TURN, new Pos(0, 2));
        var pt47 = new PlacedTile(tile47, PlayerColor.RED, Rotation.RIGHT, new Pos(-1, 1));
        var pt49 = new PlacedTile(tile49, PlayerColor.BLUE, Rotation.RIGHT, new Pos(-1, 2));
        var pt27 = new PlacedTile(tile27, PlayerColor.GREEN, Rotation.LEFT, new Pos(-1, 3));
        var pt31 = new PlacedTile(tile31, PlayerColor.YELLOW, Rotation.LEFT, new Pos(2, 2));
        var pt71 = new PlacedTile(tile71, PlayerColor.PURPLE, Rotation.RIGHT, new Pos(0, 3));
        var pt72 = new PlacedTile(tile72, PlayerColor.RED, Rotation.RIGHT, new Pos(1, 3));
        var pt45 = new PlacedTile(tile45, PlayerColor.BLUE, Rotation.LEFT, new Pos(2, 3));
        var pt88 = new PlacedTile(tile88, PlayerColor.BLUE, Rotation.HALF_TURN, new Pos(3, 3));

        var pawn540 = new Occupant(Occupant.Kind.PAWN, 540);
        var pawn330 = new Occupant(Occupant.Kind.PAWN, 330);
        var hut471 = new Occupant(Occupant.Kind.HUT, 471);
        var pawn492 = new Occupant(Occupant.Kind.PAWN, 492);
        var pawn271 = new Occupant(Occupant.Kind.PAWN, 271);
        var pawn713 = new Occupant(Occupant.Kind.PAWN, 713);
        var pawn721 = new Occupant(Occupant.Kind.PAWN, 721);
        var hut451 = new Occupant(Occupant.Kind.HUT, 451);

        var gs33 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(pawn540)
                .withPlacedTile(pt65).withNewOccupant(null)
                .withPlacedTile(pt35).withNewOccupant(null);
        var expectedGS33 = gs33.withPlacedTile(pt33);
        var sa33 = new ActionEncoder.StateAction(expectedGS33, Base32.encodeBits10(0b00000_11010));

        var gs37 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(pawn540)
                .withPlacedTile(pt65).withNewOccupant(null)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37);
        var expectedGS37 = gs37.withNewOccupant(null);
        var sa37 = new ActionEncoder.StateAction(expectedGS37, Base32.encodeBits5(0b11111));

        var gs27 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(pawn540)
                .withPlacedTile(pt65).withNewOccupant(null)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37).withNewOccupant(null)
                .withPlacedTile(pt47).withNewOccupant(hut471)
                .withPlacedTile(pt49).withNewOccupant(pawn492)
                .withPlacedTile(pt27);
        var expectedGS27 = gs27.withNewOccupant(pawn271);
        var sa27 = new ActionEncoder.StateAction(expectedGS27, Base32.encodeBits5(0b00001));

        var gs = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(pawn540)
                .withPlacedTile(pt65).withNewOccupant(null)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37).withNewOccupant(null)
                .withPlacedTile(pt47).withNewOccupant(hut471)
                .withPlacedTile(pt49).withNewOccupant(pawn492)
                .withPlacedTile(pt27).withNewOccupant(pawn271)
                .withPlacedTile(pt31).withNewOccupant(null)
                .withPlacedTile(pt71).withNewOccupant(pawn713)
                .withPlacedTile(pt72).withNewOccupant(pawn721)
                .withPlacedTile(pt45).withNewOccupant(hut451)
                .withPlacedTile(pt88);

        var expectedGS1 = gs.withOccupantRemoved(null);
        var sa1 = new ActionEncoder.StateAction(expectedGS1, Base32.encodeBits5(0b11111));

        var expectedGS2 = gs.withOccupantRemoved(pawn721);
        var sa2 = new ActionEncoder.StateAction(expectedGS2, Base32.encodeBits5(3));

        var expectedGS5 = gs.withOccupantRemoved(pawn492);
        var sa5 = new ActionEncoder.StateAction(expectedGS5, Base32.encodeBits5(0));

        var expectedGS6 = gs.withOccupantRemoved(pawn540);
        var sa6 = new ActionEncoder.StateAction(expectedGS6, Base32.encodeBits5(1));

        var expectedGS7 = gs.withOccupantRemoved(pawn713);
        var sa7 = new ActionEncoder.StateAction(expectedGS7, Base32.encodeBits5(2));

        assertAll(
                () -> assertEquals(sa33, ActionEncoder.withPlacedTile(gs33, pt33)),
                () -> assertEquals(sa37, ActionEncoder.withNewOccupant(gs37, null)),
                () -> assertEquals(sa27, ActionEncoder.withNewOccupant(gs27, pawn271)),
                () -> assertEquals(sa1, ActionEncoder.withOccupantRemoved(gs, null)),
                () -> assertEquals(sa2, ActionEncoder.withOccupantRemoved(gs, pawn721)),
                () -> assertEquals(sa5, ActionEncoder.withOccupantRemoved(gs, pawn492)),
                () -> assertEquals(sa6, ActionEncoder.withOccupantRemoved(gs, pawn540)),
                () -> assertEquals(sa7, ActionEncoder.withOccupantRemoved(gs, pawn713))
        );
    }

    @Test
    void decodeAndApplyWorks() {
        var list = List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.PURPLE);
        var start = List.of(tile56);
        var normal = List.of(tile54, tile65, tile35, tile33, tile37, tile47, tile49, tile27, tile31, tile71, tile72, tile45);
        var menhir = List.of(tile88, tile90);
        var tileDecks = new TileDecks(start, normal, menhir);
        Map<PlayerColor, String> players = new HashMap<>();
        players.put(PlayerColor.RED, "Alice");
        players.put(PlayerColor.BLUE, "Bob");
        players.put(PlayerColor.GREEN, "Carol");
        players.put(PlayerColor.YELLOW, "David");
        players.put(PlayerColor.PURPLE, "Elsa");
        var textMaker = new TextMakerFr(players);
        var gameState = GameState.initial(list, tileDecks, textMaker);

        var pt54 = new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var pt65 = new PlacedTile(tile65, PlayerColor.BLUE, Rotation.NONE, new Pos(1, 0));
        var pt35 = new PlacedTile(tile35, PlayerColor.GREEN, Rotation.NONE, new Pos(1, 1));
        var pt33 = new PlacedTile(tile33, PlayerColor.YELLOW, Rotation.HALF_TURN, new Pos(1, 2));
        var pt37 = new PlacedTile(tile37, PlayerColor.PURPLE, Rotation.HALF_TURN, new Pos(0, 2));
        var pt47 = new PlacedTile(tile47, PlayerColor.RED, Rotation.RIGHT, new Pos(-1, 1));
        var pt49 = new PlacedTile(tile49, PlayerColor.BLUE, Rotation.RIGHT, new Pos(-1, 2));
        var pt27 = new PlacedTile(tile27, PlayerColor.GREEN, Rotation.LEFT, new Pos(-1, 3));
        var pt31 = new PlacedTile(tile31, PlayerColor.YELLOW, Rotation.LEFT, new Pos(2, 2));
        var pt71 = new PlacedTile(tile71, PlayerColor.PURPLE, Rotation.RIGHT, new Pos(0, 3));
        var pt72 = new PlacedTile(tile72, PlayerColor.RED, Rotation.RIGHT, new Pos(1, 3));
        var pt45 = new PlacedTile(tile45, PlayerColor.BLUE, Rotation.LEFT, new Pos(2, 3));
        var pt88 = new PlacedTile(tile88, PlayerColor.BLUE, Rotation.HALF_TURN, new Pos(3, 3));

        var pawn330 = new Occupant(Occupant.Kind.PAWN, 330);
        var hut471 = new Occupant(Occupant.Kind.HUT, 471);
        var pawn492 = new Occupant(Occupant.Kind.PAWN, 492);
        var pawn271 = new Occupant(Occupant.Kind.PAWN, 271);
        var pawn713 = new Occupant(Occupant.Kind.PAWN, 713);
        var pawn721 = new Occupant(Occupant.Kind.PAWN, 721);
        var pawn452 = new Occupant(Occupant.Kind.PAWN, 452);
        var pawn651 = new Occupant(Occupant.Kind.PAWN, 651);

        var placeTile31 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(null)
                .withPlacedTile(pt65).withNewOccupant(pawn651)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37).withNewOccupant(null)
                .withPlacedTile(pt47).withNewOccupant(hut471)
                .withPlacedTile(pt49).withNewOccupant(pawn492)
                .withPlacedTile(pt27).withNewOccupant(pawn271);
        var gs1 = placeTile31.withPlacedTile(pt31);
        var action1 = Base32.encodeBits10(13 << 2 | 0b11);
        var sa1 = new ActionEncoder.StateAction(gs1, action1);

        var placeTile72 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(null)
                .withPlacedTile(pt65).withNewOccupant(pawn651)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37).withNewOccupant(null)
                .withPlacedTile(pt47).withNewOccupant(hut471)
                .withPlacedTile(pt49).withNewOccupant(pawn492)
                .withPlacedTile(pt27).withNewOccupant(pawn271)
                .withPlacedTile(pt31).withNewOccupant(null)
                .withPlacedTile(pt71).withNewOccupant(pawn713);
        var gs2 = placeTile72.withPlacedTile(pt72);
        var action2 = Base32.encodeBits10(10 << 2 | 0b01);
        var sa2 = new ActionEncoder.StateAction(gs2, action2);

        var occupyTile65 = GameState.initial(list, tileDecks, textMaker)
                .withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(null)
                .withPlacedTile(pt65);
        var gs3 = occupyTile65.withNewOccupant(null);
        var action3 = Base32.encodeBits5(0b11111);
        var sa3 = new ActionEncoder.StateAction(gs3, action3);

        var occupyTile49 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(null)
                .withPlacedTile(pt65).withNewOccupant(pawn651)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37).withNewOccupant(null)
                .withPlacedTile(pt47).withNewOccupant(hut471)
                .withPlacedTile(pt49);
        var gs4 = occupyTile49.withNewOccupant(pawn492);
        var action4 = Base32.encodeBits5(2);
        var sa4 = new ActionEncoder.StateAction(gs4, action4);

        var retakePawn = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(null)
                .withPlacedTile(pt65).withNewOccupant(pawn651)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37).withNewOccupant(null)
                .withPlacedTile(pt47).withNewOccupant(hut471)
                .withPlacedTile(pt49).withNewOccupant(pawn492)
                .withPlacedTile(pt27).withNewOccupant(pawn271)
                .withPlacedTile(pt31).withNewOccupant(null)
                .withPlacedTile(pt71).withNewOccupant(pawn713)
                .withPlacedTile(pt72).withNewOccupant(pawn721)
                .withPlacedTile(pt45).withNewOccupant(pawn452)
                .withPlacedTile(pt88);
        var gs5 = retakePawn.withOccupantRemoved(pawn452);
        var action5 = Base32.encodeBits5(0);
        var sa5 = new ActionEncoder.StateAction(gs5, action5);

        var gs6 = retakePawn.withOccupantRemoved(pawn651);
        var action6 = Base32.encodeBits5(2);
        var sa6 = new ActionEncoder.StateAction(gs6, action6);

        assertAll(
                () -> assertEquals(sa1, ActionEncoder.decodeAndApply(placeTile31, action1)),
                () -> assertEquals(sa2, ActionEncoder.decodeAndApply(placeTile72, action2)),
                () -> assertEquals(sa3, ActionEncoder.decodeAndApply(occupyTile65, action3)),
                () -> assertEquals(sa4, ActionEncoder.decodeAndApply(occupyTile49, action4)),
                () -> assertEquals(sa5, ActionEncoder.decodeAndApply(retakePawn, action5)),
                () -> assertEquals(sa6, ActionEncoder.decodeAndApply(retakePawn, action6)),
                () -> assertNull(ActionEncoder.decodeAndApply(retakePawn, Base32.encodeBits5(3))),
                () -> assertNull(ActionEncoder.decodeAndApply(retakePawn, Base32.encodeBits5(4)))
        );
    }

    @Test
    void decodeAndApplyReturnsNull() {
        var list = List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.PURPLE);
        var start = List.of(tile56);
        var normal = List.of(tile54, tile65, tile35, tile33, tile37, tile47, tile49, tile27, tile31, tile71, tile72, tile45);
        var menhir = List.of(tile88, tile90);
        var tileDecks = new TileDecks(start, normal, menhir);
        Map<PlayerColor, String> players = new HashMap<>();
        players.put(PlayerColor.RED, "Alice");
        players.put(PlayerColor.BLUE, "Bob");
        players.put(PlayerColor.GREEN, "Carol");
        players.put(PlayerColor.YELLOW, "David");
        players.put(PlayerColor.PURPLE, "Elsa");
        var textMaker = new TextMakerFr(players);
        var gameState = GameState.initial(list, tileDecks, textMaker);

        var pt54 = new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var pt65 = new PlacedTile(tile65, PlayerColor.BLUE, Rotation.NONE, new Pos(1, 0));
        var pt35 = new PlacedTile(tile35, PlayerColor.GREEN, Rotation.NONE, new Pos(1, 1));
        var pt33 = new PlacedTile(tile33, PlayerColor.YELLOW, Rotation.HALF_TURN, new Pos(1, 2));
        var pt37 = new PlacedTile(tile37, PlayerColor.PURPLE, Rotation.HALF_TURN, new Pos(0, 2));
        var pt47 = new PlacedTile(tile47, PlayerColor.RED, Rotation.RIGHT, new Pos(-1, 1));
        var pt49 = new PlacedTile(tile49, PlayerColor.BLUE, Rotation.RIGHT, new Pos(-1, 2));
        var pt27 = new PlacedTile(tile27, PlayerColor.GREEN, Rotation.LEFT, new Pos(-1, 3));
        var pt31 = new PlacedTile(tile31, PlayerColor.YELLOW, Rotation.LEFT, new Pos(2, 2));
        var pt71 = new PlacedTile(tile71, PlayerColor.PURPLE, Rotation.RIGHT, new Pos(0, 3));
        var pt72 = new PlacedTile(tile72, PlayerColor.RED, Rotation.RIGHT, new Pos(1, 3));
        var pt45 = new PlacedTile(tile45, PlayerColor.BLUE, Rotation.LEFT, new Pos(2, 3));
        var pt88 = new PlacedTile(tile88, PlayerColor.BLUE, Rotation.HALF_TURN, new Pos(3, 3));

        var pawn540 = new Occupant(Occupant.Kind.PAWN, 540);
        var pawn330 = new Occupant(Occupant.Kind.PAWN, 330);
        var hut471 = new Occupant(Occupant.Kind.HUT, 471);
        var pawn492 = new Occupant(Occupant.Kind.PAWN, 492);
        var pawn271 = new Occupant(Occupant.Kind.PAWN, 271);
        var pawn713 = new Occupant(Occupant.Kind.PAWN, 713);
        var pawn721 = new Occupant(Occupant.Kind.PAWN, 721);
        var hut451 = new Occupant(Occupant.Kind.HUT, 451);

        var placeTile31 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(pawn540)
                .withPlacedTile(pt65).withNewOccupant(null)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37).withNewOccupant(null)
                .withPlacedTile(pt47).withNewOccupant(hut471)
                .withPlacedTile(pt49).withNewOccupant(pawn492)
                .withPlacedTile(pt27).withNewOccupant(pawn271);
        var action1 = Base32.encodeBits10(13 << 2 | 0b11);

        var placeTile72 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(pawn540)
                .withPlacedTile(pt65).withNewOccupant(null)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37).withNewOccupant(null)
                .withPlacedTile(pt47).withNewOccupant(hut471)
                .withPlacedTile(pt49).withNewOccupant(pawn492)
                .withPlacedTile(pt27).withNewOccupant(pawn271)
                .withPlacedTile(pt31).withNewOccupant(null)
                .withPlacedTile(pt71).withNewOccupant(pawn713);

        var occupyTile65 = GameState.initial(list, tileDecks, textMaker)
                .withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(pawn540)
                .withPlacedTile(pt65);

        var occupyTile49 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(pawn540)
                .withPlacedTile(pt65).withNewOccupant(null)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37).withNewOccupant(null)
                .withPlacedTile(pt47).withNewOccupant(hut471)
                .withPlacedTile(pt49);

        var retakePawn = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(pawn540)
                .withPlacedTile(pt65).withNewOccupant(null)
                .withPlacedTile(pt35).withNewOccupant(null)
                .withPlacedTile(pt33).withNewOccupant(pawn330)
                .withPlacedTile(pt37).withNewOccupant(null)
                .withPlacedTile(pt47).withNewOccupant(hut471)
                .withPlacedTile(pt49).withNewOccupant(pawn492)
                .withPlacedTile(pt27).withNewOccupant(pawn271)
                .withPlacedTile(pt31).withNewOccupant(null)
                .withPlacedTile(pt71).withNewOccupant(pawn713)
                .withPlacedTile(pt72).withNewOccupant(pawn721)
                .withPlacedTile(pt45).withNewOccupant(hut451)
                .withPlacedTile(pt88);
        var action5 = Base32.encodeBits5(3);

        var action6 = Base32.encodeBits5(1);

        assertAll(
                () -> assertNull(ActionEncoder.decodeAndApply(placeTile31, action6)),
                () -> assertNull(ActionEncoder.decodeAndApply(placeTile72, action5)),
                () -> assertNull(ActionEncoder.decodeAndApply(placeTile72, Base32.encodeBits10(30 << 2 | 0b11))),
                () -> assertNull(ActionEncoder.decodeAndApply(occupyTile49, action1)),
                () -> assertNull(ActionEncoder.decodeAndApply(occupyTile65, action5)),
                () -> assertNull(ActionEncoder.decodeAndApply(retakePawn, action1)),
                () -> assertNull(ActionEncoder.decodeAndApply(retakePawn, Base32.encodeBits5(6))),
                () -> assertNull(ActionEncoder.decodeAndApply(gameState, "a")),
                () -> assertNull(ActionEncoder.decodeAndApply(placeTile31, "&"))
        );
    }

    @Test
    void zdecodeAndApplyWithPlacedTile() {
        Map<PlayerColor, String> players = new HashMap<>();
        players.put(PlayerColor.RED, "Rose");
        players.put(PlayerColor.BLUE, "Bernard");
        var list = List.of(PlayerColor.RED, PlayerColor.BLUE);
        var start = List.of(tile56);
        var normal = List.of(tile54, tile65, tile25, tile44, tile62);
        var menhir = List.of(tile88, tile90);
        var tileDecks = new TileDecks(start, normal, menhir);
        var textMaker = new TextMakerFr(players);
        var gameState = GameState.initial(list, tileDecks, textMaker);

        PlacedTile pt54 = new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        PlacedTile pt65 = new PlacedTile(tile65, PlayerColor.BLUE, Rotation.NONE, new Pos(1, 0));
        PlacedTile pt25 = new PlacedTile(tile25, PlayerColor.RED, Rotation.LEFT, new Pos(0, -1));
        PlacedTile pt44 = new PlacedTile(tile44, PlayerColor.BLUE, Rotation.NONE, new Pos(1, 1));

        var gs1 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54).withNewOccupant(null)
                .withPlacedTile(pt65).withNewOccupant(null);
        var action1 = Base32.encodeBits10(0b00000_11000);
        var action2 = Base32.encodeBits10(0b11000_00000);

        assertAll(
                () -> assertNull(ActionEncoder.decodeAndApply(gs1, action1)),
                () -> assertNull(ActionEncoder.decodeAndApply(gs1, action2))
        );
    }

    @Test
    void decodeAndApplyWithNewOccupant() {
        Map<PlayerColor, String> players = new HashMap<>();
        players.put(PlayerColor.RED, "Rose");
        players.put(PlayerColor.BLUE, "Bernard");
        var list = List.of(PlayerColor.RED, PlayerColor.BLUE);
        var start = List.of(tile56);
        var normal = List.of(tile54, tile65, tile25, tile44, tile62);
        var menhir = List.of(tile88, tile90);
        var tileDecks = new TileDecks(start, normal, menhir);
        var textMaker = new TextMakerFr(players);
        var gameState = GameState.initial(list, tileDecks, textMaker);

        var pt54 = new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var pt47 = new PlacedTile(tile47, PlayerColor.BLUE, Rotation.LEFT, new Pos(-1, 1));
        var pt71 = new PlacedTile(tile71, PlayerColor.RED, Rotation.NONE, new Pos(-1, 2));

        var pawn542 = new Occupant(Occupant.Kind.PAWN, 542);

        var gs1 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt54);
        var gs2 = gs1.withNewOccupant(pawn542)
                .withPlacedTile(pt47);
        var gs3 = gs2.withNewOccupant(null)
                .withPlacedTile(pt71);

        var action1 = Base32.encodeBits5(0b10000);
        var action2 = Base32.encodeBits5(0b10010);
        var action3 = Base32.encodeBits5(0b01000);
        var action4 = Base32.encodeBits5(0b11100);

        assertAll(
                () -> assertNull(ActionEncoder.decodeAndApply(gs1, action1)),
                () -> assertNull(ActionEncoder.decodeAndApply(gs2, action2)),
                () -> assertNull(ActionEncoder.decodeAndApply(gs3, action3)),
                () -> assertNull(ActionEncoder.decodeAndApply(gs2, action4))
        );
    }

    @Test
    void decodeAndApplyWithOccupandRemoved() {
        Map<PlayerColor, String> players = new HashMap<>();
        players.put(PlayerColor.RED, "Rose");
        players.put(PlayerColor.BLUE, "Bernard");
        var list = List.of(PlayerColor.RED, PlayerColor.BLUE);
        var start = List.of(tile56);
        var normal = List.of(tile54, tile65, tile25, tile44, tile62);
        var menhir = List.of(tile88, tile90);
        var tileDecks = new TileDecks(start, normal, menhir);
        var textMaker = new TextMakerFr(players);
        var gameState = GameState.initial(list, tileDecks, textMaker);

        var pt35 = new PlacedTile(tile35, PlayerColor.RED, Rotation.HALF_TURN, new Pos(1,0));
        var pt37 = new PlacedTile(tile37, PlayerColor.BLUE, Rotation.NONE, new Pos(0, 1));
        var pt88 = new PlacedTile(tile88, PlayerColor.RED, Rotation.NONE, new Pos(-1,0));

        var pawn350 = new Occupant(Occupant.Kind.PAWN, 350);
        var pawn371 = new Occupant(Occupant.Kind.PAWN, 371);

        var gs1 = gameState.withStartingTilePlaced()
                .withPlacedTile(pt35).withNewOccupant(pawn350)
                .withPlacedTile(pt37).withNewOccupant(pawn371)
                .withPlacedTile(pt88);

        var action1 = Base32.encodeBits5(30);
        var action2 = Base32.encodeBits5(0);
        var action3 = Base32.encodeBits5(4);

        assertAll(
                () -> assertNull(ActionEncoder.decodeAndApply(gs1, action1)),
                () -> assertNull(ActionEncoder.decodeAndApply(gs1, action2)),
                () -> assertNull(ActionEncoder.decodeAndApply(gs1, action3))
        );
    }

}