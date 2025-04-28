import dk.g4.st25.assembly_station.AssemblyStation;
import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.common.services.IScheduleProduction;

module AssemblyStation {
    requires Common;
    requires CommonMachine;
    requires CommonProtocol;
    requires com.google.gson;
    provides dk.g4.st25.common.services.IExecuteCommand with dk.g4.st25.assembly_station.AssemblyStation;
}