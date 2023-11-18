package me.ericjohns55.cryptography;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.fragments.CipherInformationFragment;
import me.ericjohns55.cryptography.fragments.CiphersFragment;

/**
 * Driver of the Cipher activity screen
 * This is in charge of swapping out the information (animation) and cipher fragment
 *
 * @author Eric Johns
 */

public class CipherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher);

        Toolbar toolbar = findViewById(R.id.back_toolbar);
        toolbar.inflateMenu(R.menu.back_menu);
        setSupportActionBar(toolbar);

        RadioGroup ciphersGroup = findViewById(R.id.cipher_selector_group);

        // clear the selector and load the fragment when the title label is clicked
        findViewById(R.id.cipher_label).setOnClickListener(view -> {
            ciphersGroup.clearCheck();

            loadInformationFragment();
        });

        // check change listener to load the ciphers fragment
        ciphersGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton clicked = radioGroup.findViewById(i);

            if (clicked != null && clicked.isPressed()) {
                String tag = radioGroup.findViewById(i).getTag().toString();
                loadCipherFragment(tag);
            }
        });

        // by default, load the information (animation fragment)
        loadInformationFragment();
    }

    /**
     * Loads the information fragment that contains animations for each cipher type
     */
    private void loadInformationFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, CipherInformationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    /**
     * Loads the cipher fragment and loads the cipher type into a bundle for parsing
     * @param cipher The type of cipher that was selected
     */
    public void loadCipherFragment(String cipher) {
        Bundle bundle = new Bundle();
        if (cipher.equals(getString(R.string.cipher_caesar))) {
            bundle.putString(getString(R.string.bundle_cipher),
                    Ciphers.CAESAR.toString());
        } else if (cipher.equals(getString(R.string.cipher_substitution))) {
            bundle.putString(getString(R.string.bundle_cipher),
                    Ciphers.SUBSTITUTION.toString());
        } else if (cipher.equals(getString(R.string.cipher_vigenere))) {
            bundle.putString(getString(R.string.bundle_cipher),
                    Ciphers.VIGENERE.toString());
        } else {
            bundle.putString(getString(R.string.bundle_cipher),
                    Ciphers.ATBASH.toString());
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, CiphersFragment.class, bundle)
                .setReorderingAllowed(true)
                .commit();
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