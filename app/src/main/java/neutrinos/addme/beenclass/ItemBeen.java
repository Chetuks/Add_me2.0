package neutrinos.addme.beenclass;

/**
 * Created by mahiti on 8/3/18.
 */

public class ItemBeen {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmmount() {
        return ammount;
    }

    public void setAmmount(String ammount) {
        this.ammount = ammount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String status;
    private String ammount;
    private String date;

    public ItemBeen(String status,String ammount,String date)
    {

        this.status = status;
        this.ammount = ammount;
        this.date = date;
    }



}
