import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;

module AssemblyStation {
    requires Common;
    requires CommonMachine;
    requires CommonProtocol;
    requires com.google.gson;
    provides MachineSPI with dk.g4.st25.assembly_station.AssemblyStation;
}