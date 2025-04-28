package dk.g4.st25.warehouse;

import com.google.gson.JsonObject;
import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.protocol.Protocol;
import dk.g4.st25.common.protocol.ProtocolSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.soap.SoapService;
//import kong.unirest.json.JSONObject;


import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class Warehouse implements MachineSPI, IExecuteCommand, IMonitorStatus {
    // SOAP service object for interacting with the warehouse system
    private final SoapService soapTest = new SoapService();
    // Counter to track the number of items successfully fetched
    private int itemsFetched = 0;

    @Override
    public int taskCompletion() {
        try {
            // Attempt to fetch an item from tray 1
            soapTest.pickItem(1);

            // Increment the counter if no exception is thrown
            itemsFetched++;
            System.out.println("Item successfully fetched from tray 1.");
            return 0;
        } catch (Exception e) {
            // Return a failure message
            System.err.println("Error fetching item from tray 1: " + e.getMessage());
            System.out.println("Failed to fetch item from tray 1.");
            return 0;
        }
    }

    @Override
    public int productionCompletion() {
        // Fetch the current inventory from the SOAP service
        JsonObject inventory = soapTest.getInventory();
        // Check if the number of fetched items matches the inventory size
        if (inventory != null && inventory.size() == itemsFetched) {
            System.out.println("All requested items have been fetched.");
            return 1; // Success
        } else {
            System.out.println("Not all items have been fetched yet.");
            return 0; // Incomplete
        }
    }

    @Override
    public JsonObject sendCommand(String commandType, String commandParam, String endpoint) {
//        // Handle the "refresh" command to refresh the inventory
//        if ("refresh".equalsIgnoreCase(commandType)) {
//            soapTest.refreshInventory();
//            return null; // Success
//        }
//        // Return -1 for unknown commands
//        return null;
        for (ProtocolSPI implementation : getProtocolSPIImplementationsList()) {
            if (implementation.getClass().getModule().getName().equals("SOAP")) {
                return implementation.readFrom(commandParam, endpoint);
            }
        }
        return null;
    }

    @Override
    public ArrayList<String> getCurrentSystemStatus() {
        // Create a list to store system status messages
        ArrayList<String> statusList = new ArrayList<>();
        statusList.add("Warehouse operational");
        statusList.add("Items fetched: " + itemsFetched);
        return statusList;
    }

    @Override
    public String getCurrentSystemStatus(String machineId) {
        // Return the status of the warehouse if the machine ID matches
        if ("Warehouse".equalsIgnoreCase(machineId)) {
            return "Warehouse operational, Items fetched: " + itemsFetched;
        }
        // Return an error message for unknown machine IDs
        return "Unknown machine ID";
    }

    @Override
    public String getCurrentProductionStatus() {
        // Fetch the current inventory from the SOAP service
        JsonObject inventory = soapTest.getInventory();
        // Return the number of items in the inventory if available
        if (inventory != null) {
            return "Items in inventory: " + inventory.size();
        }
        // Return an error message if the inventory cannot be fetched
        return "Unable to fetch inventory status";
    }

    @Override
    public ArrayList<String> getCurrentConnectionStatus() {
        // Create a list to store connection status messages
        ArrayList<String> connectionStatus = new ArrayList<>();
        connectionStatus.add("SOAP connection: Active");
        return connectionStatus;
    }

    @Override
    public String getCurrentConnectionStatus(String machineId) {
        // Return the connection status of the warehouse if the machine ID matches
        if ("Warehouse".equalsIgnoreCase(machineId)) {
            return "SOAP connection: Active";
        }
        // Return an error message for unknown machine IDs
        return "Unknown machine ID";
    }

    public Collection<? extends ProtocolSPI> getProtocolSPIImplementationsList() {
        return ServiceLoader.load(ProtocolSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}