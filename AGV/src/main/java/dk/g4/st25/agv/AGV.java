package dk.g4.st25.agv;

import dk.g4.st25.common.machine.*;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;

public class AGV extends Machine implements MachineSPI {
    private String endpoint;
    private SystemStatus systemStatus;
    private Object mostRecentlyReceived;
    private final Tray holding_tray; // "Can't receive a 'new' Tray, hence final"
    public enum SystemStatus {
        IDLE,
        READY,
        MOVING,
        EXECUTING,
        ERROR
    }

    public AGV() {
        this.systemStatus = SystemStatus.IDLE;
        this.holding_tray = new Tray();// One tray as the arm can only hold 1 item
        this.command = "";
        this.inventory = new HashMap<>();
        try {
            this.endpoint = Dotenv.load().get("AGV_ENDPOINT");
        } catch (Exception e) {
            this.endpoint = Dotenv.configure().directory("../").load().get("AGV_ENDPOINT");
        }
    }

    public JsonObject getStatus() {
        return protocol.readFrom(endpoint, AGVCommands.GETSTATUS.getCommandString());
    }

    @Override
    public int taskCompletion() {
        /**
         * Signals whether a Drone/Drone Part has been moved.
         * (This method is used to verify that the sequence of actions within the (Coordinator/production) step is complete)
        */
        int taskCompletion = 0;
        switch (this.command) {
            case "PutAssemblyOperation": // Return 1 if idle after either of these two commands
            case "PutStorageOperation":  // If latest command was not one of these when taskCompletion is called, something went wrong
                switch (this.systemStatus) {
                    case IDLE:
                        taskCompletion = 1;
                    case READY:
                        taskCompletion = 0;
                    case MOVING:
                    case EXECUTING:
                    case ERROR:
                }
        }
        return taskCompletion;
    }

    @Override
    public int actionCompletion() {
        /**
         * Signals the "Movement Complete" and "confirm pick up"
         * This method is used to verify that the latest action (move, pick up, present object) is finished,
         * as opposed to taskCompletion that verifies that the sequence of actions within the (Coordinator/production) step is complete
         */
        int productionCompletion = 0;
        if (!this.command.equalsIgnoreCase(AGVCommands.PUTWAREHOUSE.getCommandString()) ||
                !(this.command.equalsIgnoreCase(AGVCommands.PUTASSEMBLY.getCommandString())))
            switch (this.systemStatus) {
                case IDLE: // Default value is 0
                case MOVING:
                case EXECUTING:
                case READY:
                    productionCompletion = 1;
                case ERROR:
                    productionCompletion = 0;
            }
        return productionCompletion;
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
    public boolean confirmItemDelivery() {
        /**
         * This method verifies that the correct item was delivered *To* this machine.
         * Should be checking that object was instanceof DroneComponent or Drone
         * */
        this.systemStatus = SystemStatus.READY; // When AGV has delivered item to either AssemblyStation or Warehouse it becomes ready
            if (this.holding_tray.isAvailable() && // Holding tray must be available AND the latest command must be a pick-up operation
                    (this.command.equalsIgnoreCase(AGVCommands.PICKWAREHOUSE.getCommandString()) || (this.command.equalsIgnoreCase(AGVCommands.PICKASSEMBLY.getCommandString())))) {
                this.holding_tray.setContent(this.mostRecentlyReceived); // Grips the item that the Coordinator conjures up for it
                this.holding_tray.setAvailable(false);
                return true;
            } else {
                // If the last command is not a pick operation, it means that the agv delivers the item to either the warehouse or the assembly station
                // and therefore removes it's item
                this.holding_tray.setContent(null);
                this.holding_tray.setAvailable(true);
                return false;
        }
    }

    @Override
    public JsonObject sendCommand(String commandType) {
        /**
         * Sends the given command through AGV's given protocol, and handles which states should be set based
         * on which operation is run
         */
        this.command = commandType; // We set the command to be latest received command
        switch (commandType.toLowerCase()) {
            case "movetochargeroperation":
                if (protocol.writeTo(AGVCommands.MOVECHARGER.getCommandString(), endpoint) == 1) {
                    this.systemStatus = SystemStatus.MOVING;
                    return new JsonObject().getAsJsonObject("Success moving to Charger!");
                }
            case "movetoassemblyoperation":
                if (protocol.writeTo(AGVCommands.MOVEASSEMBLY.getCommandString(), endpoint) == 1) {
                    this.systemStatus = SystemStatus.MOVING;
                    return new JsonObject().getAsJsonObject("Success moving to Assembly!");
                }
            case "movetostorageoperation":
                if (protocol.writeTo(AGVCommands.MOVESTORAGE.getCommandString(), endpoint) == 1) {
                    this.systemStatus = SystemStatus.MOVING;
                    return new JsonObject().getAsJsonObject("Success moving to Storage!");
                }
            case "putassemblyoperation":
                if (protocol.writeTo(AGVCommands.PUTASSEMBLY.getCommandString(), endpoint) == 1) {
                    this.systemStatus = SystemStatus.EXECUTING;
                    return new JsonObject().getAsJsonObject("Success delivering to Assembly!");
                }
            case "pickassemblyoperation":
                if (protocol.writeTo(AGVCommands.PICKASSEMBLY.getCommandString(), endpoint) == 1) {
                    this.systemStatus = SystemStatus.EXECUTING;
                    confirmItemDelivery();
                    return new JsonObject().getAsJsonObject("Success picking from Assembly!");
                }
            case "pickwarehouseoperation":
                if (protocol.writeTo(AGVCommands.PICKWAREHOUSE.getCommandString(), endpoint) == 1) {
                    this.systemStatus = SystemStatus.EXECUTING;
                    confirmItemDelivery();
                    return new JsonObject().getAsJsonObject("Success picking from Storage!");
                }
            case "putstorageoperation":
                if (protocol.writeTo(AGVCommands.PUTWAREHOUSE.getCommandString(), endpoint) == 1) {
                    this.systemStatus = SystemStatus.EXECUTING;
                    return new JsonObject().getAsJsonObject("Success delivering to Storage!");
                }
            default:
                System.out.println("No Operation of that name found!");
                break;
        }
        System.out.println("Wrong CommandType! It should be the name of the operation");
        return null;
    }

    @Override
    public String getInventory() {
        // Converts the item held in its Tray to String and returns it, since that is the AGV "inventory"
        return this.holding_tray.getContent().toString();
    }

    @Override
    public String getCurrentSystemStatus() {
        int stateNumber = getStatus().getAsInt();
        String stateDesc;
        switch (stateNumber) {
            case 1:
                stateDesc = SystemStatus.IDLE.name();
            case 2:
                stateDesc = SystemStatus.EXECUTING.name();
            case 3:
                stateDesc = SystemStatus.ERROR.name();
            default:
                stateDesc = "Unknown";
        }
        return stateDesc;
    }

    @Override
    public String getCurrentConnectionStatus() {
        if (protocol.connect(endpoint) == 1) {
            return "AGV is Connected";
        } else {
            return "AGV is NOT Connected";
        }
    }
}
