package me.ericjohns55.cryptography.macros;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.ericjohns55.cryptography.R;
import me.ericjohns55.cryptography.adapters.GsonMacroAdapter;
import me.ericjohns55.cryptography.adapters.MacroListItem;
import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;

/**
 * The MacroManager class manages all of the Macros contained within an application
 * It handles the creation, deletion, saving, and retrieving of all Macros
 *
 * @author Eric Johns
 */

public class MacroManager {
    private final SharedPreferences sharedPreferences;
    private final String prefsKey;

    private final HashMap<String, Macro> macros;

    private String selectorViewSelection = null;

    /**
     * Constructor that takes in SharedPreferences object and key to pull Macro data
     * from application memory
     * @param sharedPreferences The preferences storage system to store and load from
     * @param prefsKey The identifying key used to retrieve data
     */
    public MacroManager(SharedPreferences sharedPreferences, String prefsKey) {
        this.sharedPreferences = sharedPreferences;
        this.prefsKey = prefsKey;

        String macroObject = sharedPreferences.getString(prefsKey, "");

        // If we found saved macros, decode using Gson
        if (!macroObject.equals("")) {
            Gson gson = getGsonObject();
            Type type = new TypeToken<HashMap<String, Macro>>(){}.getType();
            macros = gson.fromJson(macroObject, type);
        } else { // no saved macros found, create an empty map
            macros = new HashMap<>();
        }
    }

    /**
     * Generates the Gson object used for serializing and deserializing Macro data
     */
    private Gson getGsonObject() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(MacroItem.class,
                new GsonMacroAdapter<Macro>());
        gsonBuilder.setPrettyPrinting();

        return gsonBuilder.create();
    }

    /**
     * Saves all macro data to system preferences
     */
    public void saveMacros() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(MacroItem.class,
                new GsonMacroAdapter<Macro>());
        gsonBuilder.setPrettyPrinting();

        String macrosObject = getGsonObject().toJson(macros);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(prefsKey, macrosObject);
        editor.apply();
    }

    /**
     * Returns an array of all the macros that exist within the manager
     */
    public String[] getMacroNames() {
        return macros.keySet().toArray(new String[0]);
    }

    /**
     * Returns true if a macro with the given name exists within the manager
     * @param name The macro name to check
     */
    public boolean macroExists(String name) {
        return macros.containsKey(name);
    }

    /**
     * Returns the macro with a given name
     * It is recommended to call macroExists(name) before this method
     * @param name The name of the macro to grab
     */
    public Macro getMacro(String name) {
        return macros.get(name);
    }

    /**
     * Returns true if no macros exist, false otherwise
     */
    public boolean isEmpty() {
        return macros.isEmpty();
    }

    /**
     * Adds a new macro to the manager and saves it to system preferences
     * @param macroName The name of the new Macro
     * @param macro The Macro object to add
     */
    public void addMacro(String macroName, Macro macro) {
        macros.put(macroName, macro);
        saveMacros();
    }

    /**
     * Deletes a macro from the manager and commits the deletion to system preferences
     * @param macroName The name of the Macro to delete
     */
    public void deleteMacro(String macroName) {
        macros.remove(macroName);
        saveMacros();
    }

    /**
     * Converts a Macro to a List of MacroListItems for use in a ListView adapter
     * @param name The name of the macro to convert
     */
    public List<MacroListItem> convertMacroToList(String name) {
        if (!macroExists(name)) {
            return null;
        }

        List<MacroListItem> list = new ArrayList<>();
        Macro macro = getMacro(name);

        for (int i = 0; i < macro.getMacroSize(); i++) {
            MacroItem item = macro.getMacroItem(i);

            // embed the cipher type and MacroItem information into a MacroListItem
            // adding this to a list in order ensures that the information will be
            // reflected back in the correct order
            String cipherName = Ciphers.typeToText(item.getType());
            list.add(new MacroListItem(cipherName, item));
        }

        return list;
    }

    /**
     * Generates an AlertDialog Builder that has a spinner with all of the macros saved
     * in this manager. After the user selects a Macro, it can be retrieved using the
     * getSelectorViewSelection() metehod
     * @param activity The activity context used to generate the dialog with
     */
    public AlertDialog.Builder createMacroSelector(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // pull the macro selector from the XML file
        final View selectorView =
                activity.getLayoutInflater().inflate(R.layout.macro_selector,
                null);

        // by default select the first macro
        Spinner selectorSpinner = selectorView.findViewById(R.id.macro_selector_spinner);
        selectorSpinner.setSelection(0);

        // adapter for the spinner that has the names of all Macros in it
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(activity, R.layout.spinner_item, getMacroNames());

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        selectorSpinner.setAdapter(adapter);

        // selection listener that will store the selection in the
        // selectorViewSelection variable
        selectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectorViewSelection = getMacroNames()[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectorSpinner.setSelection(0, true);
            }
        });

        builder.setView(selectorView);
        builder.setNegativeButton(activity.getString(R.string.dialog_cancel), null);

        // purposefully do NOT create the view so when this method is called, a
        // positive button can be added

        return builder;
    }

    /**
     * Deletes all data from the MacroManager and commits that change to system
     * preferences
     */
    public void purgeData() {
        macros.clear();
        saveMacros();
    }

    /**
     * Formats the name of all of the macros into a list
     * @return The string containing the list of macros
     */
    public String formatNames() {
        StringBuilder builder = new StringBuilder();

        for (String name : getMacroNames()) {
            builder.append(name).append(", ");
        }

        // if there were no macros, say [NONE]
        if (builder.length() == 0) {
            return "[NONE]";
        }

        return builder.substring(0, builder.length() - 2);
    }

    /**
     * Returns the name of the macro selected in the macro selector dialog
     */
    public String getSelectorViewSelection() {
        return selectorViewSelection;
    }

    /**
     * Generates an Example Macro and puts the information into an ArrayList for a
     * ListView adapter. This Macro will never be saved by default and will show every
     * time MacrosActivity is launched
     */
    public static ArrayList<MacroListItem> getExampleMacro() {
        Flags flags = new Flags();
        MacroListItem caesar = new MacroListItem("Caesar", new CaesarMacroItem(12,
                flags));
        MacroListItem sub = new MacroListItem("Substitution",
                new SubstitutionMacroItem("ZAYBXCWDVEUFTGSHRIQJPKOLNM",
                        flags));
        MacroListItem vigenere = new MacroListItem("Vigenere", new VigenereMacroItem(
                "SECURITY", flags));
        MacroListItem atbash = new MacroListItem("Atbash", new AtbashMacroItem(flags));

        ArrayList<MacroListItem> list = new ArrayList<>();
        list.add(caesar);
        list.add(sub);
        list.add(vigenere);
        list.add(atbash);

        return list;
    }
}
