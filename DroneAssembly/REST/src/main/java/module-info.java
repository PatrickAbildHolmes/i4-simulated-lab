import dk.g4.st25.common.protocol.ProtocolSPI;

module REST {
    requires Common;
    requires CommonProtocol;
    requires com.google.gson;
    provides ProtocolSPI with dk.g4.st25.rest.REST;
}