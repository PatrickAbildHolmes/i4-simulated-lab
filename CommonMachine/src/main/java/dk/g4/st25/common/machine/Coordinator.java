package dk.g4.st25.common.machine;

import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;

import java.util.ServiceLoader;

public class Coordinator {

    public int startProduction() {
        IExecuteCommand warehouseExec = null;
        MachineSPI warehouse = null;
        IExecuteCommand agvExec = null;
        MachineSPI agvMachine = null;
        IExecuteCommand assemblyExec = null;
        MachineSPI assemblyMachine = null;

        ServiceLoader<MachineSPI> loader = ServiceLoader.load(MachineSPI.class);
        System.out.println(loader);
        for (MachineSPI machine : loader) {
            String name = machine.getClass().getSimpleName().toLowerCase();

            if (name.contains("warehouse")) {
                warehouse = machine;
                if (machine instanceof IExecuteCommand) {
                    warehouseExec = (IExecuteCommand) machine;
                }
            } else if (name.contains("agv")) {
                agvMachine = machine;
                if (machine instanceof IExecuteCommand) {
                    agvExec = (IExecuteCommand) machine;
                }
            } else if (name.contains("assembly")) {
                assemblyMachine = machine;
                if (machine instanceof IExecuteCommand) {
                    assemblyExec = (IExecuteCommand) machine;
                }
            }
        }

        if (warehouseExec == null || warehouse == null || agvExec == null || agvMachine == null || assemblyExec == null || assemblyMachine == null) {
            System.err.println("Required machines or services are missing.");
            return -1;
        }

        // Possibility of Threading here

        // Production cycle:
        warehouseExec.sendCommand("writeTo", "pickItem", "Thing");
        agvExec.sendCommand("writeTo", "MoveToStorageOperation");
        agvExec.sendCommand("writeTo", "PickWarehouseOperation");
        agvExec.sendCommand("writeTo", "MoveToAssemblyOperation");
        agvExec.sendCommand("writeTo", "PutAssemblyOperation");

        Machine agvMachineMachine = (Machine) agvMachine;
//        agvMachineMachine.systemStatus

        return 0;
    }
}


