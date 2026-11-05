package ch.epfl.chacun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a tile with four sides, each side containing specific zones.
 *
 * @author Imad Bechlaghem (375401)
 * @author Tino Bianconi (379733)
 */
public record Tile(
        int id,
        Kind kind,
        TileSide n,
        TileSide e,
        TileSide s,
        TileSide w
) {

    /**
     * Represents the kind of the tile, which can be START, NORMAL, or MENHIR.
     *
     * @author Imad Bechlaghem (375401)
     */
    public enum Kind {
        START,

        NORMAL,

        MENHIR
    }

    /**
     * Retrieves a list of all sides of the tile.
     *
     * @return The list of all sides of the tile.
     * @author Imad Bechlaghem (375401)
     */
    public List<TileSide> sides() {
        return List.of(n, e, s, w);
    }

    /**
     * Retrieves a set of all zones contained in the sides of the tile.
     *
     * @return The set of all zones contained in the sides of the tile.
     * @author Imad Bechlaghem (375401)
     */
    public Set<Zone> sideZones() {
        Set<Zone> sideZones = new HashSet<>();

        for (TileSide tileSide : sides()) {
            sideZones.addAll(tileSide.zones());
        }

        return sideZones;
    }

    /**
     * Retrieves a set of all zones contained in the sides of the tile, including any additional zones like lakes.
     *
     * @return The set of all zones contained in the sides of the tile.
     * @author Tino Bianconi (379733)
     */
    public Set<Zone> zones() {
        Set<Zone> zoneSet = new HashSet<>();

        for (Zone zone : sideZones()) {
            zoneSet.add(zone);
            if (zone instanceof Zone.River river && river.hasLake()) {
                zoneSet.add(river.lake());
            }
        }

        return zoneSet;
    }

}
