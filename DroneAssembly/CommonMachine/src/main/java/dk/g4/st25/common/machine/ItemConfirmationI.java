package dk.g4.st25.common.machine;

public interface ItemConfirmationI {
    // Implement in AssemblyStation and Warehouse. Check, respectively, that correct Part and Product -type was delivered by AGV
    public boolean confirmItemDelivery();
}
