package me.ericjohns55.cryptography.ciphers;

/**
 * Provides an Enum for the 3 possible cases of Text that a cipher can return
 *
 * @author Eric Johns
 */

public enum TextCases {

    PRESERVE_CASE,
    ALL_UPPER,
    ALL_LOWER;

    // Array of all values within the enum
    private static final TextCases[] values = TextCases.values();

    /**
     * Converts the given position into a value within the Enum
     */
    public static TextCases fromInt(int pos) {
        return values[pos];
    }

    /**
     * Converts a TextCase into an integer value
     */
    public static int toInt(TextCases textCases) {
        if (textCases == PRESERVE_CASE) {
            return 0;
        } else if (textCases == ALL_UPPER) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Reverses the input Text Case
     * @param input The text case to return
     * @return The reverse of the input Text Case
     */
    public static TextCases reverseCase(TextCases input) {
        switch (input) {
            case ALL_LOWER:
                return ALL_UPPER;
            case ALL_UPPER:
                return ALL_LOWER;
            default:
                return PRESERVE_CASE;
        }
    }
}
