package me.ericjohns55.cryptography.macros;

import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;

/**
 * Implements the MacroItem methods for an Vigenère cipher variant
 *
 * @author Eric Johns
 */

public class VigenereMacroItem extends MacroItem {
    private String key;

    /**
     * Default constructor, takes a secret key and encoding flags
     * @param key The key to encode/decode with
     * @param flags Encoding flags
     */
    public VigenereMacroItem(String key, Flags flags) {
        super(Ciphers.VIGENERE, flags);
        this.key = key;
    }

    /**
     * Returns the key for this MacroItem
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * Updates the key for the MacroItem
     * @param key The new key to update with
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Runs the macro item
     * @param input The input to run on
     * @param forwards True if we are encoding, false if we are decoding
     * @return The encoded/decoded text
     */
    @Override
    public String run(String input, boolean forwards) {
        return Ciphers.vigenereCipher(input, getKey(), getFlags(), forwards);
    }

    /**
     * Returns true if there is a valid key contained in the MacroItem
     */
    @Override
    public boolean validateArguments() {
        return key.length() != 0;
    }

    /**
     * Returns the alphabet field contained in this MacroItem
     */
    @Override
    public String getExtraArgument() {
        return key;
    }

    /**
     * Returns a formatted description of the extra arguments for this MacroItem
     */
    @Override
    public String parseExtraArgument() {
        return "Key: " + getExtraArgument();
    }
}
