package dk.g4.st25.warehouse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.g4.st25.common.machine.Drone;
import dk.g4.st25.common.machine.Machine;
import dk.g4.st25.common.protocol.ProtocolSPI;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.common.util.DronePart;

import java.util.HashMap;

public class Warehouse extends Machine {
    // SOAP service object for interacting with the warehouse system
    // Counter to track the number of items successfully fetched
    private int itemsFetched = 0;
    private final String endpoint = "http://localhost:8081/Service.asmx";
    private SystemStatus systemStatus;
    private Object mostRecentlyReceived;
    private boolean firstTimeInventoryUsed = true;
    public enum SystemStatus {
        IDLE,
        EXECUTING,
        ERROR,
        UNKNOWN
    }
    public Warehouse() { // There must be a parameterless, public constructor for service-loaders to work
        this.systemStatus = SystemStatus.IDLE;
        this.command = "";
        this.inventory = new HashMap<>();
        // Possibly add the use of Tray objects to simulate the 10 trays Warehouse has
        // Add Dotenv endpoint similar to implementation in AGV.java?
    }

    @Override
    public int taskCompletion() {
        /**
         * This method is used to verify that the sequence of actions within the (Coordinator/production) step is complete
         * */
        int taskCompletion = 0;
        if (this.systemStatus == SystemStatus.IDLE) {
            taskCompletion = 1;
        }
        return taskCompletion;
    }

    @Override
    public int actionCompletion() {
        /**
         * This method is used to verify that the latest action (move, pick up, present object) is finished,
         * as opposed to taskCompletion that verifies that the sequence of actions within the (Coordinator/production) step is complete
         * */
        int actionCompletion = 0;
        if (this.getCurrentSystemStatus().equalsIgnoreCase(SystemStatus.IDLE.name())){
            this.systemStatus = SystemStatus.IDLE;
            actionCompletion = 1;
        }
        return actionCompletion;
    }

    @Override
    public boolean confirmItemDelivery() {
        /**
         * This method verifies that the correct item was delivered *To* this machine.
         * Should be checking that object was instanceof DroneComponent or Drone
         * */
        if (mostRecentlyReceived instanceof Drone) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void setMostRecentlyReceived(Object mostRecentlyReceived) {
        /**
         * Ideally this method is used to handle object drop-off, since an object can be passed (in Coordinator) through this method,
         * I.E. from Warehouse->AGV->Assembly->AGV->Warehouse
        * */
        this.mostRecentlyReceived = mostRecentlyReceived;
    }

    @Override
    public void setMachineProtocol(ProtocolSPI protocol) {
        this.protocol = protocol;
    }

    @Override
    public JsonObject sendCommand(String commandType) {
        this.systemStatus = SystemStatus.EXECUTING;
        JsonObject result = new JsonObject();
        JsonArray tempInventory = JsonParser.parseString(getInventory()).getAsJsonObject().get("Inventory").getAsJsonArray();
        switch (commandType.toLowerCase()) {
            case "refresh":
                refreshInventory(endpoint);
                result.addProperty("status","Success!");
                result.addProperty("message","Inventory refreshed for warehouse");
                return result;
            // Forslag til måden at måske håndtere det på
            case "pickitem":
                for (int i = 0; i<tempInventory.size(); i++) {
                    if (!tempInventory.get(i).getAsJsonObject().get("Content").getAsString().equals("") ||
                            !(tempInventory.get(i).getAsJsonObject().get("Content").getAsString().equals("Finished drone"))) {
                        String pickMessage = "{\"action\":\"pick\",\"trayId\":" + i +"}";
                        this.protocol.writeTo(pickMessage,endpoint);
                        result.addProperty("status","Success!");
                        result.addProperty("message","Success! Picked item from slot: " + i);
                        return result;
                    }
                }
            case "insertitem":
                // First iterate through the inventory to check for empty trays
                for (int i = 0; i<tempInventory.size();i++) {
                    if (tempInventory.get(i).getAsJsonObject().get("Content").getAsString().equals("")) {
                        String insertMessage = "{\"action\":\"insert\", \"trayId\":"+ (i+1) +", \"itemName\":\"Finished drone\"}";
                        this.protocol.writeTo(insertMessage,endpoint);
                        result.addProperty("status","Success!");
                        result.addProperty("message","Success! Inserted an item at slot: " + (i+1));
                        return result;
                    }
                }
                // Otherwise we iterate through the inventory and check if there are Finished Drones that we can move to big storage
                for (int j=0;j<tempInventory.size();j++) {
                    if (tempInventory.get(j).getAsJsonObject().get("Content").getAsString().equals("Finished drone")) {
                        // First we remove the old finished drone
                        String pickMessage = "{\"action\":\"pick\",\"trayId\":" + (j+1) +"}";
                        this.protocol.writeTo(pickMessage,endpoint);
                        // Then we insert the new finished drone
                        String insertMessage = "{\"action\":\"insert\", \"trayId\":"+ (j+1) +", \"itemName\":\"Finished drone\"}";
                        this.protocol.writeTo(insertMessage,endpoint);
                        result.addProperty("status","Success!");
                        result.addProperty("message","Success! Moved a finished drone to big storage, " +
                                "and inserted new finished drone in an item at slot: " + (j+1));
                        return result;
                    }
                }
            default:
                result.addProperty("status", "error");
                result.addProperty("message", "Unknown command type.");
                return result;
        }
    }

    @Override
    public String getInventory() {
        // The inventory should be refreshed from Database, the first time we call it
        if (this.firstTimeInventoryUsed) {
            refreshInventory(endpoint);
            this.firstTimeInventoryUsed = false;
        }
        return protocol.readFrom(endpoint, "getInventory").toString();
    }

    @Override
    public String getCurrentSystemStatus() {
        // Create a list to store system status messages
        String status = JsonParser.parseString(getInventory()).getAsJsonObject().get("State").getAsString();
        String stateDesc;
        switch (status) {
            case "0":
                stateDesc = SystemStatus.IDLE.name();
                return stateDesc;
            case "1":
                stateDesc = SystemStatus.EXECUTING.name();
                return stateDesc;
            case "2":
                stateDesc = SystemStatus.ERROR.name();
                return stateDesc;
            default:
                stateDesc = SystemStatus.UNKNOWN.name();
                return stateDesc;
        }
    }

    @Override
    public String getCurrentConnectionStatus() {
        if (!getCurrentSystemStatus().equals(SystemStatus.UNKNOWN.name())) {
            return "Warehouse Active";
        } else {
            return "Warehouse NOT Active";
        }
    }

    public void refreshInventory(String endpoint) {
        DronePart[] droneItems = DronePart.values();

        for (int i = 0; i<11; i++) {
            String pickMessage = "{\"action\":\"pick\",\"trayId\":" + i +"}";
            this.protocol.writeTo(pickMessage, endpoint);
            for (int j = 0; j<9; j++){
                String insertMessage = "{\"action\":\"insert\", \"trayId\":"+ j +", \"itemName\":\"" + droneItems[j].getItemName() + "\"}";
                this.protocol.writeTo(insertMessage,endpoint);
            }
        }
    }
}