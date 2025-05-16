package dk.g4.st25.assembly_station;

import com.google.gson.JsonObject;
import dk.g4.st25.common.machine.*;
import dk.g4.st25.common.protocol.Protocol;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;

import java.util.HashMap;

public class AssemblyStation extends Machine implements MachineSPI{
    private SystemStatus systemStatus; // What it is currently doing (producing, idle, etc.)
    private int processNumber; // Increasing integer starting at 1 that logs what process nr. it is at. '9999' is not allowed.
    public enum SystemStatus {
        IDLE,
        AWAITING_PARTS,
        READY,
        ASSEMBLING,
        AWAITING_PICKUP,
        ERROR
    }

    private Tray entryTray; // Trays for delivery and pick-up. Fixed number
    private Tray exitTray; // Trays for delivery and pick-up. Fixed number
    private boolean needsMoreComponents;
    private boolean productReadyForPickup;
    private Object mostRecentlyReceived;

    public AssemblyStation() {
        this.protocol = protocol;
        this.systemStatus = SystemStatus.IDLE;
        this.processNumber = 1;
        this.command = "";
        this.inventory = new HashMap<>();
        this.inventory.put("DroneComponents",0);
        this.entryTray = new Tray();
        this.exitTray = new Tray();
        this.needsMoreComponents = true;
        this.productReadyForPickup = false;
    }
    // From README section: 'Sequence (actions) with checks'
    //3) Assembly assemble product
    //3.1) AssemblyLine receives "execute assembly" command signal-----
    //3.2) AssemblyLine confirms correct item is delivered
    //3.3) AssemblyLine sends confirmation signal to coordinator
    //3.4) AssemblyLine confirms enough items have been delivered
    //3.5) AssemblyLine sends confirmation signal to coordinator
    //3.6) AssemblyLine executes the assembly instructions
    //3.7) AssemblyLine places product for pick-up
    //3.8) AssemblyLine sends task completion signal

    @Override
    public int taskCompletion() {
        /**
         * Signals whether a product is ready for pickup
         */
        int taskCompletion = 0;
        switch (command) {
            case "assemble":
                switch (this.systemStatus) {
                    case IDLE:
                        taskCompletion = 0;
                    case AWAITING_PARTS:
                        taskCompletion = 0;
                    case READY:
                        taskCompletion = 0;
                    case ASSEMBLING:
                        taskCompletion = 0;
                    case AWAITING_PICKUP:
                        taskCompletion = 1;
                    case ERROR:
                        taskCompletion = 0;
                    default:
                        break;
                }
            case "checkhealth":
                return productionCompletion(); // "checkhealth" and productionCompletion() both checks whether systemStatus is IDLE.
        }
        return taskCompletion;
    }

    @Override
    public int productionCompletion() {
        /**
         * Signals when all tasks relating to a production are complete
         * Use it to check that AssemblyStation is no longer AWAITING_PICKUP
         */
        int productionCompletion = 0;
        switch (this.systemStatus) {
            case IDLE:
                productionCompletion = 1;
            case AWAITING_PARTS:
                productionCompletion = 0;
            case READY:
                productionCompletion = 0;
            case ASSEMBLING:
                productionCompletion = 0;
            case AWAITING_PICKUP:
                productionCompletion = 0;
            case ERROR:
                productionCompletion = 0;
            default:
                break;
        }
        return productionCompletion;
    }

    @Override
    public boolean confirmItemDelivery() { // Add object to parameter
        /**
         * Checks that a tray is available, puts the item on the available tray,
         * checks that it is a DroneComponent, and then either puts it into inventory or discards it
         */
        this.systemStatus = SystemStatus.AWAITING_PARTS;
            if (this.entryTray.isAvailable()) {
                this.entryTray.setContent(new DroneComponent()); // Adds received item to tray. Placeholder statement until AGV can transfer object
                this.entryTray.setAvailable(false);
                if (mostRecentlyReceived instanceof DroneComponent) {
                    // Add it to inventory
                    this.inventory.put("DroneComponents", this.inventory.get("DroneComponents") + 1); // Placeholder statement. Increases the V of K,V-pair DroneComponents
                    return true;
                } else {
                    this.entryTray.setContent(null); // remove the incorrect item
                    this.entryTray.setAvailable(true);
                    return false;
                }
            }
        return false;
    }

