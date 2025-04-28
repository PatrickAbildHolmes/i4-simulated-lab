module CommonProtocol {
    requires com.google.gson;
    requires Common;
    exports dk.g4.st25.common.protocol;
    uses dk.g4.st25.common.protocol.ProtocolSPI;
    opens dk.g4.st25.common.protocol to com.google.gson;
}