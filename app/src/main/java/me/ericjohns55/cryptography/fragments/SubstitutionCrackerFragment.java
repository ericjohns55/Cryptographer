package me.ericjohns55.cryptography.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

import me.ericjohns55.cryptography.FrequencyAnalysisActivity;
import me.ericjohns55.cryptography.R;
import me.ericjohns55.cryptography.Utilities;
import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;

/**
 * Provides a driver for the SubstitutionCracker fragment
 *
 * @author Eric Johns
 */

public class SubstitutionCrackerFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_substitution_cracker, container, false);
        EditText outputField = fragment.findViewById(R.id.substitution_cracker_output);

        // if an alphabet was provided in the bundle, load it into the cracker
        String bundleAlphabet = "";
        String inputText = "";
        if (getArguments() != null) {
            bundleAlphabet = getArguments().getString(getString(R.string.freq_analysis_alphabet_bundle));
            inputText =
                    getArguments().getString(getString(R.string.freq_analysis_input_bundle));

            // we have to reverse the alphabet so the headers visually match
            // this means A will be up top, then the user will input the letter they
            // want to test A on
            if (bundleAlphabet != null && bundleAlphabet.length() != 0) {
                bundleAlphabet = reverseAlphabet(bundleAlphabet);
            }
        }

        // since we are generating 52 views for the substitution cracker, it is best to
        // do it programatically
        TableLayout alphabetCrackerLayout = fragment.findViewById(R.id.alphabet_table);

        TableRow labelRow1 = new TableRow(fragment.getContext());
        TableRow inputRow1 = new TableRow(fragment.getContext());
        TableRow labelRow2 = new TableRow(fragment.getContext());
        TableRow inputRow2 = new TableRow(fragment.getContext());

        // these input filters ensure that the user can only enter one character to
        // each box, and it can only be a valid letter
        InputFilter[] inputFilters = new InputFilter[2];
        inputFilters[0] = new InputFilter.LengthFilter(1);
        inputFilters[1] = (charSequence, i, i1, spanned, i2, i3) -> {
            if (charSequence.toString().matches("[a-zA-Z]")) {
                return charSequence.toString();
            }

            return "";
        };

        // this text change listener will update the output box on edit so the user can
        // see if they are making good progress with their guesses
        String finalInputText = inputText;
        TextWatcher textChangeListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String alphabet = getAlphabet(alphabetCrackerLayout);
                outputField.setText(Ciphers.substitutionCipher(finalInputText, alphabet,
                        new Flags(), false));
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };

        // this is where we generate the actual layout
        for (char i = 'A'; i <= 'Z'; i++) {
            // Header labels
            TextView label = new TextView(fragment.getContext());
            label.setHeight((int) getResources().getDimension(R.dimen.alphabet_cracker_label_height));
            label.setGravity(Gravity.CENTER);
            label.setText(Character.toString(i));
            label.setTextSize(getResources().getDimension(R.dimen.description_text_size));
            label.setTextColor(getResources().getColor(R.color.white, null));
            label.setBackground(ContextCompat.getDrawable(fragment.getContext(),
                    R.drawable.alphabet_table_label_background));

            // Input text for each letter of the alphabet
            EditText input = new EditText(fragment.getContext());
            input.setHeight((int) getResources().getDimension(R.dimen.alphabet_cracker_input_height));
            input.setTag("input" + i);
            input.addTextChangedListener(textChangeListener);
            input.setFilters(inputFilters);
            input.setGravity(Gravity.CENTER);
            input.setHint(Character.toString(i));
            input.setTextSize(getResources().getDimension(R.dimen.description_text_size));
            input.setTextColor(getResources().getColor(R.color.white, null));
            input.setBackground(ContextCompat.getDrawable(fragment.getContext(),
                    R.drawable.alphabet_table_input_background));

            // if an alphabet was provided in the bundle, prepopulate the substitution
            // cracker to contain those values
            if (bundleAlphabet != null && bundleAlphabet.length() != 0) {
                input.setText(Character.toString(bundleAlphabet.charAt(i - 'A')));
            }

            // move to the second row after populating A-M
            if (i < 'N') {
                labelRow1.addView(label);
                inputRow1.addView(input);
            } else {
                labelRow2.addView(label);
                inputRow2.addView(input);
            }
        }

        alphabetCrackerLayout.addView(labelRow1);
        alphabetCrackerLayout.addView(inputRow1);
        alphabetCrackerLayout.addView(labelRow2);
        alphabetCrackerLayout.addView(inputRow2);

        // listener for returning the created alphabet to the SubstitutionFragment
        // this way the user can continue using their created alphabet if they want to
        // do more
        fragment.findViewById(R.id.transfer_alphabet_button).setOnClickListener(view -> {
            String alphabet = reverseAlphabet(getAlphabet(alphabetCrackerLayout));

            if (Ciphers.validAlphabet(alphabet)) {
                ((FrequencyAnalysisActivity) getActivity()).returnAlphabet(alphabet);
            } else {
                Utilities.createDialog(getContext(),
                        getString(R.string.freq_analysis_cannot_return_invalid),
                        getString(R.string.dialog_ok)).show();
            }
        });

        // run the substitution cipher on the given alphabet (if given) on load
        if (bundleAlphabet != null && bundleAlphabet.length() != 0) {
            outputField.setText(Ciphers.substitutionCipher(finalInputText, bundleAlphabet,
                    new Flags(), false));
        }

        return fragment;
    }

    /**
     * Traverses the TableLayout and grabs information from each field to put together
     * an Alphabet string
     * @param alphabetCracker The TableLayout to traverse
     * @return A string of length 26 containing an alphabet
     */
    private String getAlphabet(TableLayout alphabetCracker) {
        StringBuilder alphabetBuilder = new StringBuilder();

        for (char i = 'A'; i <= 'Z'; i++) {
            // the view ID is stored in the tag
            EditText edit = alphabetCracker.findViewWithTag("input" + i);

            if (edit != null) {
                if (edit.getText().length() != 0) {
                    alphabetBuilder.append(edit.getText().toString().toUpperCase());
                } else {
                    // if no input was given, use the header instead
                    alphabetBuilder.append(i);
                }
            }
        }

        return alphabetBuilder.toString();
    }

    /**
     * Reverses an alphabet so it can be used to decode instead of decode
     * @param alphabet Alphabet to reverse
     * @return Flipped alphabet
     */
    private String reverseAlphabet(String alphabet) {
        HashMap<Character, Character> alphabetValues = new HashMap<>();

        // populate the HashMap with alphabet values
        for (int i = 0; i < alphabet.length(); i++) {
            alphabetValues.put(Character.toUpperCase(alphabet.charAt(i)), (char) (65 + i));
        }

        // generate the alphabet by looking up alphabet values in the HashMap
        StringBuilder newAlphabet = new StringBuilder();
        for (char i = 'A'; i <= 'Z'; i++) {
            newAlphabet.append(alphabetValues.get(i));
        }

        return newAlphabet.toString();
    }
}