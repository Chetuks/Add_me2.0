package neutrinos.addme.fragment;

import java.io.Serializable;

public class PremiumModelClass implements Serializable {
    String fileName;
    String fileType;
    String ImageUrl;

    public PremiumModelClass(String fileName, String fileType, String imageUrl) {
        this.fileName = fileName;
        this.fileType = fileType;
        ImageUrl = imageUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }


}
