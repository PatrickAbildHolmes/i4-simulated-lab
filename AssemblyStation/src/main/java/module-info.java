import dk.g4.st25.common.services.IExecuteCommand;

module AssemblyStation {
    requires Common;
    requires CommonMachine;
    requires CommonProtocol;
    requires com.google.gson;
    provides IExecuteCommand with dk.g4.st25.assembly_station.AssemblyStation;
}