package neutrinos.addme.ModelClass;

import java.io.Serializable;

public class HotSearchModelclass implements Serializable {
    int fileid;
    String filetype;
    String imageurl;

    public HotSearchModelclass(int fileid, String filetype, String imageurl) {
        this.fileid = fileid;
        this.filetype = filetype;
        this.imageurl = imageurl;
    }

    public int getFileid() {
        return fileid;
    }

    public void setFileid(int fileid) {
        this.fileid = fileid;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
