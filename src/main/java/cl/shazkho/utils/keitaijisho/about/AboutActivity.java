package cl.shazkho.utils.keitaijisho.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
 * @version 1.0 alpha 150317
 * @since 2014-12-18
 */
public class AboutActivity extends CustomActionBarActivity implements View.OnClickListener {


	// OVERRIDDEN HOOK METHODS

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView email_card = (CardView) findViewById(R.id.about_card_email);
        CardView web_card = (CardView) findViewById(R.id.about_card_web);
        email_card.setOnClickListener(this);
        web_card.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.about_card_email:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "shazkho@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact from jisho app");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, ""));
                break;
            case R.id.about_card_web:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://keitaijisho.shazkho.cl"));
                startActivity(browserIntent);
                break;
            default:
                break;
        }
    }
}
