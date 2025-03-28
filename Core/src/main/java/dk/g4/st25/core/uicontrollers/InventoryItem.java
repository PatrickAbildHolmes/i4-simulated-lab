package dk.g4.st25.core.uicontrollers;


public class InventoryItem {
    private String itemType;
    private String itemID;
    private String amount;

    public InventoryItem(String itemType, String itemID, int amount){
        this.itemType = new String(itemType);
        this.itemID = new String(itemID);
        this.amount = new String(String.valueOf(amount));
    }

    // Getter and setter for type
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}

