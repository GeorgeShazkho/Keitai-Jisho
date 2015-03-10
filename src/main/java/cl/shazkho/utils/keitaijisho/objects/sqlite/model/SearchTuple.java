package cl.shazkho.utils.keitaijisho.objects.sqlite.model;

/**
 * SQLite helper class. Abstracts behaviour of "Search" table in the database.
 *
 * @author George Shazkho
 * @version 0.3
 * @since 2014-12-11
 */
public class SearchTuple extends Tuple {


	// CONSTRUCTORS

	public SearchTuple() {
		super("SearchTuple", "search");
	}

	public SearchTuple(String mIndex, String json) {
		super("SearchTuple", "search");
		this.mIndex = mIndex;
		this.mJson = json;
	}

	public SearchTuple(int id, String mIndex, String json) {
		super("SearchTuple", "search");
		this.ID = id;
		this.mIndex = mIndex;
		this.mJson = json;
	}

}
