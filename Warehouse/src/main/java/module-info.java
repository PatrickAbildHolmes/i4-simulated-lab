import dk.g4.st25.common.machine.MachineSPI;

module Warehouse {
    uses dk.g4.st25.common.protocol.ProtocolSPI;
    requires Common;
    requires CommonMachine;
    requires CommonProtocol;
    requires unirest.java;
    requires com.google.gson;
    requires Database;
    provides MachineSPI with dk.g4.st25.warehouse.Warehouse;
}