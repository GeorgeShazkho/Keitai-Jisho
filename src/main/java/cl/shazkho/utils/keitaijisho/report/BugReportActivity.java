package cl.shazkho.utils.keitaijisho.report;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import cl.shazkho.utils.keitaijisho.R;


public class BugReportActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);

        // Initialize elements
        Spinner type = (Spinner) findViewById(R.id.report_type_spinner);
        Spinner priority = (Spinner) findViewById(R.id.report_urgency_spinner);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.report_toolbar);
        TextView android_version = (TextView) findViewById(R.id.report_auto_android_version);
        TextView app_version = (TextView) findViewById(R.id.report_auto_app_version);
        TextView device_name = (TextView) findViewById(R.id.report_auto_device);

        // Configure elements
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bug report");
        android_version.setText( android_version.getText().toString() + getAndroidVersion() );
        app_version.setText( app_version.getText().toString() + getString(R.string.version) );
        device_name.setText( device_name.getText().toString() + getDeviceName() );


        // Spinners hint-adapted inflation
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };
        ArrayAdapter<String> urgencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeAdapter.add("User interface");
        typeAdapter.add("Typo/Orthography/Redaction");
        typeAdapter.add("Application crashes");
        typeAdapter.add("Wrong results");
        typeAdapter.add("Other");
        typeAdapter.add("Select from list");
        type.setAdapter(typeAdapter);
        type.setSelection(typeAdapter.getCount()); //display hint

        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urgencyAdapter.add("Minor");
        urgencyAdapter.add("Normal");
        urgencyAdapter.add("Major");
        urgencyAdapter.add("Critical");
        urgencyAdapter.add("Priority level of the issue");
        priority.setAdapter(urgencyAdapter);
        priority.setSelection(urgencyAdapter.getCount()); //display hint



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bug_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                finish();
                return true;
            case R.id.report_menu_send:
                //ToDo
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release +")";
    }

    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        if (manufacturer.equalsIgnoreCase("HTC")) {
            // make sure "HTC" is fully capitalized.
            return "HTC " + model;
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        final char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (final char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

}
