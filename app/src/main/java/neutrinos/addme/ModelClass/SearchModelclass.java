package neutrinos.addme.ModelClass;

import java.io.Serializable;

public class SearchModelclass implements Serializable{
    int fileid;
    String filename;
    String imageurl;

    public SearchModelclass(int fileid, String filename, String imageurl) {
        this.fileid = fileid;
        this.filename = filename;
        this.imageurl = imageurl;
    }

    public int getFileid() {

        return fileid;
    }

    public void setFileid(int fileid) {
        this.fileid = fileid;
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
}
