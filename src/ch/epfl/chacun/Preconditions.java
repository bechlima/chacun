package ch.epfl.chacun;


/**
 * Utility class for precondition checks.
 *
 * @author Imad Bechlaghem (375401)
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Checks if the given condition is true.
     *
     * @param shouldBeTrue The condition to check.
     * @throws IllegalArgumentException if the condition is false.
     */
    static public void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}