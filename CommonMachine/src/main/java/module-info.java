module CommonMachine {
    requires Common;
    requires CommonProtocol;
    requires com.google.gson;
    requires java.dotenv;
    exports dk.g4.st25.common.machine;
    uses dk.g4.st25.common.machine.MachineSPI;
    uses dk.g4.st25.common.protocol.ProtocolSPI;
    provides dk.g4.st25.common.services.ICoordinate with dk.g4.st25.common.machine.Coordinator;
}