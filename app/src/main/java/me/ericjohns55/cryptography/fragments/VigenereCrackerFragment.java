package me.ericjohns55.cryptography.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import me.ericjohns55.cryptography.FrequencyAnalysisActivity;
import me.ericjohns55.cryptography.R;
import me.ericjohns55.cryptography.Utilities;
import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;

/**
 * Provides a driver for the VigenereCracker fragment
 *
 * @author Eric Johns
 */

public class VigenereCrackerFragment extends Fragment {
    View fragment;
    String inputText = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_vigenere_cracker, container,
                false);

        EditText outputField = fragment.findViewById(R.id.vigenere_cracker_output);
        EditText keyField = fragment.findViewById(R.id.key_tester_field);
        EditText groupSizeField = fragment.findViewById(R.id.group_size_input);
        EditText groupNumField = fragment.findViewById(R.id.group_num_input);

        // click listener for updating the graph
        fragment.findViewById(R.id.update_graph).setOnClickListener(this::updateGraph);

        // default group size and numbers is 1 (so no groups)
        groupSizeField.setText(String.valueOf(1));
        groupNumField.setText(String.valueOf(1));

        // grab the input text and key from the bundle that launched this fragment
        String bundleKey = "";
        inputText = "";
        if (getArguments() != null) {
            bundleKey =
                    getArguments().getString(getString(R.string.freq_analysis_key_bundle));
            inputText =
                    getArguments().getString(getString(R.string.freq_analysis_input_bundle));
        }

        final String input = inputText;

        // text change event for the test key field so users can see their test cases
        // in real time
        TextWatcher keyEditEvent = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newKey = charSequence.toString();

                if (newKey.length() != 0) {
                    outputField.setText(Ciphers.vigenereCipher(input, newKey, new Flags(),
                            false));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };

        keyField.addTextChangedListener(keyEditEvent);

        // grabs the test key and returns it to the VigenereFragment
        fragment.findViewById(R.id.transfer_key_button).setOnClickListener(view -> {
            String key = keyField.getText().toString();

            if (key.length() != 0) {
                ((FrequencyAnalysisActivity) getActivity()).returnKey(key);
            } else {
                Utilities.createDialog(getContext(),
                        getString(R.string.cannot_return_key_length_zero),
                        getString(R.string.dialog_ok)).show();
            }
        });

        // if a key was passed into the bundle, update the field
        if (bundleKey != null && bundleKey.length() != 0) {
            keyField.setText(bundleKey);
            outputField.setText(Ciphers.vigenereCipher(input, bundleKey, new Flags(),
                    false));
        }

        return fragment;
    }

    /**
     * Updates the graph to reflect the group size and group number defined in the
     * input boxes
     */
    public void updateGraph(View view) {
        EditText groupSizeField = fragment.findViewById(R.id.group_size_input);
        EditText groupNumField = fragment.findViewById(R.id.group_num_input);

        int groupLength = Integer.parseInt(groupSizeField.getText().toString());
        int groupNum = Integer.parseInt(groupNumField.getText().toString());

        String errorMessage = null;

        // making sure we have non zero lengths and identifiers for the group number
        if (groupLength == 0 || groupNum == 0) {
            errorMessage = getString(R.string.invalid_values_zero);
        }

        // ensuring that the group length is not larger than the input - this ensures
        // there will be text to analyze the frequency of
        if (groupLength >= inputText.length()) {
            if (errorMessage == null) {
                errorMessage = getString(R.string.invalid_group_size_high);
            }
        }

        // ensuring that the group number is less than the length
        if (groupNum > groupLength) {
            if (errorMessage == null) {
                errorMessage = getString(R.string.invalid_group_num_high);
            }
        }

        // if we have an error, display it and do not touch the graph
        if (errorMessage != null) {
            Utilities.createDialog(getContext(), errorMessage,
                    getString(R.string.dialog_ok)).show();
            return;
        }

        // this ensures the user does not have to zero index, we do it for them
        groupNum -= 1;

        // update the graph to reflect the new group length and group number
        FrequencyAnalysisActivity freqAnalysis =
                (FrequencyAnalysisActivity) getActivity();
        freqAnalysis.setupGraph(inputText, groupLength, groupNum, true);
    }
}