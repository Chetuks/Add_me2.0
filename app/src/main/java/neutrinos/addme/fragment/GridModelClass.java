package neutrinos.addme.fragment;

import java.io.Serializable;

public class GridModelClass implements Serializable {
    int fileid;
    String fileName;
    String organizationname;
    String organizationimage;
    int organizationid;
    String ImageUrl;
    boolean favstatus;
    boolean wishstatus;
    String product_description;
    String offercode;
    String validity;

    public GridModelClass(String fileName, String imageUrl, boolean favstatus) {
        this.fileName = fileName;
        this.ImageUrl = imageUrl;
        this.favstatus=favstatus;
    }

    public GridModelClass(int fileid, String fileName, String organizationname, String organizationimage,
                          int organizationid, String imageUrl, boolean favstatus, boolean wishstatus,
                          String product_description, String offercode,String validity) {
        this.fileid = fileid;
        this.fileName = fileName;
        this.organizationname = organizationname;
        this.organizationimage = organizationimage;
        this.organizationid = organizationid;
        ImageUrl = imageUrl;
        this.favstatus = favstatus;
        this.wishstatus = wishstatus;
        this.product_description=product_description;
        this.offercode=offercode;
        this.validity=validity;
    }
    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
    public String getOffercode() {
        return offercode;
    }

    public void setOffercode(String offercode) {
        this.offercode = offercode;
    }
    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public int getFileid() {
        return fileid;
    }

    public void setFileid(int fileid) {
        this.fileid = fileid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOrganizationname() {
        return organizationname;
    }

    public void setOrganizationname(String organizationname) {
        this.organizationname = organizationname;
    }

    public String getOrganizationimage() {
        return organizationimage;
    }

    public void setOrganizationimage(String organizationimage) {
        this.organizationimage = organizationimage;
    }

    public int getOrganizationid() {
        return organizationid;
    }

    public void setOrganizationid(int organizationid) {
        this.organizationid = organizationid;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public boolean isFavstatus() {
        return favstatus;
    }

    public void setFavstatus(boolean favstatus) {
        this.favstatus = favstatus;
    }

    public boolean isWishstatus() {
        return wishstatus;
    }

    public void setWishstatus(boolean wishstatus) {
        this.wishstatus = wishstatus;
    }
}
