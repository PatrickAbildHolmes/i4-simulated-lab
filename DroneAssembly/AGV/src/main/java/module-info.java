import dk.g4.st25.common.machine.MachineSPI;

module AGV {
    requires Common;
    requires CommonMachine;
    provides MachineSPI with dk.g4.st25.agv.AGV;
}