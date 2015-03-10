package cl.shazkho.utils.keitaijisho.objects.sqlite.model;

/**
 * SQLite helper class. Abstracts behaviour of "Details" table in the database.
 *
 * @author George Shazkho
 * @version 0.3
 * @since 2014-12-11
 */
public class DetailsTuple extends Tuple {


	// CONSTRUCTORS

	public DetailsTuple() {
		super("DetailsTuple", "details");
	}

	public DetailsTuple(String mIndex, String mJson) {
		super("DetailsTuple", "details");
		this.mIndex = mIndex;
		this.mJson = mJson;
	}

	public DetailsTuple(int ID, String mIndex, String mJson) {
		super("DetailsTuple", "details");
		this.ID = ID;
		this.mIndex = mIndex;
		this.mJson = mJson;
	}

}
