import dk.g4.st25.common.protocol.ProtocolSPI;

module REST {
    requires Common;
    exports dk.g4.st25.rest;
    requires CommonProtocol;
    requires com.google.gson;
    requires java.dotenv;
    requires spring.web;
    provides ProtocolSPI with dk.g4.st25.rest.REST;
}