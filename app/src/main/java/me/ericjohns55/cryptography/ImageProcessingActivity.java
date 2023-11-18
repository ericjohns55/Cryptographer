package me.ericjohns55.cryptography;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Driver for the ImageProcessing Activity
 * This is in charge of encoding and decoding images
 */

public class ImageProcessingActivity extends AppCompatActivity {
    // The saved path of a picture taken with the camera
    private String takenPicturePath;

    // Bitmap of the current image in the ImageView (this can be a taken picture or a
    // choosen picture from gallery)
    private Bitmap currentPicture = null;

    // Result launchers for taking pictures and accessing the gallery
    private ActivityResultLauncher<Intent> pictureLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    // Color channel to encode/decode messages from images with
    private int colorChannel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);

        Toolbar toolbar = findViewById(R.id.back_toolbar);
        toolbar.inflateMenu(R.menu.back_menu);
        setSupportActionBar(toolbar);

        // setting up the spinner for color channels

        Spinner spinner = findViewById(R.id.color_channel_spinner);
        spinner.setSelection(0);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        getBaseContext(), R.array.color_channels, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos,
                                       long l) {
                colorChannel = pos; // red = 0, green = 1, blue = 2
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner.setSelection(0, true); // default to red
            }
        });

        // setting up the radio group for encoding or decoding
        // when the text changes we want to adjust the UI to change the text on some of
        // the buttons
        RadioGroup encodingGroup = findViewById(R.id.encode_decode_radio_group);
        encodingGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton clicked = radioGroup.findViewById(i);
            boolean encode =
                    clicked.getTag().toString().equals(getString(R.string.encode_image));
            adjustUI(encode);
        });

        // checking for camera and write external storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        // registering the take picture result
        pictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // decode the picture from the taken picture path in gallery
                        currentPicture = BitmapFactory.decodeFile(takenPicturePath);
                        ((ImageView) findViewById(R.id.processor_image_view)).setImageBitmap(currentPicture);
                    }
                }
        );

        // registering the choose from gallery result
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri galleryPic = result.getData().getData(); // url of the picture

                        try {
                            currentPicture =
                                    MediaStore.Images.Media.getBitmap(getContentResolver(),
                                            galleryPic); // grab picture from gallery

                            if (currentPicture != null) {
                                ((ImageView) findViewById(R.id.processor_image_view)).setImageBitmap(currentPicture);
                            } else {
                                Utilities.createToast(getBaseContext(),
                                        getString(R.string.couldnt_load_picture), 120).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        adjustUI(true);
    }

    /**
     * Adjusts the UI to change button text to encode or decode depending on the radio
     * group selection
     * @param encode True if we are encoding, false otherwise
     */
    public void adjustUI(boolean encode) {
        EditText editText = findViewById(R.id.image_processor_edit_text);
        TextView spinnerLabel = findViewById(R.id.spinner_label);
        Button runProcessor = findViewById(R.id.run_processor_button);
        Button inputOutputUtilization = findViewById(R.id.utilize_input_output_button);

        if (encode) {
            runProcessor.setText(getString(R.string.run_encoder));
            runProcessor.setTag(getString(R.string.run_encoder));
            editText.setHint(getString(R.string.encode_hint));
            editText.setEnabled(true);
            inputOutputUtilization.setText(getString(R.string.previous_input));
            inputOutputUtilization.setTag(getString(R.string.encode));
            spinnerLabel.setText(Utilities.parseID(this, R.string.encoding_channel));
        } else {
            runProcessor.setText(getString(R.string.run_decoder));
            runProcessor.setTag(getString(R.string.run_decoder));
            editText.setHint(getString(R.string.decode_hint));
            editText.setEnabled(false);
            inputOutputUtilization.setText(getString(R.string.decode_output));
            inputOutputUtilization.setTag(getString(R.string.decode));
            spinnerLabel.setText(Utilities.parseID(this, R.string.decoding_channel));
        }

        editText.setText(null);
    }

    /**
     * Click listener for the download picture button
     */
    public void downloadPicture(View view) {
        if (currentPicture == null) {
            Utilities.createToast(getBaseContext(), getString(R.string.null_picture),
                    120).show();
            return;
        }

        Utilities.downloadBitmap(this, currentPicture);
    }

    /**
     * Click listener for the choose picture from gallery button
     */
    public void choosePicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        galleryLauncher.launch(intent);
    }

    /**
     * Click listener for the take picture button
     */
    public void takePicture(View view) {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "JPEG_" + timeStamp;

        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            // create a file to store the result in and save it into a field variable
            // for reading later
            File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory);
            takenPicturePath = imageFile.getAbsolutePath();

            // use the file provider created in the Manifest to pass into the camera
            Uri imageUri = FileProvider.getUriForFile(getBaseContext(),
                    getPackageName() + ".fileprovider", imageFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            pictureLauncher.launch(intent); // launch camera
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * If we are in encode mode, this will set the text of the input field to be the
     * last output performed in the application
     * If we are in decode mode, this will take our output text and pass it to the
     * MacrosActivity for decoding
     */
    public void inputOutputButton(View view) {
        EditText editText = findViewById(R.id.image_processor_edit_text);

        if (view.getTag().toString().equals(getString(R.string.encode))) {
            if (Utilities.lastOutput != null && Utilities.lastOutput.length() > 0) {
                editText.setText(Utilities.lastOutput);
            } else {
                Utilities.createToast(getBaseContext(), getString(R.string.paste_fail), 120).show();
            }
        } else {
            if (editText.length() != 0) {
                // decode output with macro
                Intent intent = new Intent(getBaseContext(), MacrosActivity.class);
                intent.putExtra(getString(R.string.bundle_macro_input),
                        editText.getText().toString());
                startActivity(intent);
            } else {
                Utilities.createToast(getBaseContext(), getString(R.string.no_output),
                        120).show();
            }
        }
    }

    /**
     * Listener for processing an image via encoding or decoding
     */
    public void processImage(View view) {
        if (currentPicture == null) { // cannot do anything with a null picture
            Utilities.createToast(getBaseContext(), getString(R.string.null_picture),
                    120).show();
            return;
        }

        // convert our bitmap into an array of pixels
        int[] pixels = new int[currentPicture.getWidth() * currentPicture.getHeight()];
        currentPicture.getPixels(pixels, 0, currentPicture.getWidth(), 0, 0,
                currentPicture.getWidth(), currentPicture.getHeight());

        if (view.getTag().equals(getString(R.string.run_encoder))) {    // encoding
            String input =
                    ((EditText) findViewById(R.id.image_processor_edit_text)).getText().toString();
            String binary = Utilities.stringToBinary(input);

            // loop over the length of our binary string
            for (int i = 0; i <= binary.length(); i++) {
                // calculate the rgb values from the current pixel
                int red = (pixels[i] >> 16) & 0xFF;
                int green = (pixels[i] >> 8) & 0xFF;
                int blue = pixels[i] & 0xFF;

                // if we are at the ending digit, we want to adjust it to be divisible
                // by 5 to signify the end of the binary string for decoding later
                if (i == binary.length()) {
                    if (colorChannel == 0) {    // adjust digit based on color channel
                        pixels[i] = Color.rgb(Utilities.adjustEndingDigit(red), green,
                                blue);
                    } else if (colorChannel == 1) {
                        pixels[i] = Color.rgb(red, Utilities.adjustEndingDigit(green),
                                blue);
                    } else if (colorChannel == 2) {
                        pixels[i] = Color.rgb(red, green,
                                Utilities.adjustEndingDigit(blue));
                    }

                    continue;
                }

                // boolean to represent 0's and 1's
                boolean evenValue = binary.charAt(i) == '0';

                // adjust the color channel to be even or odd
                if (colorChannel == 0) {
                    pixels[i] = Color.rgb(Utilities.adjustColorChannel(red, evenValue),
                            green, blue);
                } else if (colorChannel == 1) {
                    pixels[i] = Color.rgb(red, Utilities.adjustColorChannel(green,
                                    evenValue), blue);
                } else {
                    pixels[i] = Color.rgb(red, green, Utilities.adjustColorChannel(blue,
                            evenValue));
                }
            }

            // now we are done editing the pixel array so we want to load our edited
            // pixels into the currentPicture bitmap
            currentPicture = currentPicture.copy(currentPicture.getConfig(), true);
            currentPicture.setPixels(pixels, 0, currentPicture.getWidth(), 0, 0,
                    currentPicture.getWidth(), currentPicture.getHeight());

            // finally done, update our ImageView
            ImageView imageView = findViewById(R.id.processor_image_view);
            imageView.setImageBitmap(currentPicture);

            Utilities.createToast(getBaseContext(), getString(R.string.encode_successful), 120).show();
        } else { // decode images
            StringBuilder binaryString = new StringBuilder();
            int currentIndex = 0;

            while (currentIndex < pixels.length) {
                // grab the current value from the red, green, or blue channel
                int currentValue = 0;

                if (colorChannel == 0) {
                    currentValue = (pixels[currentIndex] >> 16) & 0xFF;
                } else if (colorChannel == 1) {
                    currentValue = (pixels[currentIndex] >> 8) & 0xFF;
                } else if (colorChannel == 2) {
                    currentValue = pixels[currentIndex] & 0xFF;
                }

                // if the value is divisible by 5, we are at the end of the string
                if (currentValue % 5 == 0) {
                    break;
                }

                // if the value is divisible by 2 it must be a 0, otherwise a 1
                if (currentValue % 2 == 0) {
                    binaryString.append('0');
                } else {
                    binaryString.append('1');
                }

                currentIndex++; // increment for the next pixel
            }

            // convert our binary string back to plaintext
            String decodedText = Utilities.binaryToString(binaryString.toString());

            // update the EditText with the decoded message or warn of failure
            if (decodedText.length() != 0) {
                ((EditText) findViewById(R.id.image_processor_edit_text)).setText(decodedText);

                Utilities.createToast(getBaseContext(), getString(R.string.decode_successful),
                        120).show();
            } else {
                Utilities.createToast(getBaseContext(), getString(R.string.decode_empty_text),
                        120).show();
            }
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