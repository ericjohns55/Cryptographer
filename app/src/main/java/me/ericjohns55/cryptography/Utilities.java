package me.ericjohns55.cryptography;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.text.HtmlCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utilities {
    // Holds onto a toast object so we can hide previous messages before displaying
    // another one - this prevents a backlog from spammed messages
    private static Toast toast;

    // Holds onto the last output for any cipher
    public static String lastOutput = "";

    /**
     * Generates a larger toast message
     */
    public static Toast createToast(Context context, String text, int yOffset) {
        if (toast != null) {
            // hide previous toasts so we do not get a backlog of messages if the user
            // spams something that shows them
            toast.cancel();
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.setSpan(new RelativeSizeSpan(1.5f), 0, builder.length(), 0);

        toast = Toast.makeText(context, builder, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, yOffset);

        return toast;
    }

    /**
     * Generates an AlertDialog with one button and no click listeners
     */
    public static AlertDialog createDialog(Context context, String message_text,
                                           String button_text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name).setMessage(message_text);
        builder.setPositiveButton(button_text, null);
        return builder.create();
    }

    /**
     * Formats a string resource to reflect bold, italics, or underline
     * @param context The activity to access system strings from
     * @param id The string resource to grab
     * @return The formatted string
     */
    public static Spanned parseID(Activity context, int id) {
        return HtmlCompat.fromHtml(context.getResources().getString(id),
                HtmlCompat.FROM_HTML_MODE_COMPACT);
    }

    /**
     * Resets an input field to have no text and no underlined error marker
     */
    public static void resetInputField(EditText field, Resources resources) {
        field.setText("");
        field.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.lighter_gray, null)));
    }

    /**
     * Downloads a bitmap to system memory with the name of the current timestamp
     * @param activity The activity performing this action (used for context)
     * @param bitmap The bitmap to download
     */
    public static void downloadBitmap(Activity activity, Bitmap bitmap) {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

        File storageDirectory =
                activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // create a new file in the pictures directory with the timestamp name
        File file = new File(storageDirectory, timeStamp + ".png");

        try {
            // compress the bitmap with 100% quality so we do not lose encoded data
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            stream.flush();
            stream.close();

            // insert the image into the picture directory
            MediaStore.Images.Media.insertImage(activity.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), file.getName());

            createToast(activity, activity.getString(R.string.could_save), 120).show();
        } catch (IOException e) {
            e.printStackTrace();
            createToast(activity, activity.getString(R.string.couldnt_save), 120).show();
        }
    }

    /**
     * Array of the frequency of letters in English text
     * Index 0 is A, Index 1 is B, etc
     * Source: https://www3.nd.edu/~busiforc/handouts/cryptography/letterfrequencies.html
     */
    public static float[] englishLetterFrequencies =
            {8.4966f, 2.0720f, 4.5388f, 3.3844f, 11.1607f, 1.8121f, 2.4705f,
                    3.0034f, 7.5448f, 0.1965f, 1.1016f, 5.4893f, 3.0129f, 6.6544f,
                    7.1635f, 3.1671f, 0.1962f, 7.5809f, 5.7351f, 6.9509f, 3.6308f,
                    0.2722f, 1.2899f, 0.2902f, 1.7779f, 0.2722f};

    /**
     * Labels for the X-Axis in Frequency Analysis
     */
    public static String[] indices = {" ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * Converts input text into a binary string where each character turns into an
     * 8-bit length representation consisting of 0's and 1's
     * @param input The input text
     * @return The binary string
     */
    public static String stringToBinary(String input) {
        StringBuilder binaryString = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            // replace all white space with 0's to pad
            binaryString.append(String.format("%8s",
                    Integer.toBinaryString(input.charAt(i))).replaceAll(" ", "0"));
        }

        return binaryString.toString();
    }

    /**
     * Converts a binary string with just 0's and 1's to plaintext
     * @param binaryRepresentation The binary string to parse
     * @return The plaintext string corresponding to the binary representation
     */
    public static String binaryToString(String binaryRepresentation) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < binaryRepresentation.length() / 8; i++) {
            // Parse characters in groups of 8
            String substring = binaryRepresentation.substring(i * 8, (i + 1) * 8);

            // parse the substring into a base 2 integer to get character representation
            int characterCode = Integer.parseInt(substring, 2);

            // append the character to the new string
            stringBuilder.append((char) characterCode);
        }

        return stringBuilder.toString();
    }

    /**
     * Adjusts the value of a number to be even or odd, and not divisible by 5 for
     * encoding in an image
     * @param number The number to adjust
     * @param makeEven True if we are making even, false if we are making odd
     * @return The adjusted number
     */
    public static int adjustColorChannel(int number, boolean makeEven) {
        if (makeEven) {
            if (number % 5 == 0) { // take care of numbers that could trigger and end case
                number--;
            }

            if (number % 2 != 0) { // if we are not already even, add one
                number++;

                if (number % 5 == 0) { // if we are now divisible by 5, adjust by adding 2
                    number += 2;
                }

                if (number > 255) { // account for going out of bounds
                    number = 254;
                }
            }
        } else {
            if (number % 5 == 0) { // take care of numbers that could trigger and end case
                number--;
            }

            if (number % 2 != 1) { // if the number is not already odd, add one
                number++;

                if (number % 5 == 0) { // % 5 is ending code, so the even number cannot
                    // be divisible by 5; so add 2
                    number += 2;
                }

                if (number > 255) { // adjust for bounds if we exceed
                    number = 253;
                }
            }
        }

        return number;
    }

    /**
     * Adjusts the ending digit to be divisible by 5 to represent the end of encoded
     * text in an image
     * @param number The input number to adjust
     * @return The adjusted input number
     */
    public static int adjustEndingDigit(int number) {
        if (number % 5 != 0) { // only touch if it is already not divisible by 5
            number += (number % 5);

            if (number > 255) { // adjust back to in bounds if we go out
                number = 255;
            }
        }

        return number;
    }
}
