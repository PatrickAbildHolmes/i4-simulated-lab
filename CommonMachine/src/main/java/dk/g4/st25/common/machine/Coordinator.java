package dk.g4.st25.common.machine;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.g4.st25.common.protocol.ProtocolSPI;
import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class Coordinator implements ICoordinate{
    private final List<MachineSPI> objectList = new ArrayList<>();
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

        // Streams the protocol implementations here if any is found
        List<ProtocolSPI> protocolLoaderList = ServiceLoader.load(ProtocolSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
        System.out.println("LIST: " + protocolLoaderList);

        // Streams the machine implementations here if any is found
        List<MachineSPI> machineLoaderList = ServiceLoader.load(MachineSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
        System.out.println("MACHINE LIST: " + machineLoaderList);

        // Loops through all implementations of ProtocolSPI, and inserts into the correct machines
        for (ProtocolSPI protocol : protocolLoaderList) {
            String protocolName = protocol.getClass().getSimpleName().toLowerCase();
            System.out.println("PROTOCOL NAME: " + protocolName);
            for (MachineSPI machine : machineLoaderList) {
                String name = machine.getClass().getSimpleName().toLowerCase();
                System.out.println("NAME: " + name);
                switch (name) {
                    case "warehouse":
                        this.warehouse = machine;
                        this.warehouseFlag = true;
                        // Matches the .env variables to the corresponding protocol classnames, to ensure they exist.
                        if (protocolName.equals(Objects.requireNonNull(Dotenv.load().get("WAREHOUSE_PROTOCOL")).toLowerCase())) {
                            this.warehouse.setMachineProtocol(protocol);
                            Machine warehouseMachine = (Machine) this.warehouse;
                            System.out.println("Warehouse Protocol has now been set to: " + warehouseMachine.getProtocol());
                        } else {
                            System.out.println("Warehouse protocol defined in env, not found!");
                        }
                        break;
                    case "agv":
                        this.agvMachine = machine;
                        this.agvFlag = true;
                        if (protocolName.equals(Objects.requireNonNull(Dotenv.load().get("AGV_PROTOCOL")).toLowerCase())) {
                            this.agvMachine.setMachineProtocol(protocol);
                            Machine agvMachineMachine = (Machine) this.agvMachine;
                            System.out.println("AGV Protocol has now been set to: " + agvMachineMachine.getProtocol());
                        } else {
                            System.out.println("AGV protocol defined in env, not found!");
                        }
                        break;
                    case "assemblystation":
                        this.assemblyMachine = machine;
                        this.assemblyFlag = true;
                        if (protocolName.equals(Objects.requireNonNull(Dotenv.load().get("ASSEMBLY_PROTOCOL")).toLowerCase())) {
                            this.assemblyMachine.setMachineProtocol(protocol);
                            Machine assemblyMachineMachine = (Machine) this.assemblyMachine;
                            System.out.println("Assembly Protocol has now been set to: " + assemblyMachineMachine.getProtocol());
                        } else {
                            System.out.println("Assembly protocol defined in env, not found!");
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        if (!this.warehouseFlag) {System.err.println("Warehouse module is missing.");} else {Machine warehouseMachine = (Machine) this.warehouse;
            System.out.println("Warehouse Protocol now: " + warehouseMachine.getProtocol()); this.objectList.add(warehouse);}
        if (!this.agvFlag) {System.err.println("AGV module is missing.");} else {Machine agvMachineMachine = (Machine) this.agvMachine;
            System.out.println("AGV Protocol now: " + agvMachineMachine.getProtocol()); this.objectList.add(agvMachine);}
        if (!this.assemblyFlag) {System.err.println("AssemblyStation module is missing.");} else {Machine assemblyMachineMachine = (Machine) this.assemblyMachine;
            System.out.println("Assembly Protocol now: " + assemblyMachineMachine.getProtocol()); this.objectList.add(assemblyMachine);}
    }
    @Override
    public int getProduced(){
        /**
         * Returns the number of produced items in the current Order. Used in Core, to view status of current order.
         * */
        return this.produced;
    }

    @Override
    public JsonObject getMachineInventory(String machine) {
        switch (machine.toLowerCase()) {
            case "warehouse":
                return JsonParser.parseString(warehouse.getInventory()).getAsJsonObject();
            case "agv":
                return JsonParser.parseString(agvMachine.getInventory()).getAsJsonObject();
            case "assemblystation":
                return JsonParser.parseString(assemblyMachine.getInventory()).getAsJsonObject();
            default:
                System.out.println("Could not fetch inventory from the machine");
                return null;
        }
    }

    @Override
    public List<Object> getObjectList() {
        return new ArrayList<>(objectList);
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

        // Firstly check if a component is missing, and if there is, do not allow production
        if (!this.warehouseFlag || !this.agvFlag || !this.assemblyFlag) {
            System.out.println("A component is missing, cannot start production!");
            return 0;
        }

        Coordinator coordinator = new Coordinator();
        order.setStatus(Order.Status.BEING_PROCESSED);

        // Amount of products needed to be assembled, and parts needed for each product
        int amountOfProductsToAssemble = order.getAmount();
        this.produced = 0; // Reset to 0 at start of Order production cycle

        System.out.println("Production has been started successfully");

        // Initial sequence
        coordinator.step1_WarehouseWithdrawComponent();
        System.out.println("Step 1 completed!");
        coordinator.step2_AGVDeliverComponentToAssembly();
        System.out.println("Step 2 completed!");
        coordinator.step1_WarehouseWithdrawComponent();
        System.out.println("Step 1 completed! Again");

        // Afterwards, loops steps '3-4-5-2-1' for every product
        for (int i = 0; i < amountOfProductsToAssemble; i++) {
            coordinator.step3_AssemblyAssembleProduct();
            System.out.println("Step 3 Completed in loop! " + i);
            coordinator.step4_AGVDeliverProductToWarehouse();
            System.out.println("Step 4 Completed in loop! " + i);
            coordinator.step5_WarehouseDepositProduct();
            System.out.println("Step 5 Completed in loop! " + i);
            this.produced++;
            if(i == amountOfProductsToAssemble - 1){ // Don't prepare more components for assembly once enough drones have been produced
                break;
            }
            coordinator.step2_AGVDeliverComponentToAssembly();
            System.out.println("Step 2 Completed in loop! " + i);
            coordinator.step1_WarehouseWithdrawComponent();
            System.out.println("Step 1 Completed in loop! " + i);
        }
        order.setStatus(Order.Status.FINISHED);
        System.out.println("Order has been finished");

        // Returns 1 for success: All products produced.
        // Returns 0 for partial success: Only some products were produced.
        // Returns -1 for failure: No products were produced.
        return (this.produced == amountOfProductsToAssemble) ? 1 : (this.produced > 0 ? 0: -1);
    }
    public void step1_WarehouseWithdrawComponent(){
        int stepCount = 1;
        try {
            // 1.1 - 1.3) Warehouse places requested component into a tray
            if (machineCommand(this.warehouse, "pickItem")){stepCount++;}else{throw new Exception("Error handling warehouse command");}

            // 1.4) Warehouse moves the tray to the pickup area
            if (isMachineIdle(this.warehouse)) {stepCount++;}else{throw new Exception("Error getting warehouse state");}
            if (this.warehouse.taskCompletion()==1){stepCount++;}else{throw new Exception("Error completing task");}

        }catch (Exception e) {
            System.out.println("Failed at Step 1, action: "+stepCount); e.printStackTrace();
        }
    }
    public void step2_AGVDeliverComponentToAssembly(){
        int stepCount = 1;
        try {
            // 2.1-2.2) AGV receives 'component pick-up' command signal and moves to Warehouse
            if(machineCommand(this.agvMachine, "MoveToStorageOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}

            // 2.3) AGV sends 'movement complete' signal. Confirm position Warehouse
            if(actionCompletion(this.agvMachine)){stepCount++;}else{throw new Exception("Error completing task");}

            // 2.4-2.5) AGV receives pick-up signal. Load the program and execute
            if(machineCommand(this.agvMachine, "PickWarehouseOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}

            DroneComponent droneComponent = new DroneComponent(); // Create the withdrawn DroneComponent. Parameterless because no id
            this.agvMachine.setMostRecentlyReceived(droneComponent); // Give the withdrawn DroneComponent to the AGV. Needed for agvMachine.confirmItemDelivery()

            // 2.6) AGV sends 'confirm pick-up' signal. Confirm carrying item
            if (confirmItemDelivery(this.agvMachine)) {stepCount++;}else{throw new Exception("Error confirming item delivery");}
            if(actionCompletion(this.agvMachine)){stepCount++;}else{throw new Exception("Error completing task");}

            // 2.7-2.8) AGV receives movement instruction signal and moves to Assembly
            if(machineCommand(this.agvMachine, "MoveToAssemblyOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}

            // 2.9) AGV sends 'movement complete' signal. Confirm position Assembly
            if(actionCompletion(this.agvMachine)){stepCount++;}else{throw new Exception("Error completing task");}

            // 2.10) AGV delivers item to AssemblyLine. Load program and execute
            if(machineCommand(this.agvMachine, "PutAssemblyOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}
            this.assemblyMachine.setMostRecentlyReceived(droneComponent); // Pass the DroneComponent to the AssemblyLine
            System.out.println("Assembly Most recently recieved has been set");

            // 2.11) AGV sends task completion signal. Confirm not carrying item
            if(this.agvMachine.taskCompletion()!=1){stepCount++;}else{throw new Exception("Error completing task");}

        }catch (Exception e){
            System.out.println("Failed at Step 2, action: "+stepCount); e.printStackTrace();
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
            if(actionCompletion(this.assemblyMachine)){stepCount++;}else{throw new Exception("Error completing task");}

            // 3.8) AssemblyLine sends task completion signal (Instant)
            // .taskCompletion() returns 0 or 1, but it is not needed, since the previous step will leave AssemblyStation in the correct state
            if(this.assemblyMachine.taskCompletion()==1){throw new Exception("Error completing task");}

        } catch (Exception e){
            System.out.println("Failed at Step 3, action: "+stepCount); e.printStackTrace();
        }
    }

    public void step4_AGVDeliverProductToWarehouse(){
        int stepCount = 1;
        try {
            // 4.1 - 4.3A) Warehouse sends 'tray ready' signal
            if(isMachineIdle(this.warehouse)){stepCount++;}else{throw new Exception("Error fetching warehouse status");}

            // 4.3B-4B) AGV receives pick-up signal and moves to AssemblyLine
            if(machineCommand(this.agvMachine, "MoveToAssemblyOperation")){stepCount++;}else{throw new Exception("Error handling AGV command");}

            // 4.5B) AGV sends 'movement complete' signal. Confirm position Assembly
            if(actionCompletion(this.agvMachine)){stepCount++;}else{throw new Exception("Error completing task");}

            // 4.6B) AGV picks up item
            if(isMachineIdle(this.assemblyMachine)){stepCount++;}else{throw new Exception("Error getting machine status");}
            if(machineCommand(this.agvMachine, "PickAssemblyOperation")){stepCount++;}else{throw new Exception("Error handling AGV command");}

            Drone newDrone = new Drone(this.produced+1,"type"); // The Drone ID is the production number (Drone #3 has id=3)
            this.agvMachine.setMostRecentlyReceived(newDrone); // And give the drone to the AGV

            // Confirm carrying item
            if(actionCompletion(this.agvMachine)){
                stepCount++;
                this.assemblyMachine.actionCompletion(); // needed to clear exit tray. Think of it as "hand-off complete".
            }else{throw new Exception("Error completing task");}

            // 4.7B) AGV receives movement instructions and moves to Warehouse
            if(machineCommand(this.agvMachine, "MoveToStorageOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}

            // 4.8B) AGV sends 'movement complete' signal. Confirm position Warehouse
            if(actionCompletion(this.agvMachine)){stepCount++;}else{throw new Exception("Error completing task");}

            // 4.9) AGV delivers item to Warehouse
            if(machineCommand(this.agvMachine, "PutWarehouseOperation")){stepCount++;}else{throw new Exception("Error handling agv command");}
            this.warehouse.setMostRecentlyReceived(newDrone); // Pass the created drone on to Warehouse

            // 4.10) AGV sends task completion signal
            if(this.agvMachine.taskCompletion()!=1){stepCount++;}else{throw new Exception("Error completing task");}

        } catch (Exception e) {
            System.out.println("Failed at Step 4, action: "+stepCount); e.printStackTrace();
        }
    }
    public void step5_WarehouseDepositProduct(){
        int stepCount = 1;
        try {
            this.warehouse.setMostRecentlyReceived(new Drone(1, "transport"));
            // 5.1 - 5.2) Warehouse confirms correct item is delivered
            if (confirmItemDelivery(this.warehouse)) {stepCount++;}else{throw new Exception("Error confirming item delivery");}

            // 5.3) Warehouse stores item
            if (machineCommand(this.warehouse, "insertItem")){stepCount++;}else{throw new Exception("Error handling warehouse command");}

            // 5.4) Warehouse sends task completion signal
            if (isMachineIdle(this.warehouse)) {stepCount++;}else{throw new Exception("Error getting machine status");}
            if (this.warehouse.taskCompletion()==1){stepCount++;}else{throw new Exception("Error completing task");}

        }catch (Exception e) {
            System.out.println("Failed at Step 5, action: "+stepCount); e.printStackTrace();
        }
    }

    // Helper-methods:
    public boolean machineCommand(MachineSPI machine,String command){
        for (int i = 0; i < 5; i++) { // Attempt 5 times
            if (machine.sendCommand(command).get("status").getAsString()
                    .equals("Success!")){// and if it returns success
                System.out.println("COMMAND WAS SUCCESSFULLY RECIEVED");
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
    public boolean actionCompletion(MachineSPI machine){
        for (int i = 0; i < 5; i++) { // Try the I/O operation 5 times
            if(machine.actionCompletion() == 1){
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
        for (int i = 0; i < 5; i++) { // Try the I/O operation 5 times
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
    public boolean isMachineIdle(MachineSPI machine){
        for (int i = 0; i < 5; i++) { // Try the I/O operation 5 times
            String machineState = machine.getCurrentSystemStatus();
            System.out.println("STATE FROM MACHINE: " + machineState);
            if (machineState.equals("IDLE")) {
                return true;
            }else if (machineState.equals("ERROR") || machineState.equals("Unknown")) {
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
}
