package dk.g4.st25.assembly_station;

import dk.g4.st25.common.machine.Drone;
import dk.g4.st25.common.machine.ItemConfirmationI;
import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.machine.Tray;
import dk.g4.st25.common.protocol.Protocol;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.common.util.Part;

import java.util.ArrayList;

public class AssemblyStation implements MachineSPI, IMonitorStatus, IExecuteCommand, ItemConfirmationI {
    private Protocol protocol; // instantiate from a chosen protocol - for AssemblyStation, from MQTT
    private SystemStatus systemStatus; // What it is currently doing (producing, idle, etc.)
    private String command; // Latest received command
    private ArrayList<Part> inventory;
    private Drone product; // Assembles products, in our case, Drones
    private Tray[] trays; // Trays for delivery and pick-up. Fixed number

    private Boolean NeedsMoreComponents;
    private Boolean ProductReadyForPickup;
    private Boolean AvailableTray;

    AssemblyStation(Protocol protocol) {
        this.protocol = protocol;
        this.systemStatus = SystemStatus.IDLE;
        this.command = "";
        this.inventory = new ArrayList<>();
        this.trays = new Tray[2]; // Two trays?
        this.NeedsMoreComponents = true;
        this.ProductReadyForPickup = false;
        this.AvailableTray = true;
    }

    // From README section: 'Sequence (actions) with checks'
    //3) Assembly assemble product
    //3.1) AssemblyLine receives "execute assembly" command signal-----
    //3.2) AssemblyLine confirms correct item is delivered
    //3.3) AssemblyLine sends confirmation signal to coordinator
    //3.4) AssemblyLine confirms enough items have been delivered
    //3.5) AssemblyLine sends confirmation signal to coordinator
    //3.6) AssemblyLine executes the assembly instructions
    //3.7) AssemblyLine places product for pick-up
    //3.8) AssemblyLine sends task completion signal

    @Override
    public int sendCommand(String commandType, String commandParam) {
        // Return statement acknowledges that command was successfully sent (taskCompletion - that it was successfully completed)
        // get endpoint from instance of protocol on the this object
        // method for endpoint-to-machine
//        return assemblyStation.executeCommand(commandType, commandParam);
        return 0;
    }

    @Override
    public boolean confirmItemDelivery() {
        // Confirm that correct item type was delivered
        //      AssemblyStation will only every receive parts,
        //      and Warehouse only every products.
        // Called by coordinator. If true, check Item Quantity
        return true;
    }

    public boolean confirmItemQuantity() {
        return true;
    }

    @Override
    public String getCurrentSystemStatus() {
        //
        return "";
    }

    @Override
    public String getCurrentConnectionStatus() {
        //
        return "";
    }

    @Override
    public int productionCompletion() {
        // Signals when all tasks relating to a production are complete

        return 0;
    }

    // ------- Redundant -------
    @Override
    public int taskCompletion() {
        // Signals that a task (assemble 1 product) is complete
        // Superseded by sendCommand

        return 1;
    }

}

