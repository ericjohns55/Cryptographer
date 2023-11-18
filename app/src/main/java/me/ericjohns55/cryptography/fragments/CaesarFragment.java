package me.ericjohns55.cryptography.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import me.ericjohns55.cryptography.R;
import me.ericjohns55.cryptography.Utilities;
import me.ericjohns55.cryptography.ciphers.Ciphers;

/**
 * Driver class for the CaesarFragment
 *
 * @author Eric Johns
 */

public class CaesarFragment extends Fragment {
    private View fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_caesar, container, false);

        // adds a click listener for showing the encoding options dialog
        fragment.findViewById(R.id.encoding_options_button).setOnClickListener(this::showOptionsDialog);

        EditText rotateInputText = fragment.findViewById(R.id.rotate_input_field);

        // updates the rotate field to contain a randomly generated rotate amount from
        // the Ciphers class
        fragment.findViewById(R.id.generate_rotate_button).setOnClickListener(view ->
                rotateInputText.setText(String.valueOf(Ciphers.generateRotateAmount())));

        rotateInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int color = getResources().getColor(R.color.error, null);

                // if the rotate amount is a valid number, make the EditText's
                // underline color gray, otherwise it will be red to signify an error
                if (charSequence.length() != 0 && charSequence.toString().matches(
                        "^-?[1-9][0-9]*$")) {
                    color = getResources().getColor(R.color.lighter_gray, null);
                }

                rotateInputText.setBackgroundTintList(ColorStateList.valueOf(color));
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        return fragment;
    }

    /**
     *  Clears the rotate field and sets the underline color to be default
     */
    public void reset() {
        EditText rotateField = fragment.findViewById(R.id.rotate_input_field);
        Utilities.resetInputField(rotateField, getResources());
    }

    /**
     * Returns the rotate amount as found in the rotateInput field
     */
    public int getRotate() {
        EditText rotateInput = fragment.findViewById(R.id.rotate_input_field);
        return Integer.parseInt(rotateInput.getText().toString());
    }

    /**
     * Returns true if the rotate amount is valid, false otherwise
     */
    public boolean validRotateArgs() {
        EditText rotateInput = fragment.findViewById(R.id.rotate_input_field);
        String providedRotation = rotateInput.getText().toString();

        // A rotate amount is pretty much valid as long as a number exists
        if (providedRotation.length() == 0) {
            rotateInput.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.error, null)));
            Utilities.createDialog(getActivity(),
                    getString(R.string.encode_fail_no_rotate_provided),
                    getString(R.string.dialog_ok)).show();
            return false;
        }

        return true;
    }

    /**
     * Click listener for showing the encoding options dialog
     */
    public void showOptionsDialog(View view) {
        CiphersFragment parent = (CiphersFragment) getParentFragment();
        parent.showEncodingFlagsDialog(false);
    }
}