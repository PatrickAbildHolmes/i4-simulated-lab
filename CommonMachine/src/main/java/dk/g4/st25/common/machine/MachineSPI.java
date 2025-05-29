package dk.g4.st25.common.machine;
import dk.g4.st25.common.protocol.ProtocolSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;

public interface MachineSPI extends IExecuteCommand, IMonitorStatus, ItemConfirmationI {
    enum systemState{
    }
    /**
     * This method is used to verify that the sequence of actions within the (Coordinator/production) step is complete
     */
    int taskCompletion();

    /**
     * This method is used to verify that the latest action (move, pick up, present object) is finished
     * */
    int actionCompletion();

    /**
     * Ideally this method is used to handle object drop-off, since an object can be passed (in Coordinator) through this method,
     * I.E. from Warehouse->AGV->Assembly->AGV->Warehouse
     * */
    void setMostRecentlyReceived(Object mostRecentlyReceived);

    /**
     * This method sets the protocol through which the machine should communicate with its commands.
     * @param protocol is the protocol that the machine will be using.
     */
    void setMachineProtocol(ProtocolSPI protocol);
}
