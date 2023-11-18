package me.ericjohns55.cryptography;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Driver for the SettingsActivity
 *
 * @author Eric Johns
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.back_toolbar);
        toolbar.inflateMenu(R.menu.back_menu);
        setSupportActionBar(toolbar);
    }

    /**
     * Click listener for deleting all saved macros from the application
     * This will show a dialog and make the user confirm before committing the changes
     */
    public void deleteAllMacros(View view) {
        String dialogMessage = String.format(getString(R.string.macro_deletion_confirm),
                MainActivity.macroManager.formatNames());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name).setMessage(dialogMessage);
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);
        builder.setPositiveButton(getString(R.string.dialog_confirm), (dialogInterface, i) -> {
            MainActivity.macroManager.purgeData();
            Utilities.createToast(getBaseContext(),
                    getString(R.string.all_macro_data_cleared), 120).show();
        });

        builder.create().show();
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