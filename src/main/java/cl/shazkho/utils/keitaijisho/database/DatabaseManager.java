package cl.shazkho.utils.keitaijisho.database;

import android.util.Log;

import cl.shazkho.utils.keitaijisho.objects.ResponseObject;
import cl.shazkho.utils.keitaijisho.objects.custom.CustomActionBarActivity;
import cl.shazkho.utils.keitaijisho.objects.sqlite.helper.DatabaseHelper;
import cl.shazkho.utils.keitaijisho.objects.sqlite.model.DetailsTuple;
import cl.shazkho.utils.keitaijisho.objects.sqlite.model.SearchTuple;
import cl.shazkho.utils.keitaijisho.objects.sqlite.model.Tuple;
import cl.shazkho.utils.keitaijisho.tools.StaticHelpers;

/**
 * Controls database requests flux.
 *
 * @author George Shazkho
 * @version 0.7
 * @since 2015-03-04
 */
public class DatabaseManager implements StaticHelpers {


    // INSTANCE VARIABLES

    private CustomActionBarActivity mParent;


    // CONSTRUCTOR

    public DatabaseManager(CustomActionBarActivity mParent) {
        this.mParent = mParent;
    }


    // CLASS SPECIFIC METHODS

    /**
     * Standard query to the database group.
     *
     * @param responseObject Input object with the desired query information.
     * @param refreshLocal Determines whether do a normal short-distanced query or a forced web query.
     */
    public void query(ResponseObject responseObject, boolean refreshLocal) {
        Log.i("Database Manager", "Call to 'query' function with data: "+responseObject.getmKey().toUpperCase()+", "+responseObject.getmDatabase().toUpperCase());

        // Cleaning previous stored data on the key-database combination if requested.
        if(refreshLocal) {
            this.removeFromLocal(responseObject.getmKey(), responseObject.getmDatabase());
        }

        // Retrieving information from local database.
        String localResult = this.localQuery(responseObject.getmKey(), responseObject.getmDatabase());
        if(localResult.length() > 0) {
            responseObject.setmJsonResult(localResult);
            mParent.requestCallback(responseObject);

        // If not present, perform a web query and then localize the result.
        } else {
            this.webQuery(responseObject);
        }

    }

    /**
     * Responses to task conclusion callback.
     *
     * @param response The response String of the performed request.
     */
    public void requestTaskCallback (ResponseObject response) {
        this.addToLocal(response.getmKey(), response.getmDatabase(), response.getmJsonResult());
        mParent.requestCallback(response);
    }



    /**
     * Adds the newly found result on a web query into the local database.
     *
     * @param key Key queried.
     * @param database Database queried.
     * @param webResult Result obtained, in JSON.
     */
    private void addToLocal(String key, String database, String webResult) {
        DatabaseHelper helper = new DatabaseHelper(this.mParent);
        Tuple ST;
        switch (database) {
            case "search":
                ST = new SearchTuple(key, webResult);
                break;
            case "details":
                ST = new DetailsTuple(key, webResult);
                break;
            default:
                return;
        }
        helper.createTuple(ST);
        helper.close();
    }


    /**
     * Calls to AsyncTask for a WEB query.
     *
     * @param responseObject Query object.
     */
    private void webQuery(ResponseObject responseObject) {
        JsonAsyncTask requestTask = new JsonAsyncTask(responseObject, this);
        requestTask.execute();
    }

    /**
     * Performs a local query to the SQLite service bundled.
     *
     * @param key Key to be consulted.
     * @param database Database from which retrieve information.
     * @return A String array with the response.
     */
    private String localQuery(String key, String database) {
        DatabaseHelper helper = new DatabaseHelper(this.mParent);
        Tuple t = helper.getTuple(key,database);
        if(t == null) {
            return "";
        } else {
            return t.getmJson();
        }
    }

    /**
     * Deletes an entry from the local database, returning the deletion status.
     *
     * @param key Key to be erased.
     * @param database Database from which delete.
     * @return True if deletion ends successfully. False otherwise.
     */
    private boolean removeFromLocal(String key, String database) {
        DatabaseHelper helper = new DatabaseHelper(this.mParent);
        boolean deletionResponse = helper.deleteTuple(key, database);
        helper.close();
        return deletionResponse;
    }


    // SETTER AND GETTERS

    public CustomActionBarActivity getmParent() {
        return mParent;
    }
}
