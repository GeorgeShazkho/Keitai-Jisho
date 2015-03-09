package cl.shazkho.utils.keitaijisho.resultdetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import cl.shazkho.utils.keitaijisho.R;
import cl.shazkho.utils.keitaijisho.about.AboutActivity;
import cl.shazkho.utils.keitaijisho.database.DatabaseManager;
import cl.shazkho.utils.keitaijisho.objects.ResponseObject;
import cl.shazkho.utils.keitaijisho.objects.custom.CustomActionBarActivity;

/**
 * *************************************
 * PROJECT: Keitai Jisho
 * MODULE:  ResultDetailActivity
 * Defined in -> cl.shazkho.utils.keitaijisho.resultdetails
 * ***************************************
 * It shows detailed information about selected kanji word (together and separated).
 * ***************************************
 *
 * @author George Shazkho
 * @version 0.6 - December 21, 2014
 */
public class ResultDetailActivity extends CustomActionBarActivity {


	// INSTANCE VARIABLES

    private ResponseObject mResponseObject;
    private ResponseObject mOrigin;
    private DetailsListAdapter mAdapter;


    // IMPLEMENTED HOOK METHODS

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//Base configuration
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_detail);

		// Initialize elements
		Toolbar mToolbar = (Toolbar) findViewById(R.id.result_details_toolbar);
        ListView mDetailsList = (ListView) findViewById(R.id.result_detail_list);


		// Set elements up
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		// Receiving intent
		Intent intent = getIntent();
        mResponseObject = intent.getParcelableExtra("result");
        mOrigin = intent.getParcelableExtra("origin");
        Log.i("Result Activity", "Intent received with data: " + mResponseObject.getmKey().toUpperCase() + ", " + mResponseObject.getmDatabase().toUpperCase());

        // Inflating
        mAdapter = new DetailsListAdapter(this, mResponseObject);
        mDetailsList.setAdapter(mAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_result_detail, menu);
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
			case android.R.id.home:
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				upIntent.putExtra("result", mOrigin);
				if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
					TaskStackBuilder.create(this)
						.addNextIntentWithParentStack(upIntent)
						.startActivities();
				} else {
					NavUtils.navigateUpTo(this, upIntent);
				}

				return true;
			case R.id.results_menu_about:
				Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
				ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
					getApplicationContext(),
					R.anim.abc_slide_in_top,
					R.anim.abc_slide_out_top
				);
				ActivityCompat.startActivity(this, intent, options.toBundle());
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}


	// CLASS SPECIFIC METHODS

    @Override
    public void requestCallback(ResponseObject response) {
        mAdapter.initializeJson(response.getmJsonResult());
        mAdapter.notifyDataSetChanged();
    }

}
