import dk.g4.st25.common.protocol.ProtocolSPI;

module SOAP {
    exports dk.g4.st25.soap;
    requires Common;
    requires CommonProtocol;
    requires com.google.gson;
    requires unirest.java;
    requires java.desktop;
    provides ProtocolSPI with dk.g4.st25.soap.SOAP;
}