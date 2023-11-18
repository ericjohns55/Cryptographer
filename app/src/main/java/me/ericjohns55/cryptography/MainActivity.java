package me.ericjohns55.cryptography;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;

import me.ericjohns55.cryptography.macros.MacroManager;

/**
 * The main driver for the Cryptography application
 * The main purpose is to redirect the user to subcategories to play with ciphers in
 *
 * @author Eric Johns
 */

public class MainActivity extends AppCompatActivity {
    // Manages all Macros across the entire application
    public static MacroManager macroManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(toolbar);

        // find the width of the screen and divide by 2 to scale the app icon and
        // listview width and height to
        int halfScreenSize = getResources().getDisplayMetrics().widthPixels / 2;

        // updating the app icon size
        ImageView appIcon = findViewById(R.id.app_icon);
        appIcon.setMinimumWidth(halfScreenSize);
        appIcon.setMinimumHeight(halfScreenSize);
        appIcon.setMaxWidth(halfScreenSize);
        appIcon.setMaxHeight(halfScreenSize);

        // updating the listview size
        ListView listView = (ListView) findViewById(R.id.listview_macro_viewer);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = halfScreenSize;
        params.width = halfScreenSize;
        listView.setLayoutParams(params);

        // setup adapters for the listview
        ArrayList<String> menuOptions = new ArrayList<>(Arrays.asList(
                getString(R.string.cipher_list_text),
                getString(R.string.images_list_text),
                getString(R.string.qr_codes_list_text),
                getString(R.string.macros_list_text)));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.list_view_button, android.R.id.text1, menuOptions);

        listView.setAdapter(adapter);

        // ListView click listeners for launching other Activities
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String itemText = listView.getItemAtPosition(i).toString();
            Intent intent;

            if (itemText.equals(getString(R.string.cipher_list_text))) {
                intent = new Intent(getBaseContext(), CipherActivity.class);
            } else if (itemText.equals(getString(R.string.images_list_text))) {
                intent = new Intent(getBaseContext(), ImageProcessingActivity.class);
            } else if (itemText.equals(getString(R.string.qr_codes_list_text))) {
                intent = new Intent(getBaseContext(), QRCodesActivity.class);
            } else {
                intent = new Intent(getBaseContext(), MacrosActivity.class);
            }

            startActivity(intent);
        });

        // setting up the MacroManager
        macroManager = new MacroManager(getPreferences(Context.MODE_PRIVATE),
                getString(R.string.shared_prefs_macros));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings_button) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}