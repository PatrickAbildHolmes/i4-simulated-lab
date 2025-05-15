package dk.g4.st25.common.machine;

import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;

public interface MachineSPI extends IExecuteCommand, IMonitorStatus, ItemConfirmationI {
    enum systemState{
    }
    // Signals that a task is complete
    int taskCompletion();


    // Signals when all tasks relating to a production are complete
    int productionCompletion();

    // Used by Coordinator when AGV hands off an item
    void setMostRecentlyReceived(Object mostRecentlyReceived);
}
