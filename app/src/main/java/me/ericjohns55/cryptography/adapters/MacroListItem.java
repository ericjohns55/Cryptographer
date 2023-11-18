package me.ericjohns55.cryptography.adapters;

import me.ericjohns55.cryptography.macros.MacroItem;

/**
 * Provides an interface between the ListView and a MacroItem for displaying
 * information in a ListView row
 *
 * @author Eric Johns
 */

public class MacroListItem {
    // Name of the MacroListItem
    private String name;

    // Embedded MacroItem with cipher information
    private MacroItem macroItem;

    /**
     * Default constructor for a MacroListItem
     * @param name The name of the Cipher the MacroItem corresponds to
     * @param macroItem The MacroItem that holds information for the cipher
     */
    public MacroListItem(String name, MacroItem macroItem) {
        this.name = name;
        this.macroItem = macroItem;
    }

    /**
     * Returns the name of the MacroListItem
     * This is really the Cipher type for the MacroItem
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the name of the item
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the back end MacroItem
     * @param macroItem The new item to replace
     */
    public void setMacroItem(MacroItem macroItem) {
        this.macroItem = macroItem;
    }

    /**
     * Converts the extra arguments in a MacroItem into a string for display in a ListView
     * @return Parsed arguments for ListView
     */
    public String parseExtraArgument() {
        return macroItem.parseExtraArgument();
    }

    /**
     * Returns the MacroItem contained in this row of the ListView
     */
    public MacroItem getMacroItem() {
        return macroItem;
    }
}
