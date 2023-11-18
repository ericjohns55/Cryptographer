package me.ericjohns55.cryptography.macros;

import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;

/**
 * Implements the MacroItem methods for an Atbash cipher variant
 *
 * @author Eric Johns
 */

public class AtbashMacroItem extends MacroItem {
    /**
     * Default constructor that takes only input flags
     * @param flags Encoding flags
     */
    public AtbashMacroItem(Flags flags) {
        super(Ciphers.ATBASH, flags);
    }

    /**
     * Runs the macro item
     * @param input The input to run on
     * @param forwards True if we are encoding, false if we are decoding
     * @return The encoded/decoded text
     */
    @Override
    public String run(String input, boolean forwards) {
        return Ciphers.atbashCipher(input, getFlags());
    }

    /**
     * This is always true for the Atbash MacroItem because there are no arguments
     */
    @Override
    public boolean validateArguments() {
        return true;
    }

    /**
     * Returns an empty string for this MacroItem as there are no extra arguments
     */
    @Override
    public String getExtraArgument() {
        return "";
    }

    /**
     * Returns a string representation of the extra arguments
     * This is none for the Atbash variant of a MacroItem
     */
    @Override
    public String parseExtraArgument() {
        return "No extra arguments";
    }
}
