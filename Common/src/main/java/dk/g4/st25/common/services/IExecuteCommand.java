package dk.g4.st25.common.services;

public interface IExecuteCommand {
    int sendCommand(String commandType, String commandParam, String endpoint);
}
