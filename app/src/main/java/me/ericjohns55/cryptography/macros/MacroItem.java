package me.ericjohns55.cryptography.macros;

import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;

/**
 * Abstract class for MacroItems
 * This will be implemented for each Cipher type supported by this project
 *
 * @author Eric Johns
 */

public abstract class MacroItem {
    private final Ciphers cipherType;
    private Flags flags;

    /**
     * Default constructor that must have a cipher type and encoding flags
     * @param cipherType The type of cipher this item represents
     * @param flags The encoding flags for the MacroItem
     */
    public MacroItem(Ciphers cipherType, Flags flags) {
        this.flags = flags;
        this.cipherType = cipherType;
    }

    /**
     * Returns the encoding flags for this MacroItem
     */
    public Flags getFlags() {
        return flags;
    }

    /**
     * Updates the encoding flags for this MacroItem
     */
    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    /**
     * Returns the type of Cipher this MacroItem represents
     */
    public Ciphers getType() {
        return cipherType;
    }

    /**
     * Runs the macro item
     * @param input The input text to run the MacroItem on
     * @param forwards True if we are encoding, false if we are decoding
     */
    public abstract String run(String input, boolean forwards);

    /**
     * Returns true if the extra arguments within a MacroItem are valid
     */
    public abstract boolean validateArguments();

    /**
     * Returns the extra arguments embedded in a MacroItem
     */
    public abstract String getExtraArgument();

    /**
     * Returns a formatted string containing the extra arguments in a MacroItem
     */
    public abstract String parseExtraArgument();
}
