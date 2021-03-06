package cl.shazkho.utils.keitaijisho.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import cl.shazkho.utils.keitaijisho.R;
import cl.shazkho.utils.keitaijisho.database.DatabaseManager;
import cl.shazkho.utils.keitaijisho.objects.ResponseObject;
import cl.shazkho.utils.keitaijisho.objects.custom.CustomActionBarActivity;
import cl.shazkho.utils.keitaijisho.results.ResultsActivity;
import cl.shazkho.utils.keitaijisho.tools.JapaneseWritingHelper;


/**
 * <p>This is the main activity. It's basically a search-only activity. Uses data collected from the
 * form and uses it to send a retrieval task, searching for japanese concept matches.</p>
 *
 * <p>Keitai Jisho Activities works using a simple abstraction
 * of the Activity Class, more precisely, it uses the
 * ActionBarActivity Class to have compatibility with
 * the new Toolbar, but with older versions of Android.</p>
 *
 * @author George Shazkho
 * @version 0.7
 * @since 2015-03-08
 */
public class SearchActivity extends CustomActionBarActivity	implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher, TextView.OnEditorActionListener, Animation.AnimationListener {


	// INSTANCE VARIABLES

	private EditText mSearchField;
	private Switch mInEnglish;
	private TextView mSearchTitle;
    private Button mSearchButton;
    private View mImportPanel;


	// IMPLEMENTED HOOKS METHODS

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Base config
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Initialize elements
		Spinner mDicSelect = (Spinner) findViewById(R.id.search_dicSelect_spinner);
		mSearchField = (EditText) findViewById(R.id.search_searchBox_field);
		mSearchButton = (Button) findViewById(R.id.search_buttons_search);
		mInEnglish = (Switch) findViewById(R.id.search_config_english);
		Button mClearButton = (Button) findViewById(R.id.CLR);
		ArrayList<String> dictionaries = new ArrayList<>();
		mSearchTitle = (TextView) findViewById(R.id.search_searchBox_title);


		// Configure elements
		dictionaries.add(getResources().getString(R.string.search_general_dict) + " (Edict)");
		this.spinnerInflate(mDicSelect, dictionaries);
		Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/roboto/medium.ttf");
		mSearchButton.setTypeface(typeFace);
        mSearchButton.setEnabled(false);
		mClearButton.setTypeface(typeFace);

        mSearchField.setOnEditorActionListener(this);
		mSearchButton.setOnClickListener(this);
		mSearchField.setOnFocusChangeListener(this);
        mSearchField.addTextChangedListener(this);
		mClearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSearchField.setText("");
			}
		});

        // Whatsnew section spawn
        SharedPreferences preferences = getSharedPreferences("main_preferences", 0);
        String last_version = preferences.getString("last_version", "0");
        if ( !last_version.equals( getString( R.string.version ) ) ) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mImportPanel = ((ViewStub) findViewById(R.id.search_whatsnew)).inflate();
            WebView changelog = (WebView) mImportPanel.findViewById(R.id.whatsnew_changelog);
            changelog.loadData(
                    getString(R.string.whatsnew_changelog),
                    "text/html; charset=UTF-8",
                    null
            );
            changelog.setBackgroundColor(getResources().getColor(R.color.transparent));
            Button go_to_app = (Button) mImportPanel.findViewById(R.id.whatsnew_button);
            go_to_app.setOnClickListener(this);
        }

	}


	// CLASS SPECIFIC METHODS

    /**
     * Responses to a search task when done.
     *
     * @param response The response obtained, in JSON standard.
     */
    @Override
    public void requestCallback(ResponseObject response) {
        Intent intent = new Intent( getApplicationContext(), ResultsActivity.class );
        intent.putExtra("result", response);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                getApplicationContext(),
                R.anim.abc_slide_in_top,
                R.anim.abc_slide_out_top
        );
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    /**
	 * Inflates the dictionary Spinner.
	 *
	 * @param spinner Reference to the Spinner
	 * @param elements ArrayList with the elements to put.
	 */
	private void spinnerInflate( Spinner spinner, ArrayList<String> elements){
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
			android.R.layout.simple_spinner_item, elements);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}

    /**
     * Handles the requests triggered by any source (soft. keyboard or button).
     */
    private void handleRequest() {
        String input_query = mSearchField.getText().toString().toLowerCase();
        mSearchField.setText(input_query);
        if (input_query.contains(" ")) {
            //mSearchField.setError("Search string cannot contain spaces.");      /* Input has spaces */
            mSearchField.setError(getResources().getString(R.string.search_error_spaces));      /* Input has spaces */
            return;
        }

        String writing = JapaneseWritingHelper.isSomething(input_query);
        if ( writing.equals( "mixed" ) ) {
            mSearchField.setError(getResources().getString(R.string.search_error_mixed));      /* Invalid writing system */
            return;
        }

        if ( mInEnglish.isChecked() ) {
            if ( !writing.equals("kana") ) {
                //mSearchField.setError("English search may only contain roman characters.");      /* Force english with non roman characters */
                mSearchField.setError(getResources().getString(R.string.search_error_english_kana));      /* Force english with non roman characters */
                return;
            } else {
                writing = "gloss";
            }
        }

        ResponseObject responseObject = new ResponseObject("search", input_query + "+" + writing);
        DatabaseManager manager = new DatabaseManager(this);
        manager.query(responseObject, false);
    }


	// IMPLEMENTED METHOD FROM PARENT CLASS 'View.OnClickListener'

	@Override
	public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.whatsnew_button:
                SharedPreferences preferences = getSharedPreferences("main_preferences", 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("last_version", getString( R.string.version ) );
                editor.apply();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                fadeOut.setStartOffset(0);
                fadeOut.setDuration(300);
                fadeOut.setAnimationListener(this);
                mImportPanel.startAnimation(fadeOut);

                break;
            case R.id.search_buttons_search:
                handleRequest();
                break;
            default:
                break;
        }
	}


	// IMPLEMENTED METHOD FROM PARENT CLASS 'View.OnFocusChangeListener'

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus) {
			mSearchTitle.setTextColor(getResources().getColor(R.color.AppTheme_primaryColor));
		} else {
			mSearchTitle.setTextColor(getResources().getColor(R.color.AppTheme_text_base));
		}
	}


    // IMPLEMENTED METHODS FROM PARENT CLASS 'TextWatcher'

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mSearchButton.setEnabled(!mSearchField.getText().toString().equals(""));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    // IMPLEMENTED METHOD FROM PARENT CLASS 'TextView.OnEditorActionListener'

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ( actionId == EditorInfo.IME_ACTION_SEARCH ) {
            if (!mSearchField.getText().toString().equals("")) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                try {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    Log.w("Search Action", "Error hiding the soft keyboard.");
                    Log.d("Search Action Error", e.toString());
                    e.printStackTrace();
                }
                handleRequest();
            }
        }
        return false;
    }


    // IMPLEMENTED METHODS FROM PARENT CLASS 'Animation.AnimationListener'

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mImportPanel.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
