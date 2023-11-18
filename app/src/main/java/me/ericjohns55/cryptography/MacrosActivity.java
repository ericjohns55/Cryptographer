package me.ericjohns55.cryptography;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import me.ericjohns55.cryptography.adapters.MacroListAdapter;
import me.ericjohns55.cryptography.adapters.MacroListItem;
import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;
import me.ericjohns55.cryptography.ciphers.TextCases;
import me.ericjohns55.cryptography.macros.AtbashMacroItem;
import me.ericjohns55.cryptography.macros.CaesarMacroItem;
import me.ericjohns55.cryptography.macros.Macro;
import me.ericjohns55.cryptography.macros.MacroItem;
import me.ericjohns55.cryptography.macros.MacroManager;
import me.ericjohns55.cryptography.macros.SubstitutionMacroItem;
import me.ericjohns55.cryptography.macros.VigenereMacroItem;

/**
 * Driver for the Macros Activity
 * Responsible for the UI in creation, deletion, decoding, and encoding of macros
 *
 * @author Eric Johns
 */

public class MacrosActivity extends AppCompatActivity {
    // Adapter for the ListView which displays Macro information
    private MacroListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macros);

        Toolbar toolbar = findViewById(R.id.back_toolbar);
        toolbar.inflateMenu(R.menu.back_menu);
        setSupportActionBar(toolbar);

        // if a macro input was passed (such as from a cipher activity or image
        // processing activity, load it into the input field
        if (getIntent() != null) {
            if (getIntent().hasExtra(getString(R.string.bundle_macro_input))) {
                String text =
                        getIntent().getStringExtra(getString(R.string.bundle_macro_input));
                ((EditText) findViewById(R.id.macro_processor_input)).setText(text);
            }
        }

        // set up the example macro which is shown every time this activity is launched
        ((EditText) findViewById(R.id.macro_name_input_field)).setText(R.string.example_macro);
        adapter = new MacroListAdapter(getBaseContext(), MacroManager.getExampleMacro());

        // set up the listview and apply the custom adapter
        ListView listView = findViewById(R.id.listview_macro_viewer);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> adapter.setSelection(i));
    }

    /**
     * Creates a MacroItem editor view
     * @param macroListItem The MacroListItem to edit. If this is null then default
     *                      arguments will be used
     * @return The created macro item editor view
     */
    private View createEditorView(MacroListItem macroListItem) {
        final View editorView = getLayoutInflater().inflate(R.layout.macro_item_editor, null);

        // grab all views that will be referenced while processing the input
        RadioGroup cipherGroup = editorView.findViewById(R.id.cipher_type_selector_group);
        TextView requiredArgsLabel = editorView.findViewById(R.id.required_arguments_descriptor_label);
        EditText requiredArgsInput = editorView.findViewById(R.id.required_arguments_input_field);
        TextView noRequiredArgsLabel = editorView.findViewById(R.id.no_required_args_label);
        TextView validArgumentsLabel = editorView.findViewById(R.id.valid_input_label);
        ImageButton generateArgs = editorView.findViewById(R.id.required_arguments_generate_button);
        Spinner textCaseSpinner = editorView.findViewById(R.id.text_case_spinner);
        Spinner charactersSpinner = editorView.findViewById(R.id.characters_spinner);
        CheckBox numbersCheckbox = editorView.findViewById(R.id.encode_numbers_checkbox);
        TextView numbersDisallowedLabel = editorView.findViewById(R.id.numbers_disallowed_label);

        // cipher group check change listener
        cipherGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton clicked = radioGroup.findViewById(i);
            String tag = clicked.getTag().toString();

            // first clear all required argument input
            requiredArgsInput.setTag(tag);
            requiredArgsInput.setText("");

            // ensure that at least one cipher type was selected
            if (clicked.isChecked()) {
                generateArgs.setTag(tag);

                // by default show the numbers checkbox and hide the disallowed label
                numbersDisallowedLabel.setVisibility(View.GONE);
                numbersCheckbox.setVisibility(View.VISIBLE);

                if (!tag.equals(getString(R.string.cipher_atbash))) {
                    // ciphers that are not atbash have required arguments, so we want
                    // to make sure that all of the views are visible
                    noRequiredArgsLabel.setVisibility(View.GONE);
                    requiredArgsLabel.setVisibility(View.VISIBLE);
                    requiredArgsInput.setVisibility(View.VISIBLE);
                    generateArgs.setVisibility(View.VISIBLE);
                    validArgumentsLabel.setVisibility(View.VISIBLE);

                    if (tag.equals(getString(R.string.cipher_caesar))) {
                        // edit the args label to reflect a caesar cipher
                        requiredArgsLabel.setText(getString(R.string.rotate_amount));
                    } else if (tag.equals(getString(R.string.cipher_substitution))) {
                        // edit the args label to reflect a substitution cipher and
                        // hide the numbers checkbox as it is not used
                        requiredArgsLabel.setText(getString(R.string.alphabet_label));
                        numbersDisallowedLabel.setVisibility(View.VISIBLE);
                        numbersCheckbox.setVisibility(View.GONE);
                    } else if (tag.equals(getString(R.string.cipher_vigenere))) {
                        // edit the args label to reflect a vigenere cipher
                        requiredArgsLabel.setText(getString(R.string.key_label));
                    }
                } else {
                    // only atbash is left, hide the required args and other relevant
                    // views as they are not used
                    noRequiredArgsLabel.setVisibility(View.VISIBLE);
                    requiredArgsLabel.setVisibility(View.GONE);
                    requiredArgsInput.setVisibility(View.GONE);
                    validArgumentsLabel.setVisibility(View.GONE);
                    generateArgs.setVisibility(View.GONE);
                }
            }
        });

        // generate argument listener for the caesar, substitution, and vigenere cipher
        generateArgs.setOnClickListener(view -> {
            if (view.getTag().equals(getString(R.string.cipher_caesar))) {
                requiredArgsInput.setText(String.valueOf(Ciphers.generateRotateAmount()));
            } else if (view.getTag().equals(getString(R.string.cipher_substitution))) {
                requiredArgsInput.setText(Ciphers.generateAlphabet());
            } else if (view.getTag().equals(getString(R.string.cipher_vigenere))) {
                requiredArgsInput.setText(Ciphers.generateKey());
            }
        });

        // text change listener for the various ciphers
        // there is a label that tells you whether a required argument is valid or not,
        // and this will update it in real time
        requiredArgsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String tag = requiredArgsInput.getTag().toString();
                String resultMessage = "";
                boolean valid = true;

                if (tag.equals(getString(R.string.cipher_caesar))) {
                    if (charSequence.length() == 0 || !charSequence.toString().matches(
                            "^-?[1-9][0-9]*$")) { // regex for a valid update amount
                        valid = false;
                        resultMessage = getString(R.string.invalid_args_caesar);
                    } else {
                        resultMessage = getString(R.string.valid_args_caesar);
                    }
                } else if (tag.equals(getString(R.string.cipher_substitution))) {
                    if (!Ciphers.validAlphabet(charSequence.toString())) {
                        valid = false;
                        resultMessage = getString(R.string.invalid_args_substitution);
                    } else {
                        resultMessage = getString(R.string.valid_args_substitution);
                    }
                } else if (tag.equals(getString(R.string.cipher_vigenere))) {
                    if (charSequence.length() == 0) { // a valid key has at least 1 char
                        valid = false;
                        resultMessage = getString(R.string.invalid_args_vigenere);
                    } else {
                        resultMessage = getString(R.string.valid_args_vigenere);
                    }
                }

                // update the result message based on the text changed
                validArgumentsLabel.setText(resultMessage);

                // update the color to be green if valid, and red if invalid
                if (valid) {
                    validArgumentsLabel.setTextColor(getColor(R.color.success));
                } else {
                    validArgumentsLabel.setTextColor(getColor(R.color.error));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {  }
        });

        // if the macroListItem is not null, then we are editing an item and we should
        // load the current information into the editor
        if (macroListItem != null) {
            MacroItem item = macroListItem.getMacroItem();

            // checking the box for the current cipher type
            switch (item.getType()) {
                case CAESAR:
                    ((RadioButton) cipherGroup.findViewWithTag(getString(R.string.cipher_caesar))).setChecked(true);
                    break;
                case SUBSTITUTION:
                    ((RadioButton) cipherGroup.findViewWithTag(getString(R.string.cipher_substitution))).setChecked(true);
                    break;
                case VIGENERE:
                    ((RadioButton) cipherGroup.findViewWithTag(getString(R.string.cipher_vigenere))).setChecked(true);
                    break;
                case ATBASH:
                    ((RadioButton) cipherGroup.findViewWithTag(getString(R.string.cipher_atbash))).setChecked(true);
                    break;
            }

            // setting the text case spinner to the current items selection
            int textCase = TextCases.toInt(item.getFlags().getTextCase());
            textCaseSpinner.setSelection(textCase);

            // setting the preserve characters spinner to the current items value
            int characters = item.getFlags().preserveCharacters() ? 0 : 1;
            charactersSpinner.setSelection(characters);

            // checking the box if the current item rotates numbers
            numbersCheckbox.setChecked(item.getFlags().rotateNumbers());

            // loading the extra arguments from the item
            requiredArgsInput.setText(item.getExtraArgument());
        } else { // default cipher
            ((RadioButton) cipherGroup.findViewWithTag(getString(R.string.cipher_caesar))).setChecked(true);
        }

        return editorView;
    }

    /**
     * Grabs the data from a MacroItem editor view
     * @param view The macro item editor view
     * @param selectedIndex The index of the macro item to edit. If this is set to -1
     *                      then we are creating a new item instead of editing an
     *                      existing one
     */
    public void getDataFromEditor(View view, int selectedIndex) {
        // grab each view that holds valuable information
        EditText requiredArgsInput =
                view.findViewById(R.id.required_arguments_input_field);
        Spinner characterSpinner = view.findViewById(R.id.characters_spinner);
        Spinner preserveSpinner = view.findViewById(R.id.text_case_spinner);
        CheckBox numbersCheckbox = view.findViewById(R.id.encode_numbers_checkbox);

        // parse the cipher type and required arguments
        String cipherType = requiredArgsInput.getTag().toString();
        String input = requiredArgsInput.getText().toString();

        // grab and parse the encoding flags from the editor
        Flags flags = new Flags();
        flags.setTextCase(TextCases.fromInt(preserveSpinner.getSelectedItemPosition()));
        flags.setPreserveCharacters(characterSpinner.getSelectedItemPosition() == 0);
        flags.setRotateNumbers(numbersCheckbox.isChecked());

        // create a new macro item
        MacroItem item = null;

        if (cipherType.equals(getString(R.string.cipher_caesar))) {
            // caesar macro items get a rotate amount as their argument
            if (input.matches("^-?[1-9][0-9]*$")) {
                int rotateAmount = Integer.parseInt(input);
                item = new CaesarMacroItem(rotateAmount, flags);
            }
        } else if (cipherType.equals(getString(R.string.cipher_substitution))) {
            // substitution macro items get an alphabet as their argument
            if (Ciphers.validAlphabet(input)) {
                item = new SubstitutionMacroItem(input, flags);
            }
        } else if (cipherType.equals(getString(R.string.cipher_vigenere))) {
            // vigenere macro items get a key as their argument
            if (input.length() != 0) {
                item = new VigenereMacroItem(input, flags);
            }
        } else {
            // atbash macro items have no extra arguments, just flags
            item = new AtbashMacroItem(flags);
        }

        // as long as valid arguments were presented, the item wont be null
        if (item != null) {
            MacroListItem listItem = new MacroListItem(cipherType, item);

            // if the selected index is -1, it is a new item
            if (selectedIndex == -1) {
                adapter.addItem(listItem);
            } else { // otherwise, we are replacing the item at the given index
                adapter.replaceItem(selectedIndex, listItem);
            }
        } else {
            Utilities.createToast(getBaseContext(),
                    getString(R.string.creation_fail_bad_args), 120).show();
        }

        // if there are no items, we want to display an "empty macro" label to the user
        TextView emptyLabel = findViewById(R.id.empty_listview_label);
        if (adapter.getCount() != 0) {
            emptyLabel.setVisibility(View.GONE);
        } else {
            emptyLabel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Listener for creating a new macro
     * This resets the data in the adapter and removes all text in the name field
     */
    public void createMacro(View view) {
        ((EditText) findViewById(R.id.macro_name_input_field)).setText("");

        TextView emptyLabel = findViewById(R.id.empty_listview_label);
        emptyLabel.setVisibility(View.VISIBLE);
        adapter.resetData();
    }

    /**
     * Listener for loading a macro from the MacroManager
     */
    public void loadMacro(View view) {
        // first make sure there are any macros to load, fail if not
        if (MainActivity.macroManager.isEmpty()) {
            Utilities.createToast(this, getString(R.string.no_macros_to_load), 120).show();
            return;
        }

        // show our macro selector dialog as created in the MacroManager
        AlertDialog.Builder dialog =
                MainActivity.macroManager.createMacroSelector(this);
        dialog.setPositiveButton(getString(R.string.dialog_select), (dInterface, i) -> {
            String macroToLoad = MainActivity.macroManager.getSelectorViewSelection();

            // replace the data in our listview with the selected macro from the dialog
            adapter.replaceData(MainActivity.macroManager.convertMacroToList(macroToLoad));
            Utilities.createToast(getBaseContext(), "Loading macro \"" + macroToLoad +
                            "\"", 120).show();
            ((EditText) findViewById(R.id.macro_name_input_field)).setText(macroToLoad);

            // hide our "empty listview" label as long as there is at least one item
            if (adapter.getCount() != 0) {
                findViewById(R.id.empty_listview_label).setVisibility(View.GONE);
            }
        });

        dialog.create().show();
    }

    /**
     * Listener for saving a macro to the MacroManager
     */
    public void saveMacro(View view) {
        EditText nameInput = findViewById(R.id.macro_name_input_field);
        String macroName = nameInput.getText().toString();

        if (macroName.length() != 0) { // ensure that the macro has a name
            // if a macro with that name already exists, warn the user
            if (MainActivity.macroManager.macroExists(macroName)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage(getString(R.string.confirm_overwrite_macro));
                builder.setNegativeButton(getString(R.string.dialog_cancel), null);
                builder.setPositiveButton(getString(R.string.dialog_confirm),
                        (dialogInterface, i) -> addMacro(macroName, adapter.toMacro()));
                builder.create().show();
            } else {
                addMacro(macroName, adapter.toMacro());
            }
        } else {
            Utilities.createToast(this, getString(R.string.couldnt_save_no_name), 120).show();
        }
    }

    /**
     * Adds a macro to the MacroManager
     * @param macroName Name of the macro to add
     * @param macro The macro to add
     */
    private void addMacro(String macroName, Macro macro) {
        MainActivity.macroManager.addMacro(macroName, macro);
        Utilities.createToast(this, "Saved macro: " + macroName, 120).show();
    }

    /**
     * Listener for deleting a macro from the MacroManager
     */
    public void deleteMacro(View view) {
        EditText nameInput = findViewById(R.id.macro_name_input_field);
        String macroName = nameInput.getText().toString();

        if (MainActivity.macroManager.macroExists(macroName)) {
            String dialogMessage =
                    String.format(getString(R.string.delete_macro_message), macroName);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name).setMessage(dialogMessage);
            builder.setNegativeButton(getString(R.string.dialog_cancel), null);

            // force the user to confirm before deleting
            builder.setPositiveButton(getString(R.string.dialog_confirm), (dialogInterface, i) -> {
                MainActivity.macroManager.deleteMacro(macroName);

                nameInput.setText("");
                adapter.resetData();
                findViewById(R.id.empty_listview_label).setVisibility(View.VISIBLE);
            });

            builder.create().show();
        } else {
            Utilities.createToast(this, getString(R.string.cannot_delete_never_saved),
                    120).show();
        }
    }

    /**
     * Listener for encoding or decoding a macro
     */
    public void processMacroOnInput(View view) {
        String message = view.getTag().toString();
        Macro macro = adapter.toMacro();

        EditText inputField = findViewById(R.id.macro_processor_input);
        EditText outputField = findViewById(R.id.macro_processor_output);

        if (message.equals(getString(R.string.encode))) { // run macro forwards on encode
            macro.runMacro(inputField.getText().toString(), true);
            outputField.setText(macro.getOutput());
        } else { // run macro backwards on decode
            macro.runMacro(inputField.getText().toString(), false);
        }

        // update the text field and save our last input for future reference
        outputField.setText(macro.getOutput());
        Utilities.lastOutput = macro.getOutput();
    }

    /**
     * Listener for swapping the input and output fields in the macro processor
     */
    public void swapInputOutput(View view) {
        EditText inputField = findViewById(R.id.macro_processor_input);
        EditText outputField = findViewById(R.id.macro_processor_output);
        inputField.setText(outputField.getText());
        outputField.setText("");
    }

    /**
     * Handles all pieces of Macro Items
     * This includes adding new macro items, deleting them, rearranging them, or
     * editing existing items
     */
    public void manageMacroItem(View view) {
        String action = view.getTag().toString();

        if (action.equals(getString(R.string.add_macro_item))) {
            // on adding a macro item, we want to create the editor dialog view so the
            // user can specify required arguments. since we are adding and not
            // editing, all values will be set to the defaults
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.macro_item_editor));

            View editor_view = createEditorView(null);

            builder.setView(editor_view);

            // get the data with a selected index of -1 to represent a new item
            builder.setPositiveButton(getString(R.string.dialog_apply),
                    (dialogInterface, i) -> getDataFromEditor(editor_view, -1));

            builder.setNegativeButton(getString(R.string.dialog_cancel), null);
            builder.create().show();
        } else {
            // since we are not adding a new item, we must have one selected already
            if (!adapter.hasSelection()) {
                Utilities.createToast(getBaseContext(),
                        getString(R.string.macro_item_not_selected), 120).show();
                return;
            }

            if (action.equals(getString(R.string.move_macro_item_up))) {
                adapter.moveItemUp(adapter.getSelection()); // shift item up 1
            } else if (action.equals(getString(R.string.move_macro_item_down))) {
                adapter.moveItemDown(adapter.getSelection()); // shift item down 1
            } else if (action.equals(getString(R.string.delete_macro_item))) {
                adapter.deleteItem(adapter.getSelection()); // delete the selected item

                // if the macro now has 0 items, show our label that displays to the
                // user that the macro is empty
                if (adapter.getCount() == 0) {
                    TextView emptyLabel = findViewById(R.id.empty_listview_label);
                    emptyLabel.setVisibility(View.VISIBLE);
                }
            } else { // only edit macro item is left
                // show the same editor as before for the editor, but with a difference
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.macro_item_editor));

                int index = adapter.getSelection();

                // here is the difference: we want to grab the current macro item that
                // is selected so we can populate the editor view with its information
                View editor_view = createEditorView(adapter.getItem(index));

                builder.setView(editor_view);

                // on apply, pass in the index of the macro item to represent updating
                // and not creating a new item
                builder.setPositiveButton(getString(R.string.dialog_apply),
                        (dialogInterface, i) -> getDataFromEditor(editor_view, index));

                builder.setNegativeButton(getString(R.string.dialog_cancel), null);
                builder.create().show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_back_button) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}