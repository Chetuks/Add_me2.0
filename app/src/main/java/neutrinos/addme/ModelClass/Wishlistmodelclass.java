package neutrinos.addme.ModelClass;

import java.io.Serializable;

public class Wishlistmodelclass implements Serializable {
    String filename;
    String imageurl;
    int favid;
    String favlocation;
    String favOrgName;
    String favOrgImage;
    String address;

    public Wishlistmodelclass(int favid, String favlocation, String favOrgName, String favOrgImage, String address) {
        this.favid = favid;
        this.favlocation = favlocation;
        this.favOrgName = favOrgName;
        this.favOrgImage = favOrgImage;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getFavid() {

        return favid;
    }

    public void setFavid(int favid) {
        this.favid = favid;
    }

    public String getFavlocation() {
        return favlocation;
    }

    public void setFavlocation(String favlocation) {
        this.favlocation = favlocation;
    }

    public String getFavOrgName() {
        return favOrgName;
    }

    public void setFavOrgName(String favOrgName) {
        this.favOrgName = favOrgName;
    }

    public String getFavOrgImage() {
        return favOrgImage;
    }

    public void setFavOrgImage(String favOrgImage) {
        this.favOrgImage = favOrgImage;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public Wishlistmodelclass(String filename, String imageurl) {
        this.filename = filename;
        this.imageurl = imageurl;
    }
}
