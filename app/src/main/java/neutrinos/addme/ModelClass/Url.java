
package neutrinos.addme.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Url implements Serializable, Parcelable
{

    @SerializedName("latlong")
    @Expose
    private String latlong;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("filetype")
    @Expose
    private String filetype;
    @SerializedName("url")
    @Expose
    private String url;
    public final static Creator<Url> CREATOR = new Creator<Url>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Url createFromParcel(Parcel in) {
            return new Url(in);
        }

        public Url[] newArray(int size) {
            return (new Url[size]);
        }

    }
    ;
    private final static long serialVersionUID = 3215795477093153109L;

    protected Url(Parcel in) {
        this.latlong = ((String) in.readValue((String.class.getClassLoader())));
        this.filename = ((String) in.readValue((String.class.getClassLoader())));
        this.filetype = ((String) in.readValue((String.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Url() {
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(latlong);
        dest.writeValue(filename);
        dest.writeValue(filetype);
        dest.writeValue(url);
    }

    public int describeContents() {
        return  0;
    }

}
