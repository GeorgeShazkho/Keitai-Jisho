package cl.shazkho.utils.keitaijisho.about;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import cl.shazkho.utils.keitaijisho.R;
import cl.shazkho.utils.keitaijisho.objects.custom.CustomActionBarActivity;


/**
 * Application's information Activity.
 *
 * <p>Keitai Jisho Activities works using a simple abstraction
 * of the Activity Class, more precisely, it uses the
 * ActionBarActivity Class to have compatibility with
 * the new Toolbar, but with older versions of Android.</p>
 *
 * @author George Shazkho
 * @version 0.5b
 * @since 2014-12-18
 */
public class AboutActivity extends CustomActionBarActivity {


	// OVERRIDDEN HOOK METHODS

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		TextView description = (TextView) findViewById(R.id.about_description_value);
		description.setText(Html.fromHtml(getResources().getString(R.string.about_description_text_html)));
		WebView webView = (WebView) findViewById(R.id.about_description_webView);
		webView.loadData(
			getString(R.string.about_description_text_html),
			"text/html; charset=UTF-8",
			null
		);
		webView.setBackgroundColor(getResources().getColor(R.color.transparent));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
			case android.R.id.home:
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
