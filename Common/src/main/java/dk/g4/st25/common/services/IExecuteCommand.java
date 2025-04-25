package dk.g4.st25.common.services;

public interface IExecuteCommand {
    int sendCommand(String commandType, String commandParam);
    // endpoint (where the command is sent, that is, the "physical" machine) is found in the instance of Protocol
}
