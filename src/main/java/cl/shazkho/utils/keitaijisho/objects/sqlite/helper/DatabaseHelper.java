package cl.shazkho.utils.keitaijisho.objects.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cl.shazkho.utils.keitaijisho.objects.sqlite.model.Tuple;

/**
 * SQLite helper class. Main class to b called when Database interactions are needed.
 *
 * @author George Shazkho
 * @version 0.7
 * @since 2015-03-09
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	// INSTANCE VARIABLES

	// DATABASE SETUP
	static private final String DATABASE_NAME = "userSettings";
	static private final int DATABASE_VERSION = 18;

	// TABLE NAMES
	static private final String TABLE_SEARCH = "search";
	static private final String TABLE_DETAILS = "details";

	// TABLE COLUMNS
	static private final String KEY_ID = "ID";
	static private final String KEY_JSON = "mJson";
	static private final String KEY_INDEX = "mIndex";

	// TABLE CREATION
	static private final String CREATE_TABLE_SEARCH =
		"CREATE TABLE "	+ TABLE_SEARCH + "(" +
			KEY_ID      + " INTEGER PRIMARY KEY," +
			KEY_INDEX   + " TEXT," +
			KEY_JSON    + " TEXT)";
	static private final String CREATE_TABLE_DETAILS =
		"CREATE TABLE "	+ TABLE_DETAILS + "(" +
			KEY_ID      + " INTEGER PRIMARY KEY," +
			KEY_INDEX   + " TEXT," +
			KEY_JSON    + " TEXT)";


	// CONSTRUCTOR

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	// IMPLEMENTED METHODS FROM PARENT CLASS 'SQLiteOpenHelper'

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(CREATE_TABLE_SEARCH);
		db.execSQL(CREATE_TABLE_DETAILS);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILS);
		db.execSQL("DROP TABLE IF EXISTS " + "svg");        /* Temporal deletion for old system support */
		onCreate(db);

	}


	// CUSTOM CLASS METHODS

	/**
	 * Creates a single Tuple in the database.
	 *
	 * @param tuple Tuple abstraction as input
	 * @return ID of the requested insertion.
	 */
	public long createTuple(Tuple tuple) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_INDEX, tuple.getmIndex());
		values.put(KEY_JSON, tuple.getmJson());
		long ID = db.insert(tuple.getNAME(), null, values);
		db.close();
		return ID;
	}

	/**
	 * Gets a single Tuple, from the database, that matches provided criteria.
	 *
	 * @param index The index the tuple is store under (not ID).
	 * @param tableName The name of the table we are looking into.
	 * @return a Tuple abstraction of the result.
	 */
	public Tuple getTuple(String index, String tableName) {
		SQLiteDatabase db = this.getReadableDatabase();

		try {
			String selectQuery = "SELECT * FROM " + tableName + " WHERE "
				+ KEY_INDEX + " = '" + index + "'";
			Cursor c = db.rawQuery(selectQuery, null);
			Tuple tuple;
			if (c != null && c.getCount() != 0) {
				tuple = new Tuple(tableName, tableName);
				c.moveToFirst();
				tuple.setID(c.getInt(c.getColumnIndex(KEY_ID)));
				tuple.setmIndex(c.getString(c.getColumnIndex(KEY_INDEX)));
				tuple.setmJson(c.getString(c.getColumnIndex(KEY_JSON)));
				db.close();
                c.close();
				return tuple;
			}
		} catch(Exception e) {
			Log.wtf("Tuple retriever", e.toString());
		}
		db.close();
		return null;
	}

	/**
	 * Gets all Tuple found in the provided table.
	 *
	 * @param tableName The name of the table we are looking into.
	 * @return A List with every Tuple in that table.
	 */
	public List<Tuple> getAllTuples(String tableName) {
		List<Tuple> tuples = new ArrayList<>();
		String selectQuery = "SELECT  * FROM " + tableName;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Tuple tp = new Tuple(tableName,tableName);
				tp.setID(c.getInt((c.getColumnIndex(KEY_ID))));
				tp.setmIndex((c.getString(c.getColumnIndex(KEY_INDEX))));
				tp.setmJson(c.getString(c.getColumnIndex(KEY_JSON)));
				tuples.add(tp);
			} while (c.moveToNext());
		}
        c.close();
		return tuples;
	}

	/**
	 * Updates a table entry based on the provided input information.
	 *
	 * @param tuple Tuple abstraction of the desired object result.
	 * @return The ID of the affected tuple in the database.
	 */
	public int updateTuple(Tuple tuple) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_INDEX, tuple.getmIndex());
		values.put(KEY_JSON, tuple.getmJson());

		// updating row
		return db.update(tuple.getNAME(), values, KEY_ID + " = ?",
			new String[] { String.valueOf(tuple.getID()) });
	}

	/**
	 * Deletes a single tuple in the database if matches provided criteria
	 *
	 * @param index The index the tuple is store under (not ID).
	 * @param tableName The name of the table we are looking into.
	 * @return True if the tuple was successfully deleted. False otherwise.
	 */
	public boolean deleteTuple(String index, String tableName) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(tableName, KEY_INDEX + " = ?",
				new String[] {String.valueOf(index)});
			db.close();
			return true;
		} catch (Exception e) {
			Log.e("Tuple deleter","Couldn't erase tuple " + index + " in table " + tableName);
			return false;
		}
	}

}
