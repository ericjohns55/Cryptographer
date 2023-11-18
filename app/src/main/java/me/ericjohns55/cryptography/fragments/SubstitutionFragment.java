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
 * Provides a driver for the Substitution fragment
 *
 * @author Eric Johns
 */

public class SubstitutionFragment extends Fragment {
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
        fragment = inflater.inflate(R.layout.fragment_substitution, container, false);

        // setting up click listeners for Encoding Options and Frequency Analysis
        fragment.findViewById(R.id.encoding_options_button).setOnClickListener(this::showOptionsDialog);
        fragment.findViewById(R.id.analyze_frequency_button).setOnClickListener(this::frequencyAnalysis);

        EditText alphabetInputText = fragment.findViewById(R.id.alphabet_input_field);

        // click listener for generating a random alphabet
        fragment.findViewById(R.id.generate_alphabet_button).setOnClickListener(view ->
                alphabetInputText.setText(Ciphers.generateAlphabet()));

        // text change listener for alphabet input
        // if the text field contains an invalid alphabet, the text will be underlined
        // in red, if it is valid it will be underlined as normal
        alphabetInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int color = getResources().getColor(R.color.error, null);

                if (Ciphers.validAlphabet(charSequence.toString())) {
                    color = getResources().getColor(R.color.lighter_gray, null);
                }

                alphabetInputText.setBackgroundTintList(ColorStateList.valueOf(color));
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // result launcher for FrequencyAnalysis
        // if an alphabet was created in frequency analysis, this will populate the
        // alphabet field on return
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    Intent data = result.getData();

                    if (data != null) {
                        String alphabet =
                                data.getStringExtra(getString(R.string.freq_analysis_alphabet_bundle));

                        alphabetInputText.setText(alphabet);
                    }
                }
        );

        return fragment;
    }

    /**
     * Click event for launching the Frequency Analysis Activity
     * If there is no input text, it will not be launched as there will be nothing to
     * analyze
     */
    public void frequencyAnalysis(View view) {
        EditText inputField =
                getParentFragment().getView().findViewById(R.id.cipher_plaintext_input);

        if (inputField.getText().toString().trim().length() == 0) {
            Utilities.createDialog(getContext(),
                    getString(R.string.freq_analysis_fail_no_input),
                    getString(R.string.dialog_ok)).show();
        } else {
            // pass in the current input, cipher type, and alphabet to the activity
            Intent intent = new Intent(getActivity(), FrequencyAnalysisActivity.class);
            intent.putExtra(getString(R.string.freq_analysis_input_bundle),
                    inputField.getText().toString());
            intent.putExtra(getString(R.string.freq_analysis_cipher_bundle),
                    Ciphers.SUBSTITUTION.toString());
            intent.putExtra(getString(R.string.freq_analysis_alphabet_bundle), getAlphabet());

            resultLauncher.launch(intent);
        }
    }

    /**
     * Returns the current alphabet in the input field
     */
    public String getAlphabet() {
        return ((EditText) fragment.findViewById(R.id.alphabet_input_field)).getText().toString();
    }

    /**
     * Returns true if the alphabet in the input field is valid
     * If it is not valid, the EditText will be underlined in red
     * @return
     */
    public boolean validAlphabet() {
        EditText alphabetInput = fragment.findViewById(R.id.alphabet_input_field);
        String alphabet = alphabetInput.getText().toString();

        if (!Ciphers.validAlphabet(alphabet)) {
            alphabetInput.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.error, null)));
            Utilities.createDialog(getActivity(),
                    getString(R.string.encode_fail_bad_alphabet),
                    getString(R.string.dialog_ok)).show();
            return false;
        }

        return true;
    }

    /**
     * Resets alphabet input field to have no text, and resets the underline color to
     * default
     */
    public void reset() {
        EditText alphabetField = fragment.findViewById(R.id.alphabet_input_field);
        Utilities.resetInputField(alphabetField, getResources());
    }

    /**
     * Listener to show the encoding options dialog
     */
    public void showOptionsDialog(View view) {
        CiphersFragment parent = (CiphersFragment) getParentFragment();
        parent.showEncodingFlagsDialog(true);
    }
}