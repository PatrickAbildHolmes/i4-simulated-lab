package dk.g4.st25.agv;

import dk.g4.st25.common.machine.Machine;
import dk.g4.st25.common.machine.MachineSPI;
import com.google.gson.JsonObject;
import dk.g4.st25.common.machine.Tray;
import dk.g4.st25.common.protocol.Protocol;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.rest.REST;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.Map;

public class AGV extends Machine implements MachineSPI, IExecuteCommand, IMonitorStatus {
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
    private boolean hasProgram = false;

    public AGV() {
        this.systemStatus = SystemStatus.IDLE;
        this.trays = new Tray[1]; // One tray as the arm can only have 1 item in it

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

//    public JsonObject execute() {
//        if (hasProgram) return null; // else AGV will freeze
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("State", 2);
//        JsonObject res = this.protocol.writeTo("requestBody", null);
//        hasProgram = true;
//        return res;
//    }
//
//    private JsonObject executeOperation(String programName) {
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("Program name", programName);
//        requestBody.put("State", 1);
//        JsonObject res = protocol.writeTo(requestBody);
//        hasProgram = true;
//        return res;
//    }

//    public JsonObject chargeBattery() {
//        return executeOperation("MoveToChargerOperation");
//    }
//
//    public JsonObject moveToAssembly() {
//        return executeOperation("MoveToAssemblyOperation");
//    }
//
//    public JsonObject moveToStorage() {
//        return executeOperation("MoveToStorageOperation");
//    }
//
//    public JsonObject putAssembly() {
//        return executeOperation("PutAssemblyOperation");
//    }
//
//    public JsonObject pickAssembly() {
//        return executeOperation("PickAssemblyOperation");
//    }
//
//    public JsonObject pickWarehouse() {
//        return executeOperation("PickWarehouseOperation");
//    }
//
//    public JsonObject putWarehouse() {
//        return executeOperation("PutWarehouseOperation");
//    }

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
        return 0;
    }

    @Override
    public int productionCompletion() {
        /**
         * Signals the "Movement Complete" signal
         */
        int productionCompletion = 0;
        if (this.systemStatus.equals(SystemStatus.IDLE)) {
            productionCompletion = 1;
        }
        return productionCompletion;
    }

    @Override
    public JsonObject sendCommand(String commandType, String commandParam) {
        if (commandType.equals("writeTo")) {
            if (this.systemStatus == SystemStatus.READY) {
                protocol.writeTo(commandParam, endpoint);

                this.systemStatus = SystemStatus.MOVING;
                return new JsonObject().getAsJsonObject("Success!");
            }
        } else if (commandType.equals("readFrom")) {
            return protocol.readFrom(endpoint, commandParam);
        }
        System.out.println("Wrong CommandType!");
        return null;
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
