package cl.shazkho.utils.keitaijisho.objects.sqlite.model;


/**
 * SQLite helper class. Base class, extended by tuple-specific classes.
 *
 * @author George Shazkho
 * @version 0.3
 * @since 2014-12-11
 */
public class Tuple {


	// INSTANCE VARIABLES

	protected int ID;
	protected String mIndex;
	protected String mJson;
	protected String NAME;


	// CONSTRUCTOR

	public Tuple(String className, String tableName) {
		this.NAME = tableName;
	}


	// SETTERS AND GETTERS

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getmIndex() {
		return mIndex;
	}

	public void setmIndex(String mIndex) {
		this.mIndex = mIndex;
	}

	public String getmJson() {
		return mJson;
	}

	public void setmJson(String mJson) {
		this.mJson = mJson;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String NAME) {
		this.NAME = NAME;
	}
}
