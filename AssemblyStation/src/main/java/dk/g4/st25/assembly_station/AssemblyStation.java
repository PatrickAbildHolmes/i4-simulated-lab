package dk.g4.st25.assembly_station;

import com.google.gson.JsonObject;
import dk.g4.st25.common.machine.*;
import dk.g4.st25.common.protocol.Protocol;
import dk.g4.st25.common.protocol.ProtocolSPI;

import java.util.HashMap;

public class AssemblyStation extends Machine{
    private SystemStatus systemStatus; // What it is currently doing (producing, idle, etc.)
    private int processNumber; // Increasing integer starting at 1 that logs what process nr. it is at. '9999' is not allowed.
    private final Tray entryTray; // Trays for delivery and pick-up. Fixed number. "Can't receive a 'new' Tray, hence final"
    private final Tray exitTray; // Trays for delivery and pick-up. Fixed number. "Can't receive a 'new' Tray, hence final"
    private Object mostRecentlyReceived;
    public enum SystemStatus {
        IDLE,
        AWAITING_PARTS,
        READY,
        ASSEMBLING,
        AWAITING_PICKUP,
        ERROR
    }

    public AssemblyStation() {
        this.systemStatus = SystemStatus.IDLE;
        this.processNumber = 1;
        this.command = "";
        this.inventory = new HashMap<>();
        this.inventory.put("DroneComponents",0);
        this.entryTray = new Tray();
        this.exitTray = new Tray();
    }

    @Override
    public int taskCompletion() {
        /**
         * Signals when all tasks relating to a production are complete (Step 3 is complete)
         * Use it to check that AssemblyStation is no longer AWAITING_PICKUP
         * (This method is used to verify that the sequence of actions within the (Coordinator/production) step is complete)
         */
        int taskCompletion = 0;
        switch (this.systemStatus) {
            case IDLE:
                taskCompletion = 1;
            case AWAITING_PARTS:
                taskCompletion = 0;
            case READY:
            case ASSEMBLING:
            case AWAITING_PICKUP:
            case ERROR:
        }
        return taskCompletion;
    }

    @Override
    public int actionCompletion() {
        /**
         * Signals whether a product is ready for pickup (action "assembling" is complete"),
         * or health was checked with "checkhealth"
         * (This method is used to verify that the latest action (move, pick up, present object) is finished)
         */
        int actionCompletion = 0;
        switch(this.command){
            case "assemble":
                switch (this.systemStatus) {
                    case IDLE: // init value is 0
                    case AWAITING_PARTS:
                    case READY:
                    case ASSEMBLING: // If command was "assemble",
                        if (this.getCurrentSystemStatus().equals("Idle")) { // and the machine is now "Idle",
                            this.systemStatus = SystemStatus.AWAITING_PICKUP; // that means it is now awaiting pick-up
                            this.exitTray.setContent(this.mostRecentlyReceived); // Make sure Coordinator has generated a new Drone
                            this.exitTray.setAvailable(false);
                            actionCompletion = 1;
                        }
                        actionCompletion = 0;
                    case AWAITING_PICKUP: // And if command was "assemble", and the machine was awaiting pick-up,
                        this.systemStatus = SystemStatus.IDLE;  // this method is called after the AGV has picked it up,
                        this.exitTray.setContent(null);         // and so resets the system state and exit-tray
                        this.exitTray.setAvailable(true);
                        actionCompletion = 1;
                    case ERROR:
                        actionCompletion = 0;
                }
            case "checkhealth": // if "checkhealth" was the latest command, we want to ensure the machine is in idle state
                switch (this.systemStatus) {
                    case IDLE:
                        actionCompletion = 1;
                    case AWAITING_PARTS:
                    case READY:
                    case ASSEMBLING:
                    case AWAITING_PICKUP:
                    case ERROR:
                }
        }
        return actionCompletion;
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
         * (This method is used to handle object drop-off, since an object can be passed (in Coordinator) through this method)
         */
        this.mostRecentlyReceived = mostRecentlyReceived;
    }

    @Override
    public void setMachineProtocol(ProtocolSPI protocol) {
        this.protocol = protocol;
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
    public String getInventory() {
        // Returns the content of the two trays using '\newline'.
        // Calling .toString() on a null object safely returns "null"
        String entryTrayContent = "Entry tray: " + this.entryTray.getContent().toString();
        String exitTrayContent = "Exit tray: " + this.exitTray.getContent().toString();
        return entryTrayContent + "\n" + exitTrayContent;
    }

    @Override
    public String getCurrentSystemStatus() {
        /**
         * Returns either "Idle", "Executing", "Error" or "Unknown"
         * If called after (successful) sendCommand(assemble), will set AssemblyStation as AWAITING_PICKUP.
         * If called while AssemblyStation is AWAITING_PICKUP, will set system status to IDLE (do it after AGV picks up Drone)
         */
        try {
            JsonObject systemState = this.protocol.readFrom("emulator/status", "unused");
            // Retrieve number from State, and convert to description as seen on pg. 11 in Technical Documentation:
            int stateNumber = systemState.get("State").getAsInt();
            String stateDesc;
            switch (stateNumber) {
                case 0:
                    stateDesc = "Idle";
                case 1:
                    stateDesc = "Executing";
                case 2:
                    stateDesc = "Error";
                default:
                    stateDesc = "Unknown";
            }
            return stateDesc;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error getting Assembly Station status";
        }
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

