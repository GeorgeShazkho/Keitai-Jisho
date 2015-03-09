package cl.shazkho.utils.keitaijisho.resultdetails;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.shazkho.utils.keitaijisho.R;
import cl.shazkho.utils.keitaijisho.objects.ResponseObject;
import cl.shazkho.utils.keitaijisho.objects.custom.CustomActionBarActivity;
import cl.shazkho.utils.keitaijisho.tools.StaticHelpers;

/**
 * *************************************
 * PROJECT: KEITAI JISHO
 * MODULE:  DetailsListAdapter
 * Defined in -> cl.shazkho.utils.keitaijisho.resultdetails
 * ***************************************
 * Handles details list inflation.
 * ***************************************
 *
 * @author George Shazkho
 * @version 0.7 - March 08, 2015
 */
public class DetailsListAdapter extends BaseAdapter implements StaticHelpers {


    // INSTANCE VARIABLES

    private CustomActionBarActivity mParent;
    private JSONArray mHome;
    private boolean isJsonLoaded;
    private Drawable mDrawable;


    // CONSTRUCTOR

    public DetailsListAdapter(CustomActionBarActivity parent, ResponseObject response) {
        this.mParent = parent;
        this.isJsonLoaded = false;
        mDrawable = mParent.getResources().getDrawable(R.drawable.ic_launcher);
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
            convertView = inflater.inflate(R.layout.detail_kanji, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.details_kanji_kanji_text);
            holder.kun = (TextView) convertView.findViewById(R.id.detail_kanji_readings_kun);
            holder.on = (TextView) convertView.findViewById(R.id.detail_kanji_readings_on);
            holder.strokes = (TextView) convertView.findViewById(R.id.detail_kanji_other_strokecount);
            holder.freq = (TextView) convertView.findViewById(R.id.detail_kanji_other_freq);
            holder.skip = (TextView) convertView.findViewById(R.id.detail_kanji_other_skip);
            holder.turtle = (TextView) convertView.findViewById(R.id.detail_kanji_other_shdesc);
            holder.four = (TextView) convertView.findViewById(R.id.detail_kanji_other_fourcorner);
            holder.deroo = (TextView) convertView.findViewById(R.id.detail_kanji_other_daroo);
            holder.meanings = (LinearLayout) convertView.findViewById(R.id.detail_kanji_meanings);
            holder.svg = (ImageView) convertView.findViewById(R.id.details_kanji_strokeOrder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Data completion into list
        JSONObject result = (JSONObject) getItem(position);

        try {
            SVG svg = SVGParser.getSVGFromString( unescapeJson( result.getString( "svg" ) ) );
            mDrawable = svg.createPictureDrawable();
        } catch(Exception e) {
            Log.e("Details List SVG", e.toString());
        }
        String meanings = "";
        holder.svg.setImageDrawable( mDrawable );
        try {
            holder.title.setText( result.getString("kanji") );
            holder.kun.setText( result.getString("kun") );
            holder.on.setText( result.getString("on") );
            holder.strokes.setText( result.getString("stroke_count") );
            holder.freq.setText( result.getString("freq") + "/2500" );
            holder.skip.setText( result.getString("skip") );
            holder.turtle.setText( result.getString("sh_desc") );
            holder.four.setText( result.getString("four_corner") );
            holder.deroo.setText( result.getString("deroo") );
            meanings = result.getString( "meanings" );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] meanings_list = meanings.split("&&");
        holder.meanings.removeAllViews();
        for(String meaning: meanings_list) {
            TextView newTV = new TextView(mParent);
            newTV.setText(meaning);
            newTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            newTV.setTextColor(mParent.getResources().getColor(R.color.AppTheme_text_secondary_icons));
            holder.meanings.addView(newTV);
        }

        return convertView;
    }


    // CLASS SPECIFIC METHODS

    private String unescapeJson(String JSON) {
        String[] escape =  {"\\",   	"/", 	"\"", 	"\n", 	"\r", 	"\t",   "'"};
        String[] replace = {"\\\\", 	"\\/", 	"\\\"", "\\n", 	"\\r", 	"\\t",  "\\'"};
        for ( int i = 0; i < escape.length; ++i ) {
            JSON = JSON.replace(replace[i], escape[i]);
        }
        return JSON;
    }


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



    // VIEWHOLDER STATIC CLASS

    /**
     * ViewHolder used to implement Viewholder Pattern.
     */
    private class ViewHolder {
        TextView title;
        TextView kun;
        TextView on;
        TextView strokes;
        TextView freq;
        TextView skip;
        TextView turtle;
        TextView four;
        TextView deroo;
        LinearLayout meanings;
        ImageView svg;
    }

}
