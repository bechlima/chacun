package ch.epfl.chacun;

/**
 * Utility class for Base32 encoding and decoding.
 *
 * @author Imad Bechlaghem (375401)
 */
public final class Base32 {

    // Private constructor to prevent instantiation
    private Base32() {}

    /**
     * The Base32 alphabet.
     */
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private static final int BASE_LENGTH = 32;

    /**
     * Checks if the given string is a valid Base32 encoded string.
     *
     * @param string the string to check
     * @return true if the string is valid Base32, false otherwise
     */
    public static boolean isValid(String string) {
        if (string == null) {
            return false;
        }

        for (Character c : string.toCharArray()) {
            if (!ALPHABET.contains(Character.toString(c))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Encodes a 5-bit integer to a Base32 character.
     *
     * @param i the 5-bit integer to encode
     * @return the Base32 encoded character
     * @throws IllegalArgumentException if the integer is not in the range [0, 31]
     */
    public static String encodeBits5(int i) {
        if (i > 31 || i < 0) {
            throw new IllegalArgumentException("The integer must be in the range [0, 31]");
        }

        return String.valueOf(ALPHABET.charAt(i));
    }

    /**
     * Encodes a 10-bit integer to two Base32 characters.
     *
     * @param i the 10-bit integer to encode
     * @return the Base32 encoded string
     * @throws IllegalArgumentException if the integer is not in the range [0, 1023]
     */
    public static String encodeBits10(int i) {
        if (i > 1023 || i < 0) {
            throw new IllegalArgumentException("The integer must be in the range [0, 1023]");
        }

        int secondChar = (i >> 5);
        int firstChar = i & 31;

        return ALPHABET.charAt(secondChar) + String.valueOf(ALPHABET.charAt(firstChar));
    }

    /**
     * Decodes a Base32 encoded string to an integer.
     *
     * @param s the Base32 encoded string
     * @return the decoded integer
     * @throws IllegalArgumentException if the string is invalid, empty, or longer than 2 characters
     */
    public static int decode(String s) {
        if ((!isValid(s)) || s.isEmpty() || s.length() >= 3) {
            throw new IllegalArgumentException("Invalid Base32 string");
        }

        int i = 0;
        int power = 0;
        for (int j = s.length() - 1; j >= 0; --j) {
            i += (int) (ALPHABET.indexOf(s.charAt(j)) * Math.pow(BASE_LENGTH, power));
            ++power;
        }
        return i;
    }
}
