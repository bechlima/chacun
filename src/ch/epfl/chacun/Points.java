package ch.epfl.chacun;

/**
 * Utility class for calculating points based on various game elements.
 *
 * @author Imad Bechlaghem (375401)
 */
public final class Points {

    private static final int MAMMOUTH_POINTS = 3;
    private static final int AUROCH_POINTS = 2;
    private static final int DEER_POINTS = 1;
    private static final int FISH_POINTS = 1;
    private static final int CLOSED_FOREST_POINTS = 2;
    private static final int MUSHROOM_POINTS = 3;
    private static final int CLOSED_RIVER_POINTS = 1;
    private static final int LOGBOAT_POINTS = 2;
    private static final int RAFT_POINTS = 1;


    private Points() {
    }

    /**
     * Calculates points for closed forest based on tile count and mushroom group count.
     *
     * @param tileCount          The number of tiles in the closed forest.
     * @param mushroomGroupCount The number of mushroom groups in the closed forest.
     * @return The points for the closed forest.
     * @throws IllegalArgumentException if tileCount is not greater than 1 or mushroomGroupCount is negative.
     * @author Imad Bechlaghem (375401)
     */
    public static int forClosedForest(int tileCount, int mushroomGroupCount) {
        Preconditions.checkArgument(tileCount > 1 && mushroomGroupCount >= 0);
        return tileCount * CLOSED_FOREST_POINTS +
                mushroomGroupCount * MUSHROOM_POINTS;
    }

    /**
     * Calculates points for closed river based on tile count and fish count.
     *
     * @param tileCount The number of tiles in the closed river.
     * @param fishCount The number of fish in the closed river.
     * @return The points for the closed river.
     * @throws IllegalArgumentException if tileCount is not greater than 1 or fishCount is negative.
     * @author Imad Bechlaghem (375401)
     */
    public static int forClosedRiver(int tileCount, int fishCount) {
        Preconditions.checkArgument(tileCount > 1 && fishCount >= 0);
        return tileCount * CLOSED_RIVER_POINTS +
                fishCount * FISH_POINTS;
    }

    /**
     * Calculates points for meadow based on mammoth count, aurochs count, and deer count.
     *
     * @param mammothCount The number of mammoths in the meadow.
     * @param aurochsCount The number of aurochs in the meadow.
     * @param deerCounts   The number of deer in the meadow.
     * @return The points for the meadow.
     * @throws IllegalArgumentException if mammothCount, aurochsCount, or deerCounts is negative.
     * @author Imad Bechlaghem (375401)
     */
    public static int forMeadow(int mammothCount, int aurochsCount, int deerCounts) {
        Preconditions.checkArgument(
                mammothCount >= 0
                        && aurochsCount >= 0
                        && deerCounts >= 0);
        return mammothCount * MAMMOUTH_POINTS +
                aurochsCount * AUROCH_POINTS +
                deerCounts * DEER_POINTS;
    }

    /**
     * Calculates points for river system based on fish count.
     *
     * @param fishCount The number of fish in the river system.
     * @return The points for the river system.
     * @throws IllegalArgumentException if fishCount is negative.
     * @author Imad Bechlaghem (375401)
     */
    public static int forRiverSystem(int fishCount) {
        Preconditions.checkArgument(fishCount >= 0);
        return fishCount * FISH_POINTS;
    }

    /**
     * Calculates points for logboat based on lake count.
     *
     * @param lakeCount The number of lakes with logboats.
     * @return The points for the logboat.
     * @throws IllegalArgumentException if lakeCount is not greater than 0.
     * @author Imad Bechlaghem (375401)
     */
    public static int forLogboat(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        return lakeCount * LOGBOAT_POINTS;
    }

    /**
     * Calculates points for raft based on lake count.
     *
     * @param lakeCount The number of lakes with rafts.
     * @return The points for the raft.
     * @throws IllegalArgumentException if lakeCount is not greater than 0.
     * @author Imad Bechlaghem (375401)
     */
    public static int forRaft(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        return lakeCount * RAFT_POINTS;
    }
}
