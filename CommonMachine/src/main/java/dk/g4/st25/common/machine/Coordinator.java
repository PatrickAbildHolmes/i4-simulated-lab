package dk.g4.st25.common.machine;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;
import java.util.ServiceLoader;

public class Coordinator implements ICoordinate {

    private MachineSPI warehouse;
    private MachineSPI agvMachine;
    private MachineSPI assemblyMachine;
    private boolean warehouseFlag = false;
    private boolean agvFlag = false;
    private boolean assemblyFlag = false;

    public Coordinator(){ // Constructor must be public because of ServiceLoader
        /**
         * .startProduction() creates a single instance of Coordinator,
         * which in turn calls the ServiceLoader to create instances of the component machines.
         * Also have flags to check that the machine is attached, so that it does not end up in
         * an infinite loop by mistake.
         * Flags must be checked every production cycle during runtime,
         * since components can/may be added or removed during runtime.
         */
        ServiceLoader<MachineSPI> loader = ServiceLoader.load(MachineSPI.class);
        for (MachineSPI machine : loader) {
            String name = machine.getClass().getSimpleName().toLowerCase();
            switch (name) {
                case "warehouse":
                    this.warehouse = machine;
                    this.warehouseFlag = true;
                case "agv":
                    this.agvMachine = machine;
                    this.agvFlag = true;
                case "assembly":
                    this.assemblyMachine = machine;
                    this.assemblyFlag = true;
                default:
                    break;
            }
        }
        if (!this.warehouseFlag) {System.err.println("Warehouse module is missing.");}
        if (!this.agvFlag) {System.err.println("AGV module is missing.");}
        if (!this.assemblyFlag) {System.err.println("AssemblyStation module is missing.");}
    }

    @Override
    public int startProduction(Order order) {
        /**
         * This method is called from Core, and will start producing the amount of Drones specified by the order.
         *
         * When the Coordinator singleton is instantiated, it will run the initial "1-2-1" sequence of:
         * 1 - Present new component for pickup if 2-or-more trays empty
         * 2 - Deliver product to Warehouse upon completion
         * 1 - Present new component for pickup if 2-or-more trays empty
         */
        Coordinator coordinator = new Coordinator();
        order.setStatus(Order.Status.BEING_PROCESSED);

        // Amount of products needed to be assembled, and parts needed for each product
        int amountOfProductsToAssemble = order.getAmount();
        int amountOfPartsNeeded = order.getProduct().getParts().length;
        // Initial sequence
        // Put each inside a check for component availability, AND don't continue until it returns YES
        for (int k = 0; k < amountOfPartsNeeded; k++) {
            coordinator.step1_WarehouseWithdrawComponent();
            coordinator.step2_AGVDeliverComponentToAssembly();
        }
        coordinator.step1_WarehouseWithdrawComponent();
        // Afterwards, loops steps '3-4-5-2-1' for every product
        for (int i = 0; i < amountOfProductsToAssemble; i++) {
            coordinator.step3_AssemblyAssembleProduct();
            coordinator.step4_AGVDeliverProductToWarehouse();
            coordinator.step5_WarehouseDepositProduct();
            if(i == amountOfProductsToAssemble - 1){ // Don't prepare more components for assembly once enough drones have been produced
                break;
            }
            for (int k = 0; k < amountOfPartsNeeded; k++) {
                coordinator.step2_AGVDeliverComponentToAssembly();
                coordinator.step1_WarehouseWithdrawComponent();
            }
        }

        order.setStatus(Order.Status.FINISHED);
        // Returns 1 for success: All products produced.
        // Returns 0 for partial success: Only some products were produced.
        // Returns -1 for failure: No products were produced.
        return 1;
    }

    public boolean step1_WarehouseWithdrawComponent(){
        /**
         * Runs through the actions described in Part 1 of the production sequence
         * 1.1) Warehouse receives "start production" command signal
         * 1.2) Warehouse checks at least 2 trays available
         * 1.2) Warehouse places requested component into a tray
         * 1.3) Warehouse moves the tray to the pickup area
         * 1.3) Warehouse sends task completion signal with item id
         */

        this.warehouse.taskCompletion();
        return true;
    }
    public boolean step2_AGVDeliverComponentToAssembly(){
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
        return true;
    }
    public boolean step3_AssemblyAssembleProduct(){
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
        return true;
    }
    public boolean step4_AGVDeliverProductToWarehouse(){
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
        return true;
    }
    public boolean step5_WarehouseDepositProduct(){
        /**
         * Runs through the actions described in Part 5 of the production sequence
         * 5.1) Warehouse receives "deposit" command signal
         * 5.1) Warehouse confirms correct item is delivered
         * 5.2) Warehouse sends confirmation signal
         * 5.3) Warehouse stores item
         * 5.4) Warehouse sends task completion signal
         */
        return true;
    }
}

