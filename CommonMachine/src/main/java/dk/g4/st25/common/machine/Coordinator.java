package dk.g4.st25.common.machine;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;

import java.util.Random;
import java.util.ServiceLoader;
import java.util.ArrayList;
import java.util.List;

public class Coordinator implements ICoordinate{

    private final List<Object> objectList = new ArrayList<Object>();
    private MachineSPI warehouse;
    private MachineSPI agvMachine;
    private MachineSPI assemblyMachine;
    private boolean warehouseFlag = false;
    private boolean agvFlag = false;
    private boolean assemblyFlag = false;
    private int produced = 0; // Initialize to 0 to avoid errors

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
    public int getProduced(){
        /**
         * Returns the number of produced items in the current Order. Used in Core, to view status of current order.
         * */
        return this.produced;
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
        this.produced = 0; // Reset to 0 at start of Order production cycle
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
            this.produced++;
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
    public void step1_WarehouseWithdrawComponent(){
        int stepCount = 1;
        try {
            // 1.1 - 1.3) Warehouse places requested component into a tray
            if (machineCommand(this.warehouse, "pickItem")){stepCount++;}else{throw new Exception("Error handling warehouse command");}

            // 1.4) Warehouse moves the tray to the pickup area
            if (getMachineStatus(this.warehouse)) {stepCount++;}else{throw new Exception("Error getting warehouse state");}
            if (this.warehouse.taskCompletion()!=1){stepCount++;}else{throw new Exception("Error completing task");}
        }catch (Exception e) {
            System.out.println("Failed at Step 4, action: "+stepCount);
            e.printStackTrace();
        }
    }

    public void step2_AGVDeliverComponentToAssembly(){
        int stepCount = 1;
        try {
            // 2.1-2.2) AGV receives 'component pick-up' command signal and moves to Warehouse
            if(machineCommand(this.agvMachine, "MoveToStorageOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}

            // 2.3) AGV sends 'movement complete' signal
            // Confirm position Warehouse
            if(agvMinorTaskComplete()){stepCount++;}else{throw new Exception("Error completing task");}

            // 2.4-2.5) AGV receives pick-up signal
            //Load program and execute
            if(machineCommand(this.agvMachine, "PickWarehouseOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}
            this.agvMachine.setMostRecentlyReceived(new DroneComponent());

            // 2.6) AGV sends 'confirm pick-up' signal
            // Confirm carrying item
            if(agvMinorTaskComplete()){stepCount++;}else{throw new Exception("Error completing task");}

            // 2.7-2.8) AGV receives movement instruction signal and moves to Assembly
            if(machineCommand(this.agvMachine, "MoveToAssemblyOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}
            // 2.9) AGV sends 'movement complete' signal
            // Confirm position Assembly
            if(agvMinorTaskComplete()){stepCount++;}else{throw new Exception("Error completing task");}

            // 2.10) AGV delivers item to AssemblyLine
            //Load program and execute
            if(machineCommand(this.agvMachine, "PutAssemblyOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}
            this.assemblyMachine.setMostRecentlyReceived(new DroneComponent());

            // 2.11) AGV sends task completion signal
            // Confirm not carrying item
            if(this.agvMachine.taskCompletion()!=1){stepCount++;}else{throw new Exception("Error completing task");}
        }catch (Exception e){
            System.out.println("Failed at Step 2, action: "+stepCount);
            e.printStackTrace();
        }
    }
    public void step3_AssemblyAssembleProduct(){
        int stepCount = 1;
        try {
            // 3.1-3.3) AssemblyLine confirms correct item is delivered (Instant)
            if (confirmItemDelivery(this.assemblyMachine)) {stepCount++;}else{throw new Exception("Error confirming item delivery");}
            // 3.4-3.6) AssemblyLine confirms enough items have been delivered, and executes the assembly instructions (Instant)
            if (machineCommand(this.assemblyMachine, "assemble")){stepCount++;}else{throw new Exception("Error handling assembly station command");}
            // 3.7 - 3.8) AssemblyLine places product for pick-up (Waiting time)
            if(getMachineStatus(this.assemblyMachine)){stepCount++;}else{throw new Exception("Error getting machine status");}
            // 3.8) AssemblyLine sends task completion signal (Instant)
            // .taskCompletion() returns 0 or 1, but it is not needed, since the previous step will leave AssemblyStation in the correct state
            if(this.assemblyMachine.taskCompletion()!=1){throw new Exception("Error completing task");}

        } catch (Exception e){
            System.out.println("Failed at Step 3, action: "+stepCount);
            e.printStackTrace();
        }

    }

    public void step4_AGVDeliverProductToWarehouse(){
        int stepCount = 1;
        try {
            // 4.1 - 4.3A) Warehouse sends 'tray ready' signal
            if(getMachineStatus(this.warehouse)){stepCount++;}else{throw new Exception("Error fetching warehouse status");}

            // 4.3B-4B) AGV receives pick-up signal and moves to AssemblyLine
            if(machineCommand(this.agvMachine, "MoveToAssemblyOperation")){stepCount++;}else{throw new Exception("Error handling AGV command");}

            // 4.5B) AGV sends 'movement complete' signal
            // Confirm position Assembly
            if(agvMinorTaskComplete()){stepCount++;}else{throw new Exception("Error completing task");}

            // 4.6B) AGV picks up item
            if(getMachineStatus(this.assemblyMachine)){stepCount++;}else{throw new Exception("Error getting machine status");}
            if(machineCommand(this.agvMachine, "PickAssemblyOperation")){stepCount++;}else{throw new Exception("Error handling AGV command");}
            Drone newDrone = new Drone(generateUnboundedRandomHexUsingRandomNextInt());
            this.agvMachine.setMostRecentlyReceived(newDrone);
            // Confirm carrying item
            if(agvMinorTaskComplete()){stepCount++;}else{throw new Exception("Error completing task");}

            // 4.7B) AGV receives movement instructions and moves to Warehouse
            if(machineCommand(this.agvMachine, "MoveToStorageOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}

            // 4.8B) AGV sends 'movement complete' signal
            // Confirm position Warehouse
            if(agvMinorTaskComplete()){stepCount++;}else{throw new Exception("Error completing task");}

            // 4.9) AGV delivers item to Warehouse
            if(machineCommand(this.agvMachine, "PutWarehouseOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}
            this.warehouse.setMostRecentlyReceived(newDrone);

            // 4.10) AGV sends task completion signal
            if(this.agvMachine.taskCompletion()!=1){stepCount++;}else{throw new Exception("Error completing task");}
        } catch (Exception e) {
            System.out.println("Failed at Step 4, action: "+stepCount);
            e.printStackTrace();
        }
    }
    public void step5_WarehouseDepositProduct(){
        int stepCount = 1;
        try {
            // 5.1 - 5.2) Warehouse confirms correct item is delivered
            if (confirmItemDelivery(this.warehouse)) {stepCount++;}else{throw new Exception("Error confirming item delivery");}

            // 5.3) Warehouse stores item
            if (machineCommand(this.warehouse, "insertItem")){stepCount++;}else{throw new Exception("Error handling warehouse command");}

            // 5.4) Warehouse sends task completion signal
            if (getMachineStatus(this.warehouse)) {stepCount++;}else{throw new Exception("Error getting machine status");}
            if (this.warehouse.taskCompletion()!=1){stepCount++;}else{throw new Exception("Error completing task");}
        }catch (Exception e) {
            System.out.println("Failed at Step 5, action: "+stepCount);
            e.printStackTrace();
        }
    }


    // helper-methods:
    String generateUnboundedRandomHexUsingRandomNextInt() { // Used to generate drone ID/names
        Random random = new Random();
        int randomInt = random.nextInt();
        return Integer.toHexString(randomInt);
    }
    public boolean machineCommand(MachineSPI machine,String command){
        for (int i = 0; i < 5; i++) { // Attempt 5 times
            if (machine.sendCommand(command)         //Load program and execute
                    .has("Success!")){   // and if it returns success
                return true;                         // Then move on
            }
            else {
                try{
                    Thread.sleep(5000); // Wait 5 seconds between each attempt if unsuccessful
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }
    public boolean agvMinorTaskComplete(){
        for (int i = 0; i < 5; i++) { // Confirm position Assembly
            if(this.agvMachine.productionCompletion() == 1){
                return true;
            }else{
                try{
                    Thread.sleep(2000); // Wait 2 seconds between each attempt if unsuccessful
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }
    public boolean confirmItemDelivery(MachineSPI machine){
        for (int i = 0; i < 5; i++) {
            if(machine.confirmItemDelivery()){
                return true;
            }else{
                try{
                    Thread.sleep(1000); // Wait 1 second between each attempt
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }
    public boolean getMachineStatus(MachineSPI machine){
        for (int i = 0; i < 5; i++) { // Attempt 5 times
            String machineState = machine.getCurrentSystemStatus();
            if (machineState.equals("Idle")) {
                return true;
            }else if (machineState.equals("Error") || machineState.equals("Unknown")) {
                System.out.println("An Error occurred while assembling the product");
                return false;
            }else {
                try{
                    Thread.sleep(5000); // Wait 5 seconds between each attempt
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }
    @Override
    public List<Object> getObjectList() {
        return objectList;
    }
}
