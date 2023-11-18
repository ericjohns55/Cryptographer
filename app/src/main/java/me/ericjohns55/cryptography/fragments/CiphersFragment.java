package me.ericjohns55.cryptography.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import me.ericjohns55.cryptography.R;
import me.ericjohns55.cryptography.Utilities;
import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;
import me.ericjohns55.cryptography.ciphers.TextCases;

/**
 * Provides a driver for the CiphersFragment
 * This fragment does the heavy lifting of taking user input, encoding it, and displaying
 * the output
 *
 * @author Eric Johns
 */

public class CiphersFragment extends Fragment {
    private View fragment;

    // Holds onto the current cipher information and flags to differentiate the ciphers
    private Ciphers current_cipher = null;
    private Flags encryptionFlags = new Flags();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_ciphers, container, false);

        // The encode and decode button map to the same method, but they are
        // differentiated when the method is actually run
        fragment.findViewById(R.id.cipher_encode_button).setOnClickListener(this::runCipher);
        fragment.findViewById(R.id.cipher_decode_button).setOnClickListener(this::runCipher);

        EditText output = fragment.findViewById(R.id.cipher_ciphertext_output);

        // Click listener for swapping the output text into the input field
        fragment.findViewById(R.id.cipher_copy_result).setOnClickListener(view -> {
            EditText input = fragment.findViewById(R.id.cipher_plaintext_input);
            input.setText(output.getText());
            output.setText(null);

            // do not show the hint after the first successful encoding
            // it looks too awkward to show up again if the output field is cleared
            output.setHint(null);
        });

        // Clears the input and output fields, resets the encryption flags, and clears
        // the arguments in the child fragments
        fragment.findViewById(R.id.cipher_reset_args).setOnClickListener(view -> {
            ((EditText) fragment.findViewById(R.id.cipher_plaintext_input)).setText("");
            ((EditText) fragment.findViewById(R.id.cipher_ciphertext_output)).setText("");
            ((EditText) fragment.findViewById(R.id.cipher_ciphertext_output)).setHint(
                    getString(R.string.output_hint));

            encryptionFlags.reset();
            resetFragmentArguments();
        });

        // The cipher to run will be passed as a bundle to this fragment
        // If arguments are not passed, this fragment will do nothing
        Bundle arguments = getArguments();

        if (arguments != null) {
            current_cipher = Ciphers.valueOf(arguments.getString(getString(R.string.bundle_cipher)));
            TextView titleLabel = fragment.findViewById(R.id.cipher_name);
            TextView descriptionLabel = fragment.findViewById(R.id.cipher_description);
            String hintText = getString(R.string.output_hint);

            Class<? extends Fragment> cipherClass;

            // take the current cipher, and update the header and description label to
            // give the user preliminary information on what it is going to do
            switch (current_cipher) {
                case CAESAR:
                    titleLabel.setText(getString(R.string.cipher_title_caesar));
                    descriptionLabel.setText(getString(R.string.cipher_description_caesar));
                    cipherClass = CaesarFragment.class;

                    hintText = hintText + getString(R.string.output_hint_caesar);
                    break;
                case SUBSTITUTION:
                    titleLabel.setText(getString(R.string.cipher_title_substitution));
                    descriptionLabel.setText(getString(R.string.cipher_description_substitution));
                    cipherClass = SubstitutionFragment.class;
                    break;
                case VIGENERE:
                    titleLabel.setText(getString(R.string.cipher_title_vigenere));
                    descriptionLabel.setText(getString(R.string.cipher_description_vigenere));
                    cipherClass = VigenereFragment.class;
                    break;
                case ATBASH:
                    titleLabel.setText(getString(R.string.cipher_title_atbash));
                    descriptionLabel.setText(getString(R.string.cipher_description_atbash));
                    cipherClass = AtbashFragment.class;
                    break;
                default:
                    cipherClass = null; // this should never happen
                    break;
            }

            // Set the hint for the output field so the user knows what to expect when
            // they hit encode or decode
            output.setHint(hintText);

            // The substitution and vigenere fragments have a lot more information, so
            // we want to increase the height of the field so it is not compressed
            if (current_cipher == Ciphers.SUBSTITUTION || current_cipher == Ciphers.VIGENERE) {
                // increase the height of the args fragment to allow more space for
                // the alphabet
                fragment.findViewById(R.id.cipher_arguments_fragment).getLayoutParams().height =
                        (int) getResources().getDimension(R.dimen.arguments_fragment_height_taller);
            }

            // load the child fragment for the cipher type
            // this will then display necessary encoding arguments for each cipher type
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.cipher_arguments_fragment, cipherClass, null,
                            getString(R.string.cipher_args_fragment))
                    .setReorderingAllowed(true)
                    .commit();
        }

        return fragment;
    }

    public void runCipher(View view) {
        EditText inputField = fragment.findViewById(R.id.cipher_plaintext_input);
        EditText outputField = fragment.findViewById(R.id.cipher_ciphertext_output);

        // true if we are encoding, false if we are decoding
        boolean encode = view.getTag().toString().equals(getString(R.string.encode));

        // if we are decoding, we want to reverse the text case
        // for examaple: if the output text case is all upper for encoding, it should
        // be all lowercase for decoding the same message
        if (!encode) {
            encryptionFlags.setTextCase(TextCases.reverseCase(encryptionFlags.getTextCase()));
        }

        String inputText = inputField.getText().toString();

        // grab an instance of the child fragment so we can grab encoding information
        // from it
        Fragment argsFragment = getChildFragmentManager().findFragmentByTag(getString(R.string.cipher_args_fragment));
        String resultingText = "";

        // each cipher type here does similar things but slightly differently
        // for all but the atbash cipher, it will make sure the arguments are valid
        // before running the encoder/decoder
        // if the arguments are not valid, nothing will happen
        if (current_cipher == Ciphers.CAESAR) {
            CaesarFragment caesarFragment = (CaesarFragment) argsFragment;

            if (caesarFragment.validRotateArgs()) {
                int rotate = caesarFragment.getRotate();
                if (!encode) rotate *= -1;  // invert rotate amount for decoding

                resultingText = Ciphers.caesarCipher(inputText, rotate, encryptionFlags);
            }
        } else if (current_cipher == Ciphers.SUBSTITUTION) {
            SubstitutionFragment substitutionFragment = (SubstitutionFragment) argsFragment;

            // if we are decoding the map lookup will flip (this is handled in Ciphers)
            if (substitutionFragment.validAlphabet()) {
                resultingText = Ciphers.substitutionCipher(inputText,
                        substitutionFragment.getAlphabet(), encryptionFlags, !encode);
            }
        } else if (current_cipher == Ciphers.VIGENERE) {
            VigenereFragment vigenereFragment = (VigenereFragment) argsFragment;

            if (vigenereFragment.validKey()) {
                resultingText = Ciphers.vigenereCipher(inputText,
                        vigenereFragment.getKey(), encryptionFlags, encode);
            }
        } else if (current_cipher == Ciphers.ATBASH) {
            resultingText = Ciphers.atbashCipher(inputText, encryptionFlags);
        }

        // put the result in the output field
        outputField.setText(resultingText);

        // after decoding, we want to reverse the text case back to the original state
        if (!encode) {
            encryptionFlags.setTextCase(TextCases.reverseCase(encryptionFlags.getTextCase()));
        }

        // do not error if no input was provided, but inform the user in the output field
        if (inputField.getText().length() == 0) {
            outputField.setHint(getString(R.string.no_input_provided));
        }

        // as long as we have valid output, store it in Utilities for use in other
        // processing activities
        if (resultingText != null && resultingText.length() != 0) {
            Utilities.lastOutput = resultingText; // save last output for paste later
        }
    }

    /**
     * Takes the child fragment for a cipher type, and resets their encoding fields
     */
    public void resetFragmentArguments() {
        Fragment argsFragment =
                getChildFragmentManager().findFragmentByTag(getString(R.string.cipher_args_fragment));

        if (current_cipher == Ciphers.CAESAR) {
            ((CaesarFragment) argsFragment).reset();
        } else if (current_cipher == Ciphers.SUBSTITUTION) {
            ((SubstitutionFragment) argsFragment).reset();
        } else if (current_cipher == Ciphers.VIGENERE) {
            ((VigenereFragment) argsFragment).reset();
        }
    }

    /**
     * Shows the encoding flags editor dialog for a cipher
     * If we are hiding numbers (like in the substitution fragment), it will replace
     * the CheckBox with a message saying that the numbers do not apply to that type
     * @param hideNumbers
     */
    public void showEncodingFlagsDialog(boolean hideNumbers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.flags_dialog_title));

        // grab the layout of the dialog from the XML file
        final View options_view =
                getLayoutInflater().inflate(R.layout.encoding_options_dialog, null);

        // changing the View data to visually represent the current flags data
        RadioGroup radioGroup =
                options_view.findViewById(R.id.dialog_text_cases_group);
        radioGroup.clearCheck();
        ((RadioButton) radioGroup.findViewWithTag(encryptionFlags.getTextCase().toString())).setChecked(true);

        radioGroup = options_view.findViewById(R.id.dialog_chars_group);
        radioGroup.clearCheck();

        if (encryptionFlags.preserveCharacters()) {
            ((RadioButton) radioGroup.findViewWithTag(getString(R.string.flags_dialog_preserve_chars))).setChecked(true);
        } else {
            ((RadioButton) radioGroup.findViewWithTag(getString(R.string.flags_dialog_strip_chars))).setChecked(true);
        }

        CheckBox numbersBox = options_view.findViewById(R.id.encode_numbers_checkbox);

        // make the CheckBox invisible put the information view visible if we are not
        // allowing numbers
        if (hideNumbers) {
            options_view.findViewById(R.id.numbers_disallowed).setVisibility(View.VISIBLE);
            numbersBox.setVisibility(View.GONE);
        } else {
            numbersBox.setChecked(encryptionFlags.rotateNumbers());
        }

        // Update the AlertDialog to contain this view
        builder.setView(options_view);

        // on "Apply" we want to update the encryptionFlags object
        builder.setPositiveButton(getString(R.string.dialog_apply),
                (dialogInterface, i) -> updateFlags(options_view));

        builder.setNegativeButton(getString(R.string.dialog_cancel), null);

        builder.create().show();

    }

    /**
     * Takes information from the encoding options dialog, and updates the encoding
     * flags object to reflect the correct information
     * @param options_view The options view containing encoding flags data
     */
    public void updateFlags(View options_view) {
        RadioGroup radioGroup =
                options_view.findViewById(R.id.dialog_text_cases_group);
        int selectedRadioID = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedButton = radioGroup.findViewById(selectedRadioID);
        encryptionFlags.setTextCase(TextCases.valueOf(selectedButton.getTag().toString()));

        radioGroup = options_view.findViewById(R.id.dialog_chars_group);
        selectedRadioID = radioGroup.getCheckedRadioButtonId();
        selectedButton = radioGroup.findViewById(selectedRadioID);
        String tag = selectedButton.getTag().toString();
        encryptionFlags.setPreserveCharacters(tag.equals(getString(R.string.flags_dialog_preserve_chars)));

        CheckBox numbersCheckbox =
                options_view.findViewById(R.id.encode_numbers_checkbox);
        encryptionFlags.setRotateNumbers(numbersCheckbox.isChecked());

    }
}