package cl.shazkho.utils.keitaijisho.results;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cl.shazkho.utils.keitaijisho.R;
import cl.shazkho.utils.keitaijisho.database.DatabaseManager;
import cl.shazkho.utils.keitaijisho.objects.ResponseObject;
import cl.shazkho.utils.keitaijisho.objects.custom.CustomActionBarActivity;
import cl.shazkho.utils.keitaijisho.tools.JapaneseWritingHelper;
import cl.shazkho.utils.keitaijisho.tools.StaticHelpers;


/**
 * Handles result list inflation.
 *
 * @author George Shazkho
 * @version 0.7
 * @since 2015-03-08
 */
public class ResultListAdapter extends BaseAdapter implements StaticHelpers {


	// INSTANCE VARIABLES

    private CustomActionBarActivity mParent;
    private JSONArray mHome;
    private boolean isJsonLoaded;


	// CONSTRUCTOR

	public ResultListAdapter(CustomActionBarActivity parent, ResponseObject response) {
        this.mParent = parent;
        this.isJsonLoaded = false;
        initializeJson(response.getmJsonResult());
    }


	// OVERRIDDEN METHODS FROM PARENT CLASS 'BaseAdapter'

	@Override
	public int getCount() {
		return mHome.length();
	}

	@Override
	public Object getItem(int position) {
        try {
            return mHome.getJSONObject(position);
        } catch (JSONException e) {
            Log.e("Search List Adapter", "Error getting JSONObject in position " + position + ".");
            Log.e("Search List Adapter", e.toString());
            e.printStackTrace();
            return null;
        }
    }

	@Override
	public long getItemId(int position) {
		return position;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if ( !isJsonLoaded ) {
            return null;
        }

        // View holder Pattern
        ViewHolder holder;
        if ( convertView == null ) {
            LayoutInflater inflater =
                    (LayoutInflater) mParent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.element_results, parent, false);
            holder = new ViewHolder();
            holder.kana_row = (TableRow) convertView.findViewById(R.id.element_result_kana);
            holder.roma_row = (TableRow) convertView.findViewById(R.id.element_result_romaji);
            holder.kanji_row = (LinearLayout) convertView.findViewById(R.id.element_result_kanji);
            holder.gloss_row = (LinearLayout) convertView.findViewById(R.id.element_result_gloss);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Data completion into list
        JSONObject result = (JSONObject) getItem(position);
        try {

            // KANA SECTION
            JSONArray kanaArray = result.getJSONArray("kana");
            holder.kana_row.removeAllViews();
            holder.roma_row.removeAllViews();
            for ( int i = 0; i < kanaArray.length(); ++i ) {
                String kana = kanaArray.getString(i);
                holder.kana_row.addView(createTextView( kana, KANA ));
                holder.roma_row.addView(createTextView( JapaneseWritingHelper.toRomaji( kana ), ROMAJI ));
            }

            // KANJI SECTION
            JSONArray kanjiArray = result.getJSONArray("kanji");
            holder.kanji_row.removeAllViews();
            for ( int i = 0; i < kanjiArray.length(); ++i ) {
                View kanjiView = createTextView(kanjiArray.getString(i), KANJI);
                kanjiView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleDetailRequest( ((TextView) v).getText().toString() );
                    }
                });
                holder.kanji_row.addView( kanjiView );
            }

            // GLOSS SECTION
            JSONArray glossArray = result.getJSONArray("gloss");
            holder.gloss_row.removeAllViews();
            int pos = 1;
            for ( int i = 0; i < glossArray.length(); ++i ) {
                LayoutInflater glossInflater =
                        (LayoutInflater) mParent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View glossView = glossInflater.inflate(R.layout.element_gloss, parent, false);
                TextView glossContent = (TextView) glossView.findViewById(R.id.element_gloss_content);
                glossContent.setText(glossArray.getString(i));
                holder.gloss_row.addView(glossView);
                ++pos;
            }

        } catch (JSONException e) {
            Log.e("Search List Adapter", "Error getting details from JSON in position " + position + ".");
            Log.e("Search List Adapter", e.toString());
            e.printStackTrace();
            return null;
        }

        return convertView;
    }


    // CLASS SPECIFIC METHODS

    public void initializeJson( String JSON ) {
        try {
            JSONObject jsonObj = new JSONObject( JSON );
            mHome = jsonObj.getJSONArray("response");
            Log.i("Search List Adapter","Successfully loaded JSON String into JSONArray Object.");
            Log.d("Search List Adapter", "INPUT JSON:\n"+JSON);
            isJsonLoaded = true;
        } catch (JSONException e) {
            Log.e("Search List Adapter", "Error loading JSON String into JSONArray Object.");
            Log.e("Search List Adapter", e.toString());
            e.printStackTrace();
            isJsonLoaded = false;
        }
    }


    /**
     * Handles a detail request from an 'onClick' event
     *
     * @param kanji The kanji String requesting for details.
     */
    private void handleDetailRequest(String kanji) {

        // Take away possible hiragana characters and separate per element.
        ArrayList<Character> requestList = new ArrayList<>();
        for ( int i = 0; i < kanji.length(); ++i ) {
            if ( JapaneseWritingHelper.isKanjiCharacter( kanji.charAt( i ) ) ) {
                requestList.add( kanji.charAt( i ) );
            }
        }

        // Do the query, baby
        DatabaseManager manager = new DatabaseManager(mParent);
        String key = "";
        for ( Character kanjiRequest : requestList ) {
            key += "_" + kanjiRequest;
        }
        ResponseObject request = new ResponseObject( "details", key.substring(1) );
        manager.query( request, false );
    }


    /**
     * Creates a TextView pre-configured to fit the app theme.
     *
     * @param label Label String for the View.
     * @param source Integer identification for certain View types.
     * @return The The formatted TextView.
     */
    private TextView createTextView(String label, int source) {
        TextView newTV = new TextView(mParent);
        int size = 24;
        newTV.setGravity(Gravity.LEFT);
        int color = mParent.getResources().getColor(R.color.AppTheme_text_base);
        if(source == ROMAJI) {
            color = mParent.getResources().getColor(R.color.AppTheme_accentColor);
            newTV.setGravity(Gravity.CENTER_HORIZONTAL);
            size = 12;
        } else if(source == KANJI) {
            size = 28;
        }
        newTV.setTextAppearance(mParent, R.style.KanjiElement);
        newTV.setText(label);
        newTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        newTV.setTextColor(color);
        //newTV.setPadding((int)dpToPx(8, mParent), (int)dpToPx(4, mParent), (int)dpToPx(8, mParent), (int)dpToPx(4, mParent));
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.rightMargin = (int)dpToPx(16, mParent);
        newTV.setLayoutParams(params);
        return newTV;
    }

    private float dpToPx(int dp, Context ctx){
        Resources r = ctx.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }


	// VIEWHOLDER STATIC CLASS

	/**
	 * ViewHolder used to implement Viewholder Pattern.
	 */
	private class ViewHolder {
        TableRow kana_row;
        TableRow roma_row;
        LinearLayout kanji_row;
        LinearLayout gloss_row;
	}

}
