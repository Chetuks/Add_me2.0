package neutrinos.addme.beenclass;

/**
 * Created by Avinash raj on 10/11/17.
 */

public class UrlsBeen {
    String filetype;
    String url;
    public int viewCount;
    String imagetype;

    public UrlsBeen(String filetype, String url,int viewCount, String imagetype)
    {
        this.filetype = filetype;
        this.url = url;
        this.viewCount=viewCount;
        this.imagetype=imagetype;
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

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getImagetype() {
        return imagetype;
    }

    public void setImagetype(String imagetype) {
        this.imagetype = imagetype;
    }
}
