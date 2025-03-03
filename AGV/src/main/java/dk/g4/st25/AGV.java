package dk.g4.st25;

import com.google.gson.JsonObject;
import dk.g4.st25.REST.Protocol;
import dk.g4.st25.REST.REST;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AGV {
    private final String url;
    private final Protocol protocol;

    private int battery;
    private String programName;
    private int state;
    private String timeStamp;

    AGV(String url, Protocol protocol) {
        this.url = url;
        this.protocol = protocol;
        this.update();
    }

    public JsonObject getStatus() {
        return protocol.get(url);
    }

    public void update() {
        JsonObject status = this.getStatus();
        updateFromJson(status);
    }

    private void update(JsonObject status) {
        updateFromJson(status);
    }

    private void updateFromJson(JsonObject status) {
        if (status == null) return; // in tests status will always be null as we are mocking the API call

        battery = status.get("battery").getAsInt();
        programName = status.get("program name").getAsString();
        state = status.get("state").getAsInt();
        timeStamp = status.get("timestamp").getAsString();
    }

    // Programs

    public void execute() {
        if (programName == "no program loaded") return; // else AGV will freeze
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("State", 2);
        JsonObject res = this.protocol.put(url, requestBody);
        this.update(res);
    }

    private void executeOperation(String programName) {
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
        AGV agv = new AGV("http://localhost:8082/v1/status", new REST());
        agv.print();
        agv.execute();
        agv.update();
        agv.print();
    }
}
