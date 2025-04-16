package dk.g4.st25.core.uicontrollers;


public class InventoryItem {
    private String itemID;
    private String itemName;

    public InventoryItem() {}

    public InventoryItem(String itemID, String itemName) {
        this.itemID = itemID;
        this.itemName = itemName;
    }

    // Getter and setter for type
    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }}

