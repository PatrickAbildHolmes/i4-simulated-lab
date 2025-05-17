package dk.g4.st25.agv;

import dk.g4.st25.common.machine.*;
import com.google.gson.JsonObject;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.Locale;

public class AGV extends Machine implements MachineSPI, IExecuteCommand, IMonitorStatus, ItemConfirmationI {
    private String endpoint;
    private SystemStatus systemStatus;

    public enum SystemStatus {
        IDLE,
        READY,
        MOVING,
        EXECUTING,
        ERROR
    }

    private Tray[] trays;

    public AGV() {
        this.systemStatus = SystemStatus.IDLE;
        this.trays = new Tray[1];// One tray as the arm can only have 1 item in it
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

    public String getTimestamp() {
        return getStatus().get("timestamp").toString();
    }

    public int getState() {
        return getStatus().get("state").getAsInt();
    }

    public String getProgramName() {
        return getStatus().get("program name").toString();
    }

    public int getBattery() {
        return getStatus().get("battery").getAsInt();
    }

    @Override
    public int taskCompletion() {
        /**
         * Signals whether a Drone/Drone Part has been moved
        */
        int taskCompletion = 0;
        AGVCommands.MOVEASSEMBLY.getCommandString();
        switch (command) {
            case "PutAssemblyOperation":
                switch (this.systemStatus) {
                    case IDLE:
                        taskCompletion = 1;
                    case READY:
                        taskCompletion = 0;
                    case MOVING:
                        taskCompletion = 0;
                    case EXECUTING:
                        taskCompletion = 0;
                    case ERROR:
                        taskCompletion = 0;
                    default:
                        break;
                }
            case "PutStorageOperation":
                switch (this.systemStatus) {
                    case IDLE:
                        taskCompletion = 1;
                    case READY:
                        taskCompletion = 0;
                    case MOVING:
                        taskCompletion = 0;
                    case EXECUTING:
                        taskCompletion = 0;
                    case ERROR:
                        taskCompletion = 0;
                    default:
                        break;
                }
        }
        return taskCompletion;
    }

    @Override
    public int productionCompletion() {
        /**
         * Signals the "Movement Complete" and "confirm pick up"
         */
        int productionCompletion = 0;
        if (!this.command.equalsIgnoreCase(AGVCommands.PUTWAREHOUSE.getCommandString()) ||
                !(this.command.equalsIgnoreCase(AGVCommands.PUTASSEMBLY.getCommandString())))
            switch (this.systemStatus) {
                case IDLE:
                    productionCompletion = 0;
                case MOVING:
                    productionCompletion = 0;
                case EXECUTING:
                    productionCompletion = 0;
                case READY:
                    productionCompletion = 1;
                case ERROR:
                    productionCompletion = 0;
                default:
                    break;
            }
        return productionCompletion;
    }

    @Override
    public boolean confirmItemDelivery() {
        this.systemStatus = SystemStatus.READY; // When AGV has delivered item to either AssemblyStation or Warehouse it becomes ready
        for (Tray tray : trays) {
            if (tray.isAvailable() && (this.command.equalsIgnoreCase(AGVCommands.PICKWAREHOUSE.getCommandString()) ||
                            (this.command.equalsIgnoreCase(AGVCommands.PICKASSEMBLY.getCommandString())))) {
                tray.setContent(new DroneComponent()); // Placeholder until Coordinator can transfer objects
                tray.setAvailable(false);
                return true;
            } else {
                // If the last command is not a pick operation, it means that the agv delivers the item to either the warehouse or the assemblystation
                // and therefore removes it's item
                tray.setContent(null);
                tray.setAvailable(true);
                return false;
            }

        }
        return false;
    }

    @Override
    public JsonObject sendCommand(String commandType, String commandParam) {
        /**
         * Sends the given command through AGV's given protocol, and handles which states should be set based
         * on which operation is run
         */
        this.command = commandType; // We set the command to be latest recieved command
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
        return "";
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
