
package neutrinos.addme.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NavigationDetails implements Serializable, Parcelable
{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("organizationName")
    @Expose
    private String organizationName;
    @SerializedName("urls")
    @Expose
    private List<Url> urls = null;
    public final static Creator<NavigationDetails> CREATOR = new Creator<NavigationDetails>() {


        @SuppressWarnings({
            "unchecked"
        })
        public NavigationDetails createFromParcel(Parcel in) {
            return new NavigationDetails(in);
        }

        public NavigationDetails[] newArray(int size) {
            return (new NavigationDetails[size]);
        }

    }
    ;
    private final static long serialVersionUID = 3317244446728354935L;

    protected NavigationDetails(Parcel in) {
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        this.organizationName = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.urls, (Url.class.getClassLoader()));
    }

    public NavigationDetails() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public void setUrls(List<Url> urls) {
        this.urls = urls;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(message);
        dest.writeValue(status);
        dest.writeValue(address);
        dest.writeValue(organizationName);
        dest.writeList(urls);
    }

    public int describeContents() {
        return  0;
    }

}
