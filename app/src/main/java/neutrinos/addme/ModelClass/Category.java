
package neutrinos.addme.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Category implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("urls")
    @Expose
    private String urls;
    @SerializedName("appuniqueid")
    @Expose
    private String appuniqueid;
    @SerializedName("category_description")
    @Expose
    private String categoryDescription;
    @SerializedName("organizationId")
    @Expose
    private Integer organizationId;
    public final static Creator<Category> CREATOR = new Creator<Category>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return (new Category[size]);
        }

    }
    ;
    private final static long serialVersionUID = -7757708172444800550L;

    protected Category(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.createdDate = ((String) in.readValue((String.class.getClassLoader())));
        this.categoryName = ((String) in.readValue((String.class.getClassLoader())));
        this.urls = ((String) in.readValue((String.class.getClassLoader())));
        this.appuniqueid = ((String) in.readValue((String.class.getClassLoader())));
        this.categoryDescription = ((String) in.readValue((String.class.getClassLoader())));
        this.organizationId = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public Category() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getAppuniqueid() {
        return appuniqueid;
    }

    public void setAppuniqueid(String appuniqueid) {
        this.appuniqueid = appuniqueid;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(createdDate);
        dest.writeValue(categoryName);
        dest.writeValue(urls);
        dest.writeValue(appuniqueid);
        dest.writeValue(categoryDescription);
        dest.writeValue(organizationId);
    }

    public int describeContents() {
        return  0;
    }

}
