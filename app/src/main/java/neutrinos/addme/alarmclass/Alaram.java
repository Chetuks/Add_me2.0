package neutrinos.addme.alarmclass;

/**
 * Created by guru on 10/11/17.
 */

public class Alaram {
    String filetype;
    String url;

    public Alaram() {
    }

    public Alaram(String filetype, String url) {
        this.filetype = filetype;
        this.url = url;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return filetype;
    }


}