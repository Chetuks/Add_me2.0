
package neutrinos.addme.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DetailsListCategory implements Serializable, Parcelable
{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;
    public final static Creator<DetailsListCategory> CREATOR = new Creator<DetailsListCategory>() {


        @SuppressWarnings({
            "unchecked"
        })
        public DetailsListCategory createFromParcel(Parcel in) {
            return new DetailsListCategory(in);
        }

        public DetailsListCategory[] newArray(int size) {
            return (new DetailsListCategory[size]);
        }

    }
    ;
    private final static long serialVersionUID = 8378598326299848150L;

    protected DetailsListCategory(Parcel in) {
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.categories, (Category.class.getClassLoader()));
    }

    public DetailsListCategory() {
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

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(message);
        dest.writeValue(status);
        dest.writeList(categories);
    }

    public int describeContents() {
        return  0;
    }

}
