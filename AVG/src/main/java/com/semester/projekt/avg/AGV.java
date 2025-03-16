package com.semester.projekt.avg;

import com.google.gson.JsonObject;
import com.semester.projekt.rest.iprotocol.Protocol;
import com.semester.projekt.rest.REST;

import java.util.HashMap;
import java.util.Map;

public class AGV {
    private final Protocol protocol;
    private boolean hasProgram = false;

    AGV(REST rest) {
        this.protocol = rest;
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

    private JsonObject executeOperation(String programName) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Program name", programName);
        requestBody.put("State", 1);
        JsonObject res = protocol.put(requestBody);
        hasProgram = true;
        return res;
    }

    public JsonObject chargeBattery() {
        return executeOperation("MoveToChargerOperation");
    }

    public JsonObject moveToAssembly() {
        return executeOperation("MoveToAssemblyOperation");
    }

    public JsonObject moveToStorage() {
        return executeOperation("MoveToStorageOperation");
    }

    public JsonObject putAssembly() {
        return executeOperation("PutAssemblyOperation");
    }

    public JsonObject pickAssembly() {
        return executeOperation("PickAssemblyOperation");
    }

    public JsonObject pickWarehouse() {
        return executeOperation("PickWarehouseOperation");
    }

    public JsonObject putWarehouse() {
        return executeOperation("PutWarehouseOperation");
    }
}
