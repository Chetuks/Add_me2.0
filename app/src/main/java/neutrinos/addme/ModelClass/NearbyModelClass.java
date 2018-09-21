package neutrinos.addme.ModelClass;

import java.io.Serializable;

public class NearbyModelClass implements Serializable {
    int fileid;
    String organizationimage;
    String organizationname;
    String validity;
    String contentImage;
    String filename;
    int organizationID;
    boolean status;
    boolean wishliststatus;
    String productdescription;
    String offercode;
    String latlong;
    String address;

    public NearbyModelClass(String organizationimage, String organizationname, int organizationID, String latlong, String address) {
        this.organizationimage = organizationimage;
        this.organizationname = organizationname;
        this.organizationID = organizationID;
        this.latlong = latlong;
        this.address = address;
    }

    public NearbyModelClass(int fileid, String organizationimage, String organizationname, String validity,
                            String contentImage, String filename, int organizationID, boolean status,
                            boolean wishliststatus, String productdescription, String latlong, String offercode) {
        this.fileid = fileid;
        this.organizationimage = organizationimage;
        this.organizationname = organizationname;
        this.validity = validity;
        this.contentImage = contentImage;
        this.filename = filename;
        this.organizationID = organizationID;
        this.status = status;
        this.wishliststatus = wishliststatus;
        this.productdescription = productdescription;
        this.latlong = latlong;
        this.offercode = offercode;
    }

    public boolean isStatus() {
        return status;
    }

    public String getOffercode() {
        return offercode;
    }

    public void setOffercode(String offercode) {
        this.offercode = offercode;
    }
    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public void setProductdescription(String productdescription) {
        this.productdescription = productdescription;
    }

    public boolean isWishliststatus() {
        return wishliststatus;
    }

    public void setWishliststatus(boolean wishliststatus) {
        this.wishliststatus = wishliststatus;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getFileid() {
        return fileid;
    }

    public void setFileid(int fileid) {
        this.fileid = fileid;
    }

    public String getOrganizationimage() {
        return organizationimage;
    }

    public void setOrganizationimage(String organizationimage) {
        this.organizationimage = organizationimage;
    }

    public String getOrganizationname() {
        return organizationname;
    }

    public void setOrganizationname(String organizationname) {
        this.organizationname = organizationname;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationID(int organizationID) {
        this.organizationID = organizationID;
    }
}
