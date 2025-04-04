import dk.g4.st25.common.machine.MachineSPI;

module AssemblyStation {
    requires Common;
    requires CommonMachine;
    provides MachineSPI with dk.g4.st25.assembly_station.AssemblyStation;
}