import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.common.services.IScheduleProduction;

module AssemblyStation {
    requires Common;
    requires CommonMachine;
    requires CommonProtocol;
    provides MachineSPI with dk.g4.st25.assembly_station;
    provides IExecuteCommand with dk.g4.st25.assembly_station;
    provides IMonitorStatus with dk.g4.st25.assembly_station;
    provides IScheduleProduction with dk.g4.st25.assembly_station;
}