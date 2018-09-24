package neutrinos.addme.ModelClass;

import android.content.Intent;

import java.io.Serializable;

/**
 * Created by NS_USER on 21-Mar-18.
 */

public class CategoryModelClass implements Serializable {
    String id;
    String categoryname;
    String image;
    String description;
    String date;
    String organizationid;
    String appuniqueid;
    int hotwordid;
    String hotwordname;

    public int getHotwordid() {
        return hotwordid;
    }

    public void setHotwordid(int hotwordid) {
        this.hotwordid = hotwordid;
    }

    public String getHotwordname() {
        return hotwordname;
    }

    public void setHotwordname(String hotwordname) {
        this.hotwordname = hotwordname;
    }

    public CategoryModelClass() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizationid() {
        return organizationid;
    }

    public void setOrganizationid(String organizationid) {
        this.organizationid = organizationid;
    }

    public String getAppuniqueid() {
        return appuniqueid;
    }

    public void setAppuniqueid(String appuniqueid) {
        this.appuniqueid = appuniqueid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return categoryname;
    }

    public void setName(String name) {
        this.categoryname = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public CategoryModelClass(String id,String categoryname , String image) {
        this.id = id;
        this.categoryname = categoryname;
        this.image = image;
    }

    public CategoryModelClass(String id, String name, String image, String description, String date,String appuniqueid,String organizationid) {
        this.id = id;
        this.categoryname = name;
        this.image = image;
        this.date = date;
        this.description = description;
        this.appuniqueid=appuniqueid;
        this.organizationid=organizationid;
    }

    public CategoryModelClass(int hotwordid, String hotwordname) {
        this.hotwordid = hotwordid;
        this.hotwordname = hotwordname;
    }

    //myCategoryModelclass = new CategoryModelClass(id, "name", "image", "description", "date");


}
