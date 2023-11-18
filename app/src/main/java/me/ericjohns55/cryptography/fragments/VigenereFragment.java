package me.ericjohns55.cryptography.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import me.ericjohns55.cryptography.FrequencyAnalysisActivity;
import me.ericjohns55.cryptography.R;
import me.ericjohns55.cryptography.Utilities;
import me.ericjohns55.cryptography.ciphers.Ciphers;

/**
 * Provides a driver for the Vigenere fragment
 *
 * @author Eric Johns
 */

public class VigenereFragment extends Fragment {
    // result launcher for frequency analysis cracker
    private ActivityResultLauncher<Intent> resultLauncher;
    private View fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_vigenere, container, false);

        // registering key events for Encoding Options and Frequency Analysis
        fragment.findViewById(R.id.encoding_options_button).setOnClickListener(this::showOptionsDialog);
        fragment.findViewById(R.id.analyze_frequency_button).setOnClickListener(this::frequencyAnalysis);

        EditText keyInputText = fragment.findViewById(R.id.key_input_field);

        // click event for generating a random key
        fragment.findViewById(R.id.generate_key_button).setOnClickListener(view ->
                keyInputText.setText(Ciphers.generateKey()));

        // text change listener that will underline text in red if there is an invalid
        // key in the input field
        keyInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int color = getResources().getColor(R.color.error, null);

                if (charSequence.length() != 0) {
                    color = getResources().getColor(R.color.lighter_gray, null);
                }

                keyInputText.setBackgroundTintList(ColorStateList.valueOf(color));
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // registers a result launcher for Frequency Analysis
        // if the user generated a key and hit the return key button, it will be moved
        // into the key input field here
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    Intent data = result.getData();

                    if (data != null) {
                        String key =
                                data.getStringExtra(getString(R.string.freq_analysis_key_bundle));

                        keyInputText.setText(key);
                    }
                }
        );

        return fragment;
    }

    /**
     * Launches the Frequency Analysis activity
     */
    public void frequencyAnalysis(View view) {
        EditText inputField =
                getParentFragment().getView().findViewById(R.id.cipher_plaintext_input);

        if (inputField.getText().toString().trim().length() == 0) {
            Utilities.createDialog(getContext(),
                    getString(R.string.freq_analysis_fail_no_input),
                    getString(R.string.dialog_ok)).show();
        } else {
            // pass in the current input, cipher type, and key to the activity
            Intent intent = new Intent(getActivity(), FrequencyAnalysisActivity.class);
            intent.putExtra(getString(R.string.freq_analysis_input_bundle),
                    inputField.getText().toString());
            intent.putExtra(getString(R.string.freq_analysis_cipher_bundle),
                    Ciphers.VIGENERE.toString());
            intent.putExtra(getString(R.string.freq_analysis_key_bundle), getKey());

            resultLauncher.launch(intent);
        }
    }

    /**
     * Returns the key contained in the key input field
     */
    public String getKey() {
        return ((EditText) fragment.findViewById(R.id.key_input_field)).getText().toString();
    }

    /**
     * Returns true if a key is valid
     * Returns false if a key is not valid and underlines the key input field in red to
     * show there is an error
     */
    public boolean validKey() {
        EditText keyInput = fragment.findViewById(R.id.key_input_field);
        String key = keyInput.getText().toString();

        if (key.length() == 0) {
            keyInput.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.error, null)));
            Utilities.createDialog(getActivity(),
                    getString(R.string.encode_fail_bad_key),
                    getString(R.string.dialog_ok)).show();
            return false;
        }

        return true;
    }


    /**
     * Resets key input field to have no text, and resets the underline color to
     * default
     */
    public void reset() {
        EditText keyField = fragment.findViewById(R.id.key_input_field);
        Utilities.resetInputField(keyField, getResources());
    }

    /**
     * Listener to show the encoding options dialog
     */
    public void showOptionsDialog(View view) {
        CiphersFragment parent = (CiphersFragment) getParentFragment();
        parent.showEncodingFlagsDialog(false);
    }
}