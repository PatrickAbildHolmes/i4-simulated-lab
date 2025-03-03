package dk.g4.st25;

import com.google.gson.JsonObject;
import dk.g4.st25.REST.Protocol;
import dk.g4.st25.REST.REST;

import java.util.HashMap;
import java.util.Map;


public class AGV {
    private final String url = "http://localhost:8082/v1/status";
    private final Protocol protocol = new REST();

    private int battery;
    private String programName;
    private int state;
    private String timeStamp;

    AGV() {
        this.update();
    }

    public JsonObject getStatus() {
        return protocol.get(url);
    }

    public void update() {
        JsonObject status = this.getStatus();
        updateFromJson(status);
    }

    public void update(JsonObject status) {
        updateFromJson(status);
    }

    private void updateFromJson(JsonObject status) {
        battery = status.get("battery").getAsInt();
        programName = status.get("program name").getAsString();
        state = status.get("state").getAsInt();
        timeStamp = status.get("timestamp").getAsString();
    }

    public void execute() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("State", 2);
        JsonObject res = protocol.put(url, requestBody);
        this.update(res);
    }

    // Programs

    public void executeOperation(String programName) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Program name", programName);
        requestBody.put("State", 1);
        JsonObject res = protocol.put(url, requestBody);
        this.update(res);
    }

    public void chargeBattery() {
        executeOperation("MoveToChargerOperation");
    }

    public void moveToAssembly() {
        executeOperation("MoveToAssemblyOperation");
    }

    public void moveToStorage() {
        executeOperation("MoveToStorageOperation");
    }

    public void putAssembly() {
        executeOperation("PutAssemblyOperation");
    }

    public void pickAssembly() {
        executeOperation("PickAssemblyOperation");
    }

    public void pickWarehouse() {
        executeOperation("PickWarehouseOperation");
    }

    public void putWarehouse() {
        executeOperation("PutWarehouseOperation");
    }

    @Override
    public String toString() {
        return String.format("Battery: %d, State: %d, Program: %s", battery, state, programName);
    }

    public void print() {
        System.out.println(this);
    }

    // Getters

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getState() {
        return state;
    }

    public String getProgramName() {
        return programName;
    }

    public int getBattery() {
        return battery;
    }


    public static void main(String[] args) {
        AGV agv = new AGV();
        agv.print();
        agv.execute();
        agv.update();
        agv.print();
    }
}
