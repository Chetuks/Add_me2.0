package neutrinos.addme.ModelClass;

import java.io.Serializable;

public class SortedModelclass implements Serializable {
    private String sortedid;
    private String organizationid;
    private String appuniqueid;
    private String categoryname;
    private String image;
    private String orglatlong;
    private String organizationname;
    private boolean isFav;
    private boolean check;

    public String getSortedid() {
        return sortedid;
    }

    public void setSortedid(String sortedid) {
        this.sortedid = sortedid;
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

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrglatlong() {
        return orglatlong;
    }

    public void setOrglatlong(String orglatlong) {
        this.orglatlong = orglatlong;
    }

    public String getOrganizationname() {
        return organizationname;
    }

    public void setOrganizationname(String organizationname) {
        this.organizationname = organizationname;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
    /*  public SortedModelclass(String id, String organizationid, String suborganizationname, String image) {
        this.organizationid = organizationid;
        this.categoryname = suborganizationname;
        this.image = image;
        this.sortedid = id;
    }*/

    public SortedModelclass(String sortedid, String categoryname, String sortedImage,boolean flag,boolean check) {
        this.sortedid = sortedid;
        this.categoryname = categoryname;
        this.image = sortedImage;
        this.isFav=flag;
        this.check=check;
    }


}
