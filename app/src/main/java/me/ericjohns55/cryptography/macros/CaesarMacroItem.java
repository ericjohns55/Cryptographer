package me.ericjohns55.cryptography.macros;

import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;

/**
 * Implements the MacroItem methods for an Caesar cipher variant
 *
 * @author Eric Johns
 */

public class CaesarMacroItem extends MacroItem {
    private int rotateAmount;

    /**
     * Default constructor that takes in a rotate amount and encoding flags
     * @param rotateAmount Amount to rotate
     * @param flags Encoding flags
     */
    public CaesarMacroItem(int rotateAmount, Flags flags) {
        super(Ciphers.CAESAR, flags);
        this.rotateAmount = rotateAmount;
    }

    /**
     * Updates the rotate amount for the macro item
     * @param rotateAmount New rotate amount
     */
    public void setRotateAmount(int rotateAmount) {
        this.rotateAmount = rotateAmount;
    }

    /**
     * Returns the rotate amount for this MacroItem
     */
    public int getRotateAmount() {
        return rotateAmount;
    }

    /**
     * Runs the macro item
     * @param input The input to run on
     * @param forwards True if we are encoding, false if we are decoding
     * @return The encoded/decoded text
     */
    @Override
    public String run(String input, boolean forwards) {
        int rotate = getRotateAmount();
        if (!forwards) rotate *= -1;

        return Ciphers.caesarCipher(input, rotate, getFlags());
    }

    /**
     * This is always true for the Caesar MacroItem because any integer is valid
     */
    @Override
    public boolean validateArguments() {
        return true;
    }

    /**
     * Returns a string representation of the rotateAmount field
     */
    @Override
    public String getExtraArgument() {
        return String.valueOf(rotateAmount);
    }

    /**
     * Returns a formatted description of the extra arguments for this MacroItem
     */
    @Override
    public String parseExtraArgument() {
        return "Rotation: " + getExtraArgument();
    }
}
