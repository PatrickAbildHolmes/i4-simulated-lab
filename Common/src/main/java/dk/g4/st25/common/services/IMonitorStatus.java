package dk.g4.st25.common.services;
import java.util.ArrayList;

public interface IMonitorStatus {

    // Returns the state (activity) of the machines/machine that makes up the system
    ArrayList<String> getCurrentSystemStatus();
    String getCurrentSystemStatus(String machineId);

    // Returns production details
    String getCurrentProductionStatus();

    // Returns the connections to machines/machine
    ArrayList<String> getCurrentConnectionStatus();
    String getCurrentConnectionStatus(String machineId);

}
