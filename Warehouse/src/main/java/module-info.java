import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.services.IExecuteCommand;

module Warehouse {
    uses dk.g4.st25.common.protocol.ProtocolSPI;
    requires Common;
    requires CommonMachine;
    requires CommonProtocol;
    requires unirest.java;
    requires com.google.gson;
    provides MachineSPI with dk.g4.st25.warehouse.Warehouse;
    provides IExecuteCommand with dk.g4.st25.warehouse.Warehouse;

}