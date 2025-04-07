import dk.g4.st25.common.protocol.ProtocolSPI;

module SOAP {
    requires Common;
    requires CommonProtocol;
    requires com.google.gson;
    requires unirest.java;
    requires java.net.http;
    provides ProtocolSPI with dk.g4.st25.soap.SOAP;
}