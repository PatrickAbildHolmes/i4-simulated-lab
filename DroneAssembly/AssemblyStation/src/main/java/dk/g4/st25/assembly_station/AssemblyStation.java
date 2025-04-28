package dk.g4.st25.assembly_station;

import com.google.gson.JsonObject;
import dk.g4.st25.common.machine.*;
import dk.g4.st25.common.protocol.Protocol;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;

import java.util.HashMap;

public class AssemblyStation extends Machine implements MachineSPI, IMonitorStatus, IExecuteCommand, ItemConfirmationI {
    private SystemStatus systemStatus; // What it is currently doing (producing, idle, etc.)
    private int processNumber; // Increasing integer starting at 1 that logs what process nr. it is at. '9999' is not allowed.
    public enum SystemStatus {
        IDLE,
        AWAITING_PARTS,
        ASSEMBLING,
        AWAITING_PICKUP;
    }

    private Tray[] trays; // Trays for delivery and pick-up. Fixed number
    private Boolean NeedsMoreComponents;
    private Boolean ProductReadyForPickup;
    private Boolean AvailableTray;

    AssemblyStation(Protocol protocol) {
        this.protocol = protocol;
        this.systemStatus = SystemStatus.IDLE;
        this.processNumber = 1;
        this.command = "";
        this.inventory = new HashMap<>();
        this.trays = new Tray[2]; // Two trays?
        this.NeedsMoreComponents = true;
        this.ProductReadyForPickup = false;
        this.AvailableTray = true;
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
    public int productionCompletion() {
        // Signals when all tasks relating to a production are complete

        return 0;
    }

    // ------- Redundant -------
    @Override
    public int taskCompletion() {
        // Signals that a task (assemble 1 product) is complete
        // Superseded by sendCommand

        return 1;
    }

    @Override
    public boolean confirmItemDelivery() {
        // Confirm that correct item type was delivered
        //      AssemblyStation will only every receive parts,
        //      and Warehouse only every products.
        // Called by coordinator. If true, check Item Quantity
        return true;
    }

    public boolean confirmItemQuantity() {
        return true;
    }

    @Override
    public int sendCommand(String commandType, String commandParam) {
        // commandType: whether to:
        //                  "assemble": execute operation (assemble)
        //                  "check_health": check machine health
        // commandParam: Not used.
        if (commandType.equals("assemble")) {
            String actualMessage = "\"ProcessID\": "+this.processNumber;
            this.protocol.writeTo(actualMessage,"emulator/operation");
            this.processNumber +=1;
            return 1; // Success
        }
        else if (commandType.equals("checkHealth")) {
            JsonObject healthStatus = this.protocol.readFrom("emulator/checkhealth", "unused");
            // Implement something that converts healthStatus to a code that is then returned
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String getCurrentSystemStatus() {
        JsonObject systemState = this.protocol.readFrom("emulator/status", "unused");
        // Retrieve number from State, and convert to description as seen on pg. 11:
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
    }

    @Override
    public String getCurrentConnectionStatus() {
        JsonObject systemStatus = this.protocol.readFrom("emulator/status", "unused");
        // Retrieves the timestamp of the latest connection heartbeat
        return systemStatus.get("Timestamp").getAsString();
    }
}

