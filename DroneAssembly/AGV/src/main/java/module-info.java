import dk.g4.st25.common.machine.MachineSPI;

module AGV {
    requires CommonMachine;
    requires REST;
    requires CommonProtocol;
    requires com.google.gson;
}