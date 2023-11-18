package me.ericjohns55.cryptography.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import me.ericjohns55.cryptography.R;
import me.ericjohns55.cryptography.Utilities;
import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.ciphers.Flags;

/**
 * Driver class for the CipherInformationFragment
 * Provides a little encryption animation while the user considers which Cipher to
 * explore
 *
 * @author Eric Johns
 */

public class CipherInformationFragment extends Fragment {
    private final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_cipher_information, container,
                false);

        // get the current activity as an argument for parsing strings in Utilities
        Activity activity = getActivity();

        // We have 4 views that correspond to each type of cipher
        // The views are copied from a separate resource file, so all we have to do
        // for each one is update the title and the description to start
        View caesarBox = fragment.findViewById(R.id.caesar_box);
        ((TextView) caesarBox.findViewById(R.id.info_cipher_title)).setText(Utilities.parseID(activity, R.string.cipher_caesar));
        ((TextView) caesarBox.findViewById(R.id.info_cipher_desc)).setText(Utilities.parseID(activity, R.string.descript_caesar_short));

        View substitutionBox = fragment.findViewById(R.id.substitution_box);
        ((TextView) substitutionBox.findViewById(R.id.info_cipher_title)).setText(Utilities.parseID(activity, R.string.cipher_substitution));
        ((TextView) substitutionBox.findViewById(R.id.info_cipher_desc)).setText(Utilities.parseID(activity, R.string.descript_substitution_short));

        View vigenereBox = fragment.findViewById(R.id.vigenere_box);
        ((TextView) vigenereBox.findViewById(R.id.info_cipher_title)).setText(Utilities.parseID(activity, R.string.cipher_vigenere));
        ((TextView) vigenereBox.findViewById(R.id.info_cipher_desc)).setText(Utilities.parseID(activity, R.string.descript_vigenere_short));

        View atbashBox = fragment.findViewById(R.id.atbash_box);
        ((TextView) atbashBox.findViewById(R.id.info_cipher_title)).setText(Utilities.parseID(activity, R.string.cipher_atbash));
        ((TextView) atbashBox.findViewById(R.id.info_cipher_desc)).setText(Utilities.parseID(activity, R.string.descript_atbash_short));

        // Arrays that hold the input and output fields
        // These will be referenced in the loop animations as we add digits
        EditText[] inputFields = {
                caesarBox.findViewById(R.id.info_cipher_input),
                substitutionBox.findViewById(R.id.info_cipher_input),
                vigenereBox.findViewById(R.id.info_cipher_input),
                atbashBox.findViewById(R.id.info_cipher_input)
        };

        EditText[] outputFields = {
                caesarBox.findViewById(R.id.info_cipher_output),
                substitutionBox.findViewById(R.id.info_cipher_output),
                vigenereBox.findViewById(R.id.info_cipher_output),
                atbashBox.findViewById(R.id.info_cipher_output)
        };

        // Cryptography is the input text
        String cryptography = getString(R.string.cryptography);
        Flags flags = new Flags();

        // Here we run "cryptography" through each cipher using random arguments
        // This means that every time this fragment is loaded it will display a different
        // output text while still being a valid encoding of each cipher
        String[] encodedWords = {
                Ciphers.caesarCipher(cryptography, Ciphers.generateRotateAmount(), flags),
                Ciphers.substitutionCipher(cryptography, Ciphers.generateAlphabet(),
                        flags, false),
                Ciphers.vigenereCipher(cryptography, Ciphers.generateKey(), flags, true),
                Ciphers.atbashCipher(cryptography, flags)
        };

        // Loop that repeats every half second that performs the "animation"
        // It adds a character to the EditTexts' each iteration, and flips once we hit
        // the max length
        handler.postDelayed(new Runnable() {
            int index;
            boolean flip = false;

            @Override
            public void run() {
                index++;

                // flip the order of the input/output fields if we hit the end of the word
                if (index > cryptography.length()) {
                    index = 0;
                    flip = !flip;
                }

                // Populate the fields using the substring between the input word and
                // output cipher texts for each iteration
                for (int i = 0; i < inputFields.length; i++) {
                    if (!flip) {
                        inputFields[i].setText(cryptography.substring(0, index));
                        outputFields[i].setText(encodedWords[i].substring(0, index));
                    } else {
                        inputFields[i].setText(encodedWords[i].substring(0, index));
                        outputFields[i].setText(cryptography.substring(0, index));
                    }
                }

                handler.postDelayed(this, 500);
            }
        }, 500);

        return fragment;
    }
}