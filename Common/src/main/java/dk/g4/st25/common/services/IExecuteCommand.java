package dk.g4.st25.common.services;

import com.google.gson.JsonObject;

public interface IExecuteCommand {
    /**
     * Primary method to order a machine to "do something", e.g.: "assemble" to AssemblyStation, or "MoveToWarehouse" to AGV.
     */
    JsonObject sendCommand(String commandType);
}
