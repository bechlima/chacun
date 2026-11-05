package ch.epfl.chacun;

import java.util.Objects;

/**
 * Represents an occupant in a specific zone.
 *
 * @author Imad Bechlaghem (375401)
 */
public record Occupant(Kind kind, int zoneId) {

    /**
     * Enum representing the kind of occupant.
     */
    public enum Kind {
        /**
         * A pawn occupant.
         */
        PAWN,
        /**
         * A hut occupant.
         */
        HUT
    }

    /**
     * Constructs an occupant with the specified kind and zone ID.
     *
     * @param kind   The kind of occupant.
     * @param zoneId The zone ID where the occupant is located.
     * @throws NullPointerException     if the kind is null.
     * @throws IllegalArgumentException if the zone ID is negative.
     */
    public Occupant {
        Objects.requireNonNull(kind);
        Preconditions.checkArgument(zoneId >= 0);
    }

    /**
     * Returns the count of occupants of the specified kind.
     *
     * @param kind The kind of occupant.
     * @return The count of occupants for the specified kind.
     */
    public static int occupantsCount(Kind kind) {
        return switch (kind) {
            case PAWN -> 5;
            case HUT -> 3;
        };
    }
}