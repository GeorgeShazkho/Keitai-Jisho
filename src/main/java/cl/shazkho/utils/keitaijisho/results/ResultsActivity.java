package cl.shazkho.utils.keitaijisho.results;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.shazkho.utils.keitaijisho.R;
import cl.shazkho.utils.keitaijisho.database.DatabaseManager;
import cl.shazkho.utils.keitaijisho.objects.ResponseObject;
import cl.shazkho.utils.keitaijisho.objects.custom.CustomActionBarActivity;
import cl.shazkho.utils.keitaijisho.resultdetails.ResultDetailActivity;
import cl.shazkho.utils.keitaijisho.settings.SettingsActivity;
import cl.shazkho.utils.keitaijisho.tools.StaticHelpers;


/**
 * Activity that shows the results of a single query to the database.
 *
 * <p>Keitai Jisho Activities works using a simple abstraction
 * of the Activity Class, more precisely, it uses the
 * ActionBarActivity Class to have compatibility with
 * the new Toolbar, but with older versions of Android.</p>
 *
 * @author George Shazkho
 * @version 0.7.1
 * @since 2015-03-06
 */
public class ResultsActivity extends CustomActionBarActivity implements TextToSpeech.OnInitListener, StaticHelpers, View.OnClickListener {


    // INSTANCE VARIABLES

    //private TextToSpeech mTextToSpeech;
    private ResultListAdapter mAdapter;
    private ResponseObject mResponseObject;
    private ListView mResultList;


    // OVERRIDDEN HOOK METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Base config
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize elements
        //mTextToSpeech = new TextToSpeech(this, this);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.results_toolbar);
        mResultList = (ListView) findViewById(R.id.results_list);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Getting intent
        Intent intent = getIntent();
        mResponseObject = intent.getParcelableExtra("result");
        Log.i("Result Activity", "Intent received with data: "+mResponseObject.getmKey().toUpperCase()+", "+mResponseObject.getmDatabase().toUpperCase());

        // Inflating

        String JSON = mResponseObject.getmJsonResult();
        try {
            JSONObject jsonObj = new JSONObject( JSON );
            JSONArray home = jsonObj.getJSONArray("response");
            if ( home.length() != 0 ) {
                mAdapter = new ResultListAdapter(this, mResponseObject);
                mResultList.setAdapter(mAdapter);
                tryToShowHint();
            } else {
                LinearLayout no_result = (LinearLayout) findViewById(R.id.results_no_result_layout);
                no_result.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(this);
                RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.no_result, null, false);
                no_result.addView(layout);
            }
        } catch (JSONException e) {
            Log.e("Search List Adapter", "Error loading JSON String into JSONArray Object.");
            Log.e("Search List Adapter", e.toString());
            e.printStackTrace();
        }



    }

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.menu_results, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item) {
		int id = item.getItemId();
		switch(id){
			case R.id.results_menu_force_reload:
                DatabaseManager manager = new DatabaseManager(this);
                manager.query( mResponseObject, true );
				break;
			case R.id.results_menu_settings:
                Intent intent = new Intent(ResultsActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
			case android.R.id.home:
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
					TaskStackBuilder.create(this)
						.addNextIntentWithParentStack(upIntent)
						.startActivities();
				} else {
					NavUtils.navigateUpTo(this, upIntent);
				}
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
        /*
		if (mTextToSpeech != null) {
			mTextToSpeech.stop();
			mTextToSpeech.shutdown();
		}
		*/
		super.onDestroy();
	}


    // CLASS SPECIFIC METHODS

    /**
     * Tries to show a hint over the result list.
     */
    private void tryToShowHint () {
        SharedPreferences preferences = getSharedPreferences("main_preferences", 0);
        boolean is_kanji_hint_disposed = preferences.getBoolean("is_kanji_hint_disposed", false);
        if ( !is_kanji_hint_disposed ) {
            RelativeLayout hint = (RelativeLayout) findViewById(R.id.results_hint);
            RelativeLayout hintBackground = (RelativeLayout) findViewById(R.id.results_hint_background);
            hint.setVisibility(View.VISIBLE);
            hintBackground.setVisibility(View.VISIBLE);
            Button hintButton = (Button) findViewById(R.id.result_hint_button);
            hintButton.setOnClickListener(this);
        }
    }

	// OVERRIDDEN METHOD PROM PARENT CLASS 'CustomActionBarActivity'

    @Override
    public void requestCallback(ResponseObject response) {

        if (response.getmDatabase().equals("search")) {
            mResponseObject = response;
            String JSON = mResponseObject.getmJsonResult();
            try {
                JSONObject jsonObj = new JSONObject( JSON );
                JSONArray home = jsonObj.getJSONArray("response");
                if ( home.length() != 0 ) {
                    mAdapter = new ResultListAdapter(this, mResponseObject);
                    mResultList.setAdapter(mAdapter);
                    tryToShowHint();
                } else {
                    LinearLayout no_result = (LinearLayout) findViewById(R.id.results_no_result_layout);
                    no_result.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(this);
                    RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.no_result, null, false);
                    no_result.addView(layout);
                }
            } catch (JSONException e) {
                Log.e("Search List Adapter", "Error loading JSON String into JSONArray Object.");
                Log.e("Search List Adapter", e.toString());
                e.printStackTrace();
            }


        } else if (response.getmDatabase().equals("details")) {
            Intent intent = new Intent( getApplicationContext(), ResultDetailActivity.class );
            intent.putExtra("result", response);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                    getApplicationContext(),
                    R.anim.abc_slide_in_top,
                    R.anim.abc_slide_out_top
            );
            ActivityCompat.startActivity(this, intent, options.toBundle());
        }
    }


    // OVERRIDDEN METHODS FROM PARENT CLASS 'TextToSpeech.OnInitListener'

	@Override
	public void onInit(int status) {
        /*
		currentMethod = "onInit[TTS]";
		if (status == TextToSpeech.SUCCESS) {
			int result = mTextToSpeech.setLanguage(Locale.JAPAN);
			// mTextToSpeech.setPitch(5); // set pitch level
			// mTextToSpeech.setSpeechRate(2); // set speech speed rate
			if (result == TextToSpeech.LANG_MISSING_DATA
				|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				log(ERROR, "Language is not supported");
				Toast toast = Toast.makeText(
					getApplicationContext(),
					getResources().getString(R.string.result_tts_error1),
					Toast.LENGTH_SHORT);
				toast.show();
			} else {
				mIsTtsReady = true;
			}

		} else {
			log(ERROR, "Initialization Failed");
			Toast toast = Toast.makeText(
				getApplicationContext(),
				getResources().getString(R.string.result_tts_error2),
				Toast.LENGTH_SHORT);
			toast.show();
		}
		*/
	}

    @Override
    public void onClick(View v) {
        SharedPreferences preferences = getSharedPreferences("main_preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_kanji_hint_disposed", true );
        editor.apply();
        RelativeLayout hint = (RelativeLayout) findViewById(R.id.results_hint);
        RelativeLayout hintBackground = (RelativeLayout) findViewById(R.id.results_hint_background);
        hint.setVisibility(View.GONE);
        hintBackground.setVisibility(View.GONE);
    }

}
