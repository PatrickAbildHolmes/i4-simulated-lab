import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.common.services.IScheduleProduction;

module AGV {
    requires Common;
    requires CommonMachine;
    provides MachineSPI with dk.g4.st25.agv;
    provides IExecuteCommand with dk.g4.st25.agv;
    provides IMonitorStatus with dk.g4.st25.agv;
    provides IScheduleProduction with dk.g4.st25.agv;
    requires REST;
    requires CommonProtocol;
    requires com.google.gson;
}