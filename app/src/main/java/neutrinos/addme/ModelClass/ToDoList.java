package neutrinos.addme.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NS_USER on 10-Mar-18.
 */

public class ToDoList implements Parcelable {

    String UUID;
    String itemText;
    String itemQTy;
    String modifiedDate;
    String status;

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Creator<ToDoList> getCREATOR() {
        return CREATOR;
    }

    protected ToDoList(Parcel in) {
        UUID = in.readString();
        itemText = in.readString();
        itemQTy = in.readString();
        modifiedDate = in.readString();
    }

    public ToDoList(String UUID, String itemText, String itemQTy, String modifiedDate, String status) {
        this.UUID = UUID;
        this.itemText = itemText;
        this.itemQTy = itemQTy;
        this.modifiedDate = modifiedDate;
        this.status = status;
    }

    public ToDoList() {
    }

    public static final Creator<ToDoList> CREATOR = new Creator<ToDoList>() {
        @Override
        public ToDoList createFromParcel(Parcel in) {
            return new ToDoList(in);
        }

        @Override
        public ToDoList[] newArray(int size) {
            return new ToDoList[size];
        }
    };

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getitemText() {
        return itemText;
    }

    public void setitemText(String itemText) {
        this.itemText = itemText;
    }

    public String getItemQTy() {
        return itemQTy;
    }

    public void setItemQTy(String itemQTy) {
        this.itemQTy = itemQTy;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UUID);
        dest.writeString(itemText);
        dest.writeString(itemQTy);
        dest.writeString(modifiedDate);
    }
}
