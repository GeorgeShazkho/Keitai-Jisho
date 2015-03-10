package cl.shazkho.utils.keitaijisho.database;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import cl.shazkho.utils.keitaijisho.R;
import cl.shazkho.utils.keitaijisho.objects.ResponseObject;
import cl.shazkho.utils.keitaijisho.tools.StaticHelpers;

/**
 * General asynchronous task to retrieve JSON response from the web service.
 *
 * @author George Shazkho
 * @version 0.7
 * @since 2015-03-04
 */
public class JsonAsyncTask extends AsyncTask<Void, Integer, Object> implements StaticHelpers {


    // INSTANCE VARIABLES

    private DatabaseManager mParent;
    private ResponseObject mResponseObject;
    private ProgressDialog mProgressDialog;
    private String mResponseString;


    // CONSTRUCTOR

    public JsonAsyncTask(ResponseObject responseObject, DatabaseManager parent) {
        this.mParent = parent;
        this.mResponseString = EMPTY_RESPONSE;
        this.mResponseObject = responseObject;
        this.mProgressDialog = new ProgressDialog(mParent.getmParent());
    }


    // IMPLEMENTED HOOK METHODS FROM PARENT CLASS 'AsyncTask'

    @Override
    protected void onPreExecute() {
        this.mProgressDialog.setMessage(mParent.getmParent().getString(R.string.search_async_searching));
        this.mProgressDialog.show();
        Log.i("Json Async Task", "Call to Async Task executed from parent '"+mParent.getClass().toString()+"'");
    }

    @Override
    protected Object doInBackground(Void... params) {

        /*
        WEB SERVICE NAMES BY DATABASE HANDLER
        SEARCH:     search.php?     <QUERY>&<TYPE>&<MATCH>
        DETAILS:    details.php?    <QUERY>
         */

        String jsonQuery = "http://shazkho.cl/kj_webservices/";
        switch (mResponseObject.getmDatabase()) {
            case "search":
                jsonQuery += "search.php?";
                int separatorPosition = mResponseObject.getmKey().indexOf('+');
                String term = mResponseObject.getmKey().substring(0, separatorPosition);
                String type = mResponseObject.getmKey().substring(separatorPosition + 1);
                // ToDo Use regexp.
                jsonQuery += "query=" + term + "&" + "type="  + type + "&match=strict";
                break;
            case "details":
                jsonQuery += "details.php?query=" + mResponseObject.getmKey();
                break;
            default:
                return null;
        }
        mResponseString = makeServiceCall(jsonQuery);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        mResponseObject.setmJsonResult(mResponseString);
        mProgressDialog.dismiss();
        mParent.requestTaskCallback(mResponseObject);
    }


    // CLASS SPECIFIC METHODS

    /**
     * Calls for HTTP response. Used to get JSON response.
     *
     * @param jsonQuery Request URL
     * @return HTTP response, in JSON format.
     */
    private String makeServiceCall(String jsonQuery) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(jsonQuery);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder sBuilder = new StringBuilder();
            String line;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line).append("\n");
            }
            inputStream.close();
            return sBuilder.toString();
        } catch(Exception e) {
            return EMPTY_RESPONSE;
        }
    }
}
