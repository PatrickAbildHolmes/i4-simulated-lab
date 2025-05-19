package dk.g4.st25.warehouse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dk.g4.st25.common.machine.Drone;
import dk.g4.st25.common.machine.Machine;
import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.protocol.Protocol;

import java.util.HashMap;

public class Warehouse extends Machine implements MachineSPI {
    // SOAP service object for interacting with the warehouse system
    // Counter to track the number of items successfully fetched
    private int itemsFetched = 0;
    private final String endpoint = "http://localhost:8081/Service.asmx";
    private SystemStatus systemStatus;
    private Object mostRecentlyReceived;
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
        if (this.systemStatus == SystemStatus.EXECUTING) {
            if (this.getCurrentSystemStatus().equals("idle")){
                this.systemStatus = SystemStatus.IDLE;
                actionCompletion = 1;
            }
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
    public JsonObject sendCommand(String commandType) {
        this.systemStatus = SystemStatus.EXECUTING;
        JsonObject result = new JsonObject();
        JsonArray tempInventory = this.protocol.readFrom("getInventory",endpoint).get("Inventory").getAsJsonArray();
        switch (commandType.toLowerCase()) {
            case "refresh":
                refreshInventory(endpoint);
                result.addProperty("status","Success");
                result.addProperty("message","Inventory refreshed for warehouse");
                return result;
            // Forslag til måden at måske håndtere det på
            case "pickitem":
                for (int i = 0; i>tempInventory.size(); i++) {
                    if (tempInventory.get(i).getAsString().equals("Drone component")) {
                        String pickMessage = "{\"action\":\"pick\",\"trayId\":" + i +"}";
                        this.protocol.writeTo(pickMessage,endpoint);
                        result.addProperty("status","Success");
                        result.addProperty("message","Success! Picked item from slot: " + i);
                    }
                }
            case "insertitem":
                for (int i = 0; i>tempInventory.size();i++) {
                    if (tempInventory.get(i).getAsString().equals("")) {
                        String insertMessage = "{\"action\":\"insert\", \"trayId\":"+ i +", \"itemName\":\"Finished drone\"}";
                        this.protocol.writeTo(insertMessage,endpoint);
                        result.addProperty("status","Success");
                        result.addProperty("message","Success! Inserted an item at slot: " + i);
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
        return protocol.readFrom(endpoint, "GetInventory").toString();
    }

    @Override
    public String getCurrentSystemStatus() {
        // Create a list to store system status messages
        String status = protocol.readFrom(endpoint, "GetInventory").get("State").getAsString();
        System.out.println(status);
        String stateDesc;
        switch (status) {
            case "0":
                stateDesc = "Idle";
                return stateDesc;
            case "1":
                stateDesc = "Executing";
                return stateDesc;
            case "2":
                stateDesc = "Error";
                return stateDesc;
            default:
                stateDesc = "Unknown";
                return stateDesc;
        }
    }

    @Override
    public String getCurrentConnectionStatus() {
        // Create a list to store connection status messages
        if (this.protocol != null) {
            return "Warehouse protocol active. Protocol: " + this.protocol;
        }
        return "ERROR: Warehouse protocol not active";
    }

    public void refreshInventory(String endpoint) {
        for (int i = 0; i<10; i++) {
            String pickMessage = "{\"action\":\"pick\",\"trayId\":" + i +"}";
            this.protocol.writeTo(pickMessage, endpoint);
            for (int j = 0; j<8; j++){
                String insertMessage = "{\"action\":\"insert\", \"trayId\":"+ i +", \"itemName\":\"Drone component\"}";
                this.protocol.writeTo(insertMessage,endpoint);
            }
        }
    }
}