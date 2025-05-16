package dk.g4.st25.agv;

import dk.g4.st25.common.machine.Machine;
import dk.g4.st25.common.machine.MachineSPI;
import com.google.gson.JsonObject;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.rest.REST;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AGV extends Machine implements MachineSPI, IExecuteCommand, IMonitorStatus {
    private final REST protocol;
    private boolean hasProgram = false;

    public AGV(REST rest) {
        this.protocol = rest;
    }

    public AGV() {
        this(new REST());
    }

    public JsonObject getStatus() {
        return protocol.get();
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

    public JsonObject execute() {
        if (hasProgram) return null; // else AGV will freeze
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("State", 2);
        JsonObject res = this.protocol.put(requestBody);
        hasProgram = true;
        return res;
    }

    private JsonObject loadProgram(String programName) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Program name", programName);
        requestBody.put("State", 1);
        JsonObject res = protocol.put(requestBody);
        hasProgram = true;
        return res;
    }

    public static void main(String[] args) {
        AGV agv = new AGV();

        agv.moveToAssembly();
        System.out.println(agv.getStatus());
        Scanner scanner = new Scanner(System.in);
        scanner.next();
        agv.execute();
        scanner.next();
        System.out.println(agv.getStatus());
    }

    public JsonObject chargeBattery() {
        return loadProgram("MoveToChargerOperation");
    }

    public JsonObject moveToAssembly() {
        return loadProgram("MoveToAssemblyOperation");
    }

    public JsonObject moveToStorage() {
        return loadProgram("MoveToStorageOperation");
    }

    public JsonObject putAssembly() {
        return loadProgram("PutAssemblyOperation");
    }

    public JsonObject pickAssembly() {
        return loadProgram("PickAssemblyOperation");
    }

    public JsonObject pickWarehouse() {
        return loadProgram("PickWarehouseOperation");
    }

    public JsonObject putWarehouse() {
        return loadProgram("PutWarehouseOperation");
    }

    @Override
    public int taskCompletion() {
        return 0;
    }

    @Override
    public int productionCompletion() {
        return 0;
    }

    @Override
    public void setMostRecentlyReceived(Object mostRecentlyReceived) {

    }


    @Override
    public JsonObject sendCommand(String commandType) {
        return null;
    }
    @Override
    public JsonObject sendCommand(String commandType, String commandName) {
        if (commandType.equals("writeTo")) {
        }
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
        return null;
    }

    @Override
    public String getCurrentConnectionStatus() {
        return null;
    }

    @Override
    public boolean confirmItemDelivery() {
        return false;
    }
}
