module CommonMachine {
    requires Common;
    requires CommonProtocol;
    exports dk.g4.st25.common.machine;
    uses dk.g4.st25.common.machine.MachineSPI;
    provides dk.g4.st25.common.services.ICoordinate with dk.g4.st25.common.machine.Coordinator;
}