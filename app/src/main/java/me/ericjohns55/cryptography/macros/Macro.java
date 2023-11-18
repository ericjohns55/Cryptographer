package me.ericjohns55.cryptography.macros;

import java.util.ArrayList;

import me.ericjohns55.cryptography.ciphers.Flags;
import me.ericjohns55.cryptography.ciphers.TextCases;

/**
 * The Macro class represents a collection of Ciphers that can be run at once to encode
 * or decode input text
 *
 * @author Eric Johns
 */

public class Macro {
    private final ArrayList<MacroItem> macroItems;
    private String output;

    /**
     * Default constructor that creates an empty macro with no output
     */
    public Macro() {
        this.macroItems = new ArrayList<>();
        this.output = "This macro has not been run yet.";
    }

    /**
     * Returns the output of the macro (it must be ran before getting legitimate values)
     */
    public String getOutput() {
        return output;
    }

    /**
     * Return the number of Ciphers contained within this Macro
     */
    public int getMacroSize() {
        return macroItems.size();
    }

    /**
     * Adds a new Cipher to this MacroItem
     * @param item The item to add
     */
    public void addMacroItem(MacroItem item) {
        macroItems.add(item);
    }

    /**
     * Returns the MacroItem at the specified index within this Macro
     * @param index The index of the MacroItem to get
     */
    public MacroItem getMacroItem(int index) {
        return macroItems.get(index);
    }

    /**
     * Returns true if all macro items have valid arguments, false otherwise
     */
    public boolean validate() {
        for (MacroItem item : macroItems) {
            if (!item.validateArguments()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Runs the macro item and stores the output in the output field
     * Call getOutput() to retrieve the output
     * @param input The input text to run the macro on
     * @param forwards True if we are encoding, false if we are decoding
     */
    public void runMacro(String input, boolean forwards) {
        if (forwards) {
            for (int i = 0; i < getMacroSize(); i++) {
                input = macroItems.get(i).run(input, true);
            }
        } else {
            for (int i = getMacroSize() - 1; i >= 0; i--) {
                MacroItem currentItem = macroItems.get(i);
                Flags currentItemFlags = currentItem.getFlags();

                currentItemFlags.setTextCase(TextCases.reverseCase(currentItemFlags.getTextCase()));
                input = macroItems.get(i).run(input, false);
                currentItemFlags.setTextCase(TextCases.reverseCase(currentItemFlags.getTextCase()));
            }
        }

        this.output = input;
    }
}
