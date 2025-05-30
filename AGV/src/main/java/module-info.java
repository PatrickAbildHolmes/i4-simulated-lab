import dk.g4.st25.agv.AGV;
import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;

module AGV {
    requires Common;
    requires CommonMachine;
    requires CommonProtocol;
    requires com.google.gson;
    requires java.dotenv;
    provides MachineSPI with dk.g4.st25.agv.AGV;
}