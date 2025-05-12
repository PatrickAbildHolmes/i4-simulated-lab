package dk.g4.st25.common.machine;

import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;
import java.util.ServiceLoader;

public class Coordinator implements ICoordinate {

    private IExecuteCommand warehouseExec;
    private MachineSPI warehouse;
    private IExecuteCommand agvExec;
    private MachineSPI agvMachine;
    private IExecuteCommand assemblyExec;
    private MachineSPI assemblyMachine;

    @Override
    public int startProduction(Order order) {
        /**
         * This method is called from Core, and will place the order in queue.
         * If no queue exists, it will instantiate the Coordinator singleton.
         * When the Coordinator singleton is instantiated, it will run the initial "1-2-1" sequence of:
         * 1 - Present new component for pickup if 2-or-more trays empty
         * 2 - Deliver product to Warehouse upon completion
         * 1 - Present new component for pickup if 2-or-more trays empty
         */

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

            switch (name) {
                case "warehouse":
                    warehouse = machine;
                    if (machine instanceof IExecuteCommand) {
                        warehouseExec = (IExecuteCommand) machine;
                    }
                case "agv":
                    agvMachine = machine;
                    if (machine instanceof IExecuteCommand) {
                        agvExec = (IExecuteCommand) machine;
                    }
                case "assembly":
                    assemblyMachine = machine;
                    if (machine instanceof IExecuteCommand) {
                        assemblyExec = (IExecuteCommand) machine;
                    }
                default:
                    break;
            }
        }

        if (warehouseExec == null || warehouse == null) {
            System.err.println("Warehouse module is missing.");
            return -1;
        }

        if (agvExec == null || agvMachine == null) {
            System.err.println("AGV module is missing.");
            return -1;
        }

        if (assemblyExec == null || assemblyMachine == null) {
            System.err.println("AssemblyStation module is missing.");
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

    @Override
    public ICoordinate getInstance() {
        return null;
    }

    public void warehouseWithdrawComponent(){
        /**
         * Runs through the actions described in Part 1 of the production sequence
         * 1.1) Warehouse receives "start production" command signal
         * 1.2) Warehouse checks at least 2 trays available
         * 1.2) Warehouse places requested component into a tray
         * 1.3) Warehouse moves the tray to the pickup area
         * 1.3) Warehouse sends task completion signal with item id
         */
        warehouse.taskCompletion();
    }
    public void AGVDeliverComponentToAssembly(){
        /**
         * Runs through the actions described in Part 2 of the production sequence
         * 2.1) AGV receives 'component pick-up' command signal
         * 2.2) AGV moves to warehouse position
         * 2.3) AGV sends 'movement complete' signal
         * 2.4) AGV receives pick-up signal
         * 2.5) AGV picks up item
         * 2.6) AGV sends 'confirm pick-up' signal
         * 2.7) AGV receives movement instruction signal
         * 2.8) AGV moves to AssemblyLine position
         * 2.9) AGV sends 'movement complete' signal
         * 2.10) AGV delivers item to AssemblyLine
         * 2.11) AGV sends task completion signal
         */

    }
    public void AssemblyAssembleProduct(){
        /**
         * Runs through the actions described in Part 3 of the production sequence
         * 3.1) AssemblyLine receives "execute assembly" command signal
         * 3.2) AssemblyLine confirms correct item is delivered
         * 3.3) AssemblyLine sends confirmation signal
         * 3.4) AssemblyLine confirms enough items have been delivered
         * 3.5) AssemblyLine sends confirmation signal
         * 3.6) AssemblyLine executes the assembly instructions
         * 3.7) AssemblyLine places product for pick-up
         * 3.8) AssemblyLine sends task completion signal
         */

    }
    public void AGVDeliverProductToWarehouse(){
        /**
         * Runs through the actions described in Part 4 of the production sequence
         * 4.1) Warehouse receives “prepare” command signal
         * 4.2) Warehouse confirms tray available
         * 4.3A) Warehouse prepares storage tray
         * 4.3A) Warehouse sends 'tray ready' signal
         * 4.3B) AGV receives pick-up signal
         * 4.4B) AGV moves to AssemblyLine position
         * 4.5B) AGV sends 'movement complete' signal
         * 4.6B) AGV picks up item
         * 4.7B) AGV receives movement instructions
         * 4.8B) AGV moves to Warehouse
         * 4.9) AGV delivers item to Warehouse
         * 4.10) AGV sends task completion signal
         */

    }
    public void warehouseDepositProduct(){
        /**
         * Runs through the actions described in Part 5 of the production sequence
         * 5.1) Warehouse receives "deposit" command signal
         * 5.1) Warehouse confirms correct item is delivered
         * 5.2) Warehouse sends confirmation signal
         * 5.3) Warehouse stores item
         * 5.4) Warehouse sends task completion signal
         */

    }

    /*
    Coordinator/production rhythm (from README.md)

Derived rhythm:
Start: 1-2-1
Loop: 3-4-5-2-1     3-4-5-2-1     3-4-5-2-1

Derived rules, in priority:
* Present new component for pickup if 2-or-more trays empty
* Deliver product to Warehouse upon completion
* Always deposit product into warehouse upon delivery
* Deliver component to Assembly if more components needed

Sequence (actions) with checks:
(Each step within an action requires previous steps are successfully completed)

1) ---------Warehouse withdraws component---------
1.1) Warehouse receives "start production" command signal
1.2) Warehouse checks at least 2 trays available
1.2) Warehouse places requested component into a tray
1.3) Warehouse moves the tray to the pickup area
1.3) Warehouse sends task completion signal with item id

2) ---------AGV picks up component and delivers to Assembly---------
2.1) AGV receives 'component pick-up' command signal
2.2) AGV moves to warehouse position
2.3) AGV sends 'movement complete' signal
2.4) AGV receives pick-up signal
2.5) AGV picks up item
2.6) AGV sends 'confirm pick-up' signal
2.7) AGV receives movement instruction signal
2.8) AGV moves to AssemblyLine position
2.9) AGV sends 'movement complete' signal
2.10) AGV delivers item to AssemblyLine
2.11) AGV sends task completion signal

3) ---------Assembly assemble product---------
3.1) AssemblyLine receives "execute assembly" command signal
3.2) AssemblyLine confirms correct item is delivered
3.3) AssemblyLine sends confirmation signal
3.4) AssemblyLine confirms enough items have been delivered
3.5) AssemblyLine sends confirmation signal
3.6) AssemblyLine executes the assembly instructions
3.7) AssemblyLine places product for pick-up
3.8) AssemblyLine sends task completion signal

4) ---------AGV picks up product and delivers to Warehouse---------
4.1) Warehouse receives “prepare” command signal
4.2) Warehouse confirms tray available
4.3A) Warehouse prepares storage tray
4.3A) Warehouse sends 'tray ready' signal
4.3B) AGV receives pick-up signal
4.4B) AGV moves to AssemblyLine position
4.5B) AGV sends 'movement complete' signal
4.6B) AGV picks up item
4.7B) AGV receives movement instructions
4.8B) AGV moves to Warehouse
4.9) AGV delivers item to Warehouse
4.10) AGV sends task completion signal

5) ---------Warehouse deposits product---------
5.1) Warehouse receives "deposit" command signal
5.1) Warehouse confirms correct item is delivered
5.2) Warehouse sends confirmation signal
5.3) Warehouse stores item
5.4) Warehouse sends task completion signal

    */
}

