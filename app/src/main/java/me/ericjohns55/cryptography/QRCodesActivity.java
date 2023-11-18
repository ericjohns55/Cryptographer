package me.ericjohns55.cryptography;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * Driver for the QRCodes Activity
 * Responsible for scanning, generating, and saving QR codes
 *
 * @author Eric Johns
 */

public class QRCodesActivity extends AppCompatActivity {
    // Launcher for the QR scanner
    private ActivityResultLauncher<ScanOptions> barLauncher;
    private Bitmap barcodeBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcodes);

        Toolbar toolbar = findViewById(R.id.back_toolbar);
        toolbar.inflateMenu(R.menu.back_menu);
        setSupportActionBar(toolbar);

        // setting up the return listener for scanning QR or Barcodes
        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                String decodedMessage = result.getContents();

                EditText editText = findViewById(R.id.qr_codes_edit_text);
                editText.setText(decodedMessage);
            }
        });
    }

    /**
     * Click listener for scanning QR or Code 128 Bar codes
     */
    public void scanQRCode(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE, ScanOptions.CODE_128);
        options.setBeepEnabled(false);
        options.setOrientationLocked(false);
        barLauncher.launch(options);
    }

    /**
     * Click listener for generating QR codes
     */
    public void generateQRCode(View view) {
        try {
            EditText editText = findViewById(R.id.qr_codes_edit_text);
            String text = editText.getText().toString();

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            barcodeBitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE,
                    400, 400);

            ((ImageView) findViewById(R.id.qr_code_viewer)).setImageBitmap(barcodeBitmap);
        } catch (WriterException exception) {
            Utilities.createToast(getBaseContext(),
                    getString(R.string.code_generation_failed), 120).show();
        }
    }

    /**
     * Click listener for loading previous cipher results into the EditText
     */
    public void previousCipherResults(View view) {
        EditText editText = findViewById(R.id.qr_codes_edit_text);

        if (Utilities.lastOutput != null && Utilities.lastOutput.length() > 0) {
            editText.setText(Utilities.lastOutput);
        } else {
            Utilities.createToast(getBaseContext(), getString(R.string.paste_fail), 120).show();
        }
    }

    /**
     * Click listener for saving a generated QR code to system files
     */
    public void saveQRCode(View view) {
        if (barcodeBitmap != null) {
            Utilities.downloadBitmap(this, barcodeBitmap);
        } else {
            Utilities.createToast(getBaseContext(),
                    getString(R.string.no_barcode_to_save), 120).show();
        }
    }

    /**
     * Click listener to decode the EditText's text with a Macro
     */
    public void decodeWithMacro(View view) {
        EditText editText = findViewById(R.id.qr_codes_edit_text);

        if (editText.length() != 0) {
            // decode output with macro by launching the MacrosActivity
            Intent intent = new Intent(getBaseContext(), MacrosActivity.class);
            intent.putExtra(getString(R.string.bundle_macro_input),
                    editText.getText().toString());
            startActivity(intent);
        } else {
            Utilities.createToast(getBaseContext(), getString(R.string.no_output),
                    120).show();
        }
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