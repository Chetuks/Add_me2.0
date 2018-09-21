package neutrinos.addme.ModelClass;

import java.io.Serializable;

public class ModelClassChecklist implements Serializable {
    String id;
    String name;
    Boolean checkstatus;
    String quantity;
    String quantitySpinser;

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    String modifiedDate;

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Boolean getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(Boolean checkstatus) {
        this.checkstatus = checkstatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModelClassChecklist(){

    }

    public ModelClassChecklist(String id, String name, Boolean checkstatus,String quantity, String quantitySpinser,String modifiedDate) {
        this.id = id;
        this.name = name;
        this.checkstatus = checkstatus;
        this.quantity=quantity;
        this.quantitySpinser=quantitySpinser;
        this.modifiedDate = modifiedDate;
    }

    public ModelClassChecklist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getQuantitySpinser() {
        return quantitySpinser;
    }

    public void setQuantitySpinser(String quantitySpinser) {
        this.quantitySpinser = quantitySpinser;
    }
}