    @Override
    public void setMostRecentlyReceived(Object mostRecentlyReceived) {
        /**
         * Used by Coordinator when AGV hands off item
         */
        this.mostRecentlyReceived = mostRecentlyReceived;
    }

    public void confirmItemQuantity() {
        /**
         * How many Drone components to make a Drone?
         */
        int componentsNeeded = 1;
        if (this.inventory.get("DroneComponents")>=componentsNeeded){
            this.systemStatus = SystemStatus.READY;
        }else{
            System.out.println("Not enough components received for production yet");
        }
    }

    @Override
    public JsonObject sendCommand(String commandType) {
        /**
         * commandType: whether to:
         *     "assemble": execute operation (assemble)
         *     "checkhealth": check machine health
         */
        if (commandType.equals("assemble")) {
            this.confirmItemQuantity(); // this will set SystemStatus.READY if enough components in inventory
            if (this.systemStatus == SystemStatus.READY && this.exitTray.isAvailable()) { // AStation must have received components, and have an available exit tray
                this.command = commandType; // Set latest received command
                String actualMessage = "\"ProcessID\": "+this.processNumber;
                this.protocol.writeTo(actualMessage,"emulator/operation");
                if (this.processNumber ==9998) {
                    this.processNumber = 1;
                } else {
                    this.processNumber++;
                }
                this.systemStatus = SystemStatus.ASSEMBLING;
                this.entryTray.setContent(null); // Clears the entry tray
                this.entryTray.setAvailable(true);
                return new JsonObject().getAsJsonObject("Success!"); // Success
            } else
                return null; // Assemble command sent, but machine not ready
        }
        else if (commandType.equals("checkhealth")) {
            this.command = commandType; // Set latest received command
            JsonObject healthStatus = this.protocol.readFrom("emulator/checkhealth", "unused");
            // Implement something that converts healthStatus to a code that is then returned
            return new JsonObject().getAsJsonObject("Success!");
        } else {
            return null;
        }
    }

    @Override
    public JsonObject sendCommand(String commandType, String commandName) {
        return null;
    }

    @Override
    public JsonObject sendCommand(String commandType, String commandName, String commandParam) {
        return null;
    }

    @Override
    public String getInventory() {
        return "";
    }

    @Override
    public String getCurrentSystemStatus() {
        /**
         * Returns either "Idle", "Executing", "Error" or "Unknown"
         * If called after (successful) sendCommand(assemble), will set AssemblyStation as AWAITING_PICKUP.
         * If called while AssemblyStation is AWAITING_PICKUP, will set system status to IDLE (do it after AGV picks up Drone)
         */
        JsonObject systemState = this.protocol.readFrom("emulator/status", "unused");
        // Retrieve number from State, and convert to description as seen on pg. 11:
        int stateNumber = systemState.get("State").getAsInt();
        String stateDesc;
        switch (stateNumber) {
            case 0:
                stateDesc = "Idle";
                if (systemStatus == SystemStatus.ASSEMBLING) {
                    this.systemStatus = SystemStatus.AWAITING_PICKUP;
                    this.exitTray.setContent(new Drone("Insert_Drone_ID")); // Placeholder-statement. Implement method to generate actual Drone ID here.
                    this.exitTray.setAvailable(false);
                } else if (systemStatus == SystemStatus.AWAITING_PICKUP) {
                    this.systemStatus = SystemStatus.IDLE;
                    this.exitTray.setContent(null);
                    this.exitTray.setAvailable(true);
                }
            case 1:
                stateDesc = "Executing";
            case 2:
                stateDesc = "Error";
            default:
                stateDesc = "Unknown";
        }
        return stateDesc;
    }

    @Override
    public String getCurrentConnectionStatus() {
        /**
         * Returns the timestamp of the latest heartbeat of the connection as a String
         */
        JsonObject systemStatus = this.protocol.readFrom("emulator/status", "unused");

        // Retrieves the timestamp of the latest connection heartbeat
        return systemStatus.get("Timestamp").getAsString();
    }
}

