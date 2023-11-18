package me.ericjohns55.cryptography.macros;

import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;

/**
 * Implements the MacroItem methods for an Substitution cipher variant
 *
 * @author Eric Johns
 */

public class SubstitutionMacroItem extends MacroItem {
    private String alphabet;

    /**
     * Default constructor, takes in an alphabet and encoding flags
     * @param alphabet Alphabet to encode with
     * @param flags Encoding flags
     */
    public SubstitutionMacroItem(String alphabet, Flags flags) {
        super(Ciphers.SUBSTITUTION, flags);
        this.alphabet = alphabet;
    }

    /**
     * Returns the alphabet present in this MacroItem
     */
    public String getAlphabet() {
        return alphabet;
    }

    /**
     * Updates the alphabet in the MacroItem
     * @param alphabet The new alphabet
     */
    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }

    /**
     * Runs the macro item
     * @param input The input to run on
     * @param forwards True if we are encoding, false if we are decoding
     * @return The encoded/decoded text
     */
    @Override
    public String run(String input, boolean forwards) {
        return Ciphers.substitutionCipher(input, getAlphabet(), getFlags(), !forwards);
    }

    /**
     * Returns true if there is a valid alphabet contained in the MacroItem
     */
    @Override
    public boolean validateArguments() {
        return Ciphers.validAlphabet(alphabet);
    }

    /**
     * Returns the alphabet field contained in this MacroItem
     */
    @Override
    public String getExtraArgument() {
        return alphabet;
    }

    /**
     * Returns a formatted description of the extra arguments for this MacroItem
     */
    @Override
    public String parseExtraArgument() {
        return "Alphabet: " + getExtraArgument();
    }
}
