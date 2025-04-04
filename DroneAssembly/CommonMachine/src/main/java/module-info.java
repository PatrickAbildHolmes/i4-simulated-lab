module CommonMachine {
    requires Common;
    requires CommonProtocol;
    exports dk.g4.st25.common.machine;
    uses dk.g4.st25.common.machine.MachineSPI;
}