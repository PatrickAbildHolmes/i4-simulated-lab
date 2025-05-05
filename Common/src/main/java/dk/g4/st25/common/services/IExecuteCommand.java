package dk.g4.st25.common.services;

import com.google.gson.JsonObject;

public interface IExecuteCommand {
    JsonObject sendCommand(String commandType, String commandParam);
    // endpoint (where the command is sent, that is, the "physical" machine) is found in the instance of Protocol
}
