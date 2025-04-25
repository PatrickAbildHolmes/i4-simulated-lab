import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.common.services.IScheduleProduction;

module AGV {
    requires Common;
    requires CommonMachine;
    requires CommonProtocol;
    requires com.google.gson;
    requires REST;
}