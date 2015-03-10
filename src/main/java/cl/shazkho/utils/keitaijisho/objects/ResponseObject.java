package cl.shazkho.utils.keitaijisho.objects;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Custom object. Standard database query result.
 *
 * @author George Shazkho
 * @version 0.7
 * @since 2015-03-03
 */
public class ResponseObject implements Parcelable {


    // INSTANCE VARIABLES

    private String mKey;
    private String mDatabase;
    private String mJsonResult;

    public static final Parcelable.Creator<ResponseObject> CREATOR
            = new Parcelable.Creator<ResponseObject>() {
        public ResponseObject createFromParcel(Parcel in) {
            return new ResponseObject(in);
        }
        public ResponseObject[] newArray(int size) {
            return new ResponseObject[size];
        }
    };


    // CONSTRUCTORS

    public ResponseObject(String mDatabase, String mKey) {
        this.mDatabase = mDatabase;
        this.mKey = mKey;
    }

    public ResponseObject(Parcel in) {
        this.readFromParcel(in);
    }


    // CLASS SPECIFIC METHODS

    /**
     * Reads data from input Parcel. Works as part of the 'Parcelable' driven constructor.
     *
     * @param in The parcel receiver in the constructor.
     */
    private void readFromParcel(Parcel in) {
        mKey = in.readString();
        mDatabase = in.readString();
        mJsonResult = in.readString();
    }



    // SETTERS AND GETTERS


    public String getmKey() {
        return mKey;
    }

    public String getmDatabase() {
        return mDatabase;
    }

    public String getmJsonResult() {
        return mJsonResult;
    }

    public void setmJsonResult(String mJsonResult) {
        this.mJsonResult = mJsonResult;
    }


    // OVERRIDDEN METHODS FROM PARENT CLASS 'Parcelable'

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mKey);
        dest.writeString(mDatabase);
        dest.writeString(mJsonResult);
    }
}
