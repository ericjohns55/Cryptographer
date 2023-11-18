package me.ericjohns55.cryptography.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import me.ericjohns55.cryptography.R;

/**
 * Driver class for the AtbashFragment
 *
 * @author Eric Johns
 */

public class AtbashFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_atbash, container, false);

        // this is the simplest fragment as it only has the "Encoding Options" button
        // this adds the key listener and displays the dialog
        fragment.findViewById(R.id.encoding_options_button).setOnClickListener(view -> {
            CiphersFragment parent = (CiphersFragment) getParentFragment();
            parent.showEncodingFlagsDialog(false);
        });

        return fragment;
    }
}