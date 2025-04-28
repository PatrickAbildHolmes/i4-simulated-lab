package dk.g4.st25.common.services;

import com.google.gson.JsonObject;

public interface IExecuteCommand {
    JsonObject sendCommand(String commandType, String commandParam, String endpoint);
}
