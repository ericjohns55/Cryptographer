package me.ericjohns55.cryptography;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import me.ericjohns55.cryptography.ciphers.Ciphers;
import me.ericjohns55.cryptography.fragments.SubstitutionCrackerFragment;
import me.ericjohns55.cryptography.fragments.VigenereCrackerFragment;

/**
 * Driver for the Frequency Analysis activity
 * This is in charge of setting up the graphics and loading the cipher cracker fragments
 *
 * @author Eric Johns
 */

public class FrequencyAnalysisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequency_analysis);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.back_menu);
        setSupportActionBar(toolbar);

        // grab the cipher type and input string from the bundle
        String input = getIntent().getStringExtra(getString(R.string.freq_analysis_input_bundle));
        Ciphers cipher =
                Ciphers.valueOf(getIntent().getStringExtra(getString(R.string.freq_analysis_cipher_bundle)));

        // determine whether or not we are loading the SubstitutionCracker or
        // VigenereCracker fragment, then load it
        Class<? extends Fragment> cipherClass;
        if (cipher == Ciphers.SUBSTITUTION) {
            cipherClass = SubstitutionCrackerFragment.class;
        } else {
            cipherClass = VigenereCrackerFragment.class;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cipher_cracker_container, cipherClass, getIntent().getExtras())
                .setReorderingAllowed(true)
                .commit();

        // loading the passed input into the edit text
        EditText textField = findViewById(R.id.freq_analysis_input_field);
        textField.setText(input);

        // setting up the left and right graphs
        setupGraph(input, -1, -1, true);
        setupGraph(input, -1, -1, false);
    }

    /**
     *
     * @param input The input string to analyze the frequency of
     * @param groupLength The length of the group for Vigenère ciphers
     * @param groupNum The index of the group for Vigenère ciphers
     * @param mainGraph True if we are setting up the relative frequency graph, false
     *                  if we are setting up the english language graph
     */
    public void setupGraph(String input, int groupLength, int groupNum, boolean mainGraph) {
        BarChart barChart;

        // determining the bar chart to set up
        if (mainGraph) {
            barChart = findViewById(R.id.frequency_graph);
        } else {
            barChart = findViewById(R.id.english_frequency_graph);
        }

        // resetting the data, because this method will be called multiple times if the
        // group length or number changed
        barChart.clear();

        // disable the legend and description because it adds extra noise to the UI and
        // does not look as clean
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);

        BarDataSet barDataSet;

        // grabbing the bar graph information based on group length and number
        if (mainGraph) {
            if (groupLength == -1 && groupNum == -1) {
                barDataSet = new BarDataSet(getBarEntries(input, -1, -1), "");
            } else {
                barDataSet = new BarDataSet(getBarEntries(input, groupLength, groupNum), "");
            }
        } else {
            barDataSet = new BarDataSet(getEnglishFrequency(), "");
        }

        // simple formatting of the text on the graph
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(getColor(R.color.white));
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        // formatting value above the bars: puts data in terms of 00.00%
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return decimalFormat.format(value) + "%";
            }
        };

        barData.setValueFormatter(formatter);

        // adjusting the zoom of the axes so the data scales well on the screen
        adjustAxes(barChart, barData);

        // zooming in an appropriate amount so the data is readable and to make it
        // obvious that the user can choose to scroll or not
        barChart.setData(barData);
        barChart.fitScreen();
        barChart.zoom(8f, 1f, 1f, 1f);
    }

    /**
     * Parses the frequency of letters in the English language into an ArrayList for
     * the bar data set
     * @return ArrayList containing BarEntries for each letter in the English language
     */
    public ArrayList<BarEntry> getEnglishFrequency() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (char i = 'A'; i <= 'Z'; i++) {
            int index = i - 'A';

            // start indexing at 1 so the first entry does not hang off the edge (this
            // is a weird quirk the library has)
            entries.add(new BarEntry(index + 1, Utilities.englishLetterFrequencies[index]));
        }

        return entries;
    }

    /**
     * Parses the frequency of letters in an input String and puts them into an
     * ArrayList for the bar data set
     * @param input The input string
     * @param groupLength The length of each group if the string is split
     * @param groupNum The index of the group if the string is split
     * @return An ArrayList with a BarEntry for each letter in the group
     */
    public ArrayList<BarEntry> getBarEntries(String input, int groupLength,
                                             int groupNum) {
        HashMap<Character, Integer> frequency = Ciphers.getFrequency(input, groupLength,
                groupNum);

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (Character character : frequency.keySet()) {
            if (character == '#') continue; // ignore the counter character

            int letterIndex = (character - 'A');

            // convert into percent frequency instead of counting the number of entries
            float percentFrequency =
                    ((float) frequency.get(character) / frequency.get('#')) * 100;
            entries.add(new BarEntry(letterIndex + 1, percentFrequency));
        }

        return entries;
    }

    /**
     * Adjusts the X and both Y axes to scale the data to the graph
     * @param barChart The chart to adjust the axes off
     * @param barData The dataset to pull values off of for adjustment
     */
    public void adjustAxes(BarChart barChart, BarData barData) {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(getColor(R.color.white));

        // formatting the X-Axis to show letters instead of numbers
        xAxis.setValueFormatter(new IndexAxisValueFormatter(Utilities.indices));

        // Each BarChart has a left and right Y-Axis, so they both must be adjusted
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setTextColor(getColor(R.color.white));
        yAxisLeft.setAxisMaximum(barData.getYMax() + 2f);
        yAxisLeft.setAxisMinimum(0f);

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setTextColor(getColor(R.color.white));
        yAxisRight.setAxisMaximum(barData.getYMax() + 2f);
        yAxisRight.setAxisMinimum(0f);
    }

    /**
     * Puts the alphabet passed as a parameter into a bundle, and finishes this activity
     * to return to the SubstitutionFragment
     * @param alphabet The alphabet to return to the SubstitutionFragment
     */
    public void returnAlphabet(String alphabet) {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.freq_analysis_alphabet_bundle),
                alphabet);
        setResult(0, intent);
        finish();
    }

    /**
     * Puts the key passed as a parameter into a bundle, and finishes this activity
     * to return to the VigenereFragment
     * @param key The key to return to the VigenereFragment
     */
    public void returnKey(String key) {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.freq_analysis_key_bundle),
                key);
        setResult(0, intent);
        finish();
    }

    // hides keyboard when tapping out of EditText (makes for easier exit of the EditText)
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getCurrentFocus() != null) {
            InputMethodManager manager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(event);
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