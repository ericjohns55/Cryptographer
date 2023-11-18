package me.ericjohns55.cryptography.ciphers;

import androidx.annotation.NonNull;

/**
 * Provides encoding flags passed as a parameter to each Cipher type
 *
 * @author Eric Johns
 */

public class Flags {
    private TextCases textCase;
    private boolean preserveCharacters;
    private boolean rotateNumbers;

    /**
     * Public constructor that takes in all flag information at once
     * @param textCase The text case of the output
     * @param preserveCharacters Boolean value of whether we should preserve characters
     *                          or strip them
     * @param rotateNumbers BOolean value of whether or not we should rotate numbers
     *                      within the ciphers or not
     */
    public Flags(TextCases textCase, boolean preserveCharacters, boolean rotateNumbers) {
        this.textCase = textCase;
        this.preserveCharacters = preserveCharacters;
        this.rotateNumbers = rotateNumbers;
    }

    /**
     * Default constructor that creates flags that preserve text case, characters, and
     * does not rotate numbers
     */
    public Flags() {
        this(TextCases.PRESERVE_CASE, true, false);
    }

    /**
     * Returns the flags resulting TextCase
     */
    public TextCases getTextCase() {
        return textCase;
    }

    /**
     * Updates the flags resulting TextCase
     * @param textCase New resulting TextCase
     */
    public void setTextCase(TextCases textCase) {
        this.textCase = textCase;
    }

    /**
     * Returns true if we should preserve characters, false otherwise
     */
    public boolean preserveCharacters() {
        return preserveCharacters;
    }

    /**
     * Updates whether or not we should preserve characters or not while encoding
     * @param preserveCharacters True if we should preserve characters, false otherwise
     */
    public void setPreserveCharacters(boolean preserveCharacters) {
        this.preserveCharacters = preserveCharacters;
    }

    /**
     * Returns true if we should rotate numbers while encoding, false otherwise
     */
    public boolean rotateNumbers() {
        return rotateNumbers;
    }

    /**
     * Updates whether or not we should rotate numbers while encoding
     * @param rotateNumbers True if we should rotate numbers, false otherwise
     */
    public void setRotateNumbers(boolean rotateNumbers) {
        this.rotateNumbers = rotateNumbers;
    }

    /**
     * Resets the Flag's values to the default information
     */
    public void reset() {
        this.textCase = TextCases.PRESERVE_CASE;
        this.preserveCharacters = true;
        this.rotateNumbers = false;
    }

    /**
     * Prints out debugging information including all Flag information
     */
    @NonNull
    public String toString() {
        return "[text_case: " + textCase + "; preserveCharacters: " +
                preserveCharacters + "; rotateNumbers: " + rotateNumbers + "]";
    }
}
