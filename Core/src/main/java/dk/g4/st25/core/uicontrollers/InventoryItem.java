package dk.g4.st25.core.uicontrollers;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class InventoryItem {
    private final SimpleStringProperty itemType;
    private final SimpleStringProperty itemID;
    private final SimpleIntegerProperty amount;

    public InventoryItem(String itemType, String itemID, int amount){
        this.itemType = new SimpleStringProperty(itemType);
        this.itemID = new SimpleStringProperty(itemID);
        this.amount = new SimpleIntegerProperty(amount);
    }

    // Getter and setter for type
    public String getItemType(){
        return itemType.get();
    }
    public void setItemType(String itemType) {
        this.itemType.set(itemType);
    }

    // Getter and setter for ID
    public String getItemID() {
        return itemID.get();
    }
    public void setItemID(String itemID) {
        this.itemID.set(itemID);
    }

    // Getter and setter for amount
    public int getAmount() {
        return amount.get();
    }
    public void setAmount(int amount) {
        this.amount.set(amount);
    }

}

