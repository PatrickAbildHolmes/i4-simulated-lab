package dk.g4.st25.assembly_station;

import dk.g4.st25.common.machine.Drone;
import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.util.Connection;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.common.services.IScheduleProduction;
import dk.g4.st25.common.util.Part;

import java.util.ArrayList;
import java.util.HashMap;

public class AssemblyStation implements MachineSPI, IMonitorStatus, IExecuteCommand, IScheduleProduction {
    private Connection connection; // instantiate from a chosen protocol - for AssemblyStation, from MQTT
    private HashMap<String, Integer> productionStatus; // how many items have been processed, out of how many
    private SystemStatus systemStatus; // What it is currently doing (producing, idle, etc.)
    private String command; // Latest received command
    private ArrayList<Part> inventory;
    private Drone product; // Assembles products, in our case, Drones
    private Tray[] trays; // Trays for delivery and pick-up. Fixed number

    private Boolean NeedsMoreComponents;
    private Boolean ProductReadyForPickup;
    private Boolean AvailableTray;

    // From README section: 'Sequence (actions) with checks'
    //3) Assembly assemble product
    //3.1) AssemblyLine receives "execute assembly" command signal
    //3.2) AssemblyLine confirms correct item is delivered
    //3.3) AssemblyLine sends confirmation signal
    //3.4) AssemblyLine confirms enough items have been delivered
    //3.5) AssemblyLine sends confirmation signal
    //3.6) AssemblyLine executes the assembly instructions
    //3.7) AssemblyLine places product for pick-up
    //3.8) AssemblyLine sends task completion signal

    AssemblyStation(Connection connection) {
        this.connection = connection;
        this.productionStatus = new HashMap<>();
        this.systemStatus = SystemStatus.IDLE;
        this.command = "";
        this.inventory = new ArrayList<>();
        this.trays = new Tray[2]; // Two trays?
        this.NeedsMoreComponents = true;
        this.ProductReadyForPickup = false;
        this.AvailableTray = true;
    }

    @Override
    public int taskCompletion() {
        // Signals that a task is complete
        // Only used internally in class
        // ---- we should change method to return int ----
        return 0;
    }

    @Override
    public int productionCompletion() {
        // Signals when all tasks relating to a production are complete
        return 0;
    }

    @Override
    public int sendCommand(String commandType, String commandParam, String endpoint) {
        //
        return 0;
    }

    @Override
    public ArrayList<String> getCurrentSystemStatus() {
        //
        return null;
    }

    @Override
    public String getCurrentSystemStatus(String machineId) {
        //
        return "";
    }

    @Override
    public String getCurrentProductionStatus() {
        //
        return "";
    }

    @Override
    public ArrayList<String> getCurrentConnectionStatus() {
        //
        return null;
    }

    @Override
    public String getCurrentConnectionStatus(String machineId) {
        //
        return "";
    }

    @Override
    public int addProductionToQueue(String product, int amount, ArrayList<Object> itemsNeeded) {
        //
        return 0;
    }

    @Override
    public ArrayList<Object> getProductionQueue() {
        //
        return null;
    }

    @Override
    public Object getProductionQueue(String id) {
        //
        return null;
    }
}

