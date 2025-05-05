package dk.g4.st25.warehouse;

import com.google.gson.JsonObject;
import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.protocol.ProtocolSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.soap.SoapService;


import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class Warehouse implements MachineSPI, IExecuteCommand, IMonitorStatus {
    // SOAP service object for interacting with the warehouse system
    private final SoapService soapTest = new SoapService();
    // Counter to track the number of items successfully fetched
    private int itemsFetched = 0;
    private final String endpoint = "http://localhost:8081/Service.asmx";

    @Override
    public int taskCompletion() {
        try {
            // Attempt to fetch an item from tray 1
            soapTest.pickItem(1, endpoint);

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
//        // Fetch the current inventory from the SOAP service
//        JsonObject inventory = soapTest.getInventory();
//        // Check if the number of fetched items matches the inventory size
//        if (inventory != null && inventory.size() == itemsFetched) {
//            System.out.println("All requested items have been fetched.");
//            return 1; // Success
//        } else {
//            System.out.println("Not all items have been fetched yet.");
//            return 0; // Incomplete
//        }
        return 0;
    }

    @Override
    public JsonObject sendCommand(String commandType, String commandParam) {
//        // Handle the "refresh" command to refresh the inventory
//        if ("refresh".equalsIgnoreCase(commandType)) {
//            soapTest.refreshInventory();
//            return null; // Success
//        }
//        // Return -1 for unknown commands
//        return null;

        /*System.out.println("PROTOCOLS: " + getProtocolSPIImplementationsList());
        for (ProtocolSPI implementation : getProtocolSPIImplementationsList()) {
            System.out.println("Inside protocol loop");
            if (implementation.getClass().getModule().getName().equals("SOAP")) {
                System.out.println("SOAP found!!");
                return implementation.readFrom(endpoint, commandParam);
            }
        }*/
        if (commandType.equalsIgnoreCase("readFrom")) {
            for (ProtocolSPI implementation : getProtocolSPIImplementationsList()) {
                if (implementation.getClass().getModule().getName().equals("SOAP")) {
                    return implementation.readFrom(endpoint, commandParam);
                }
            }
        }
        return null;
    }

    @Override
    public String getCurrentSystemStatus() {
        // Create a list to store system status messages
        return "OPERATING";
    }

    @Override
    public String getCurrentConnectionStatus() {
        // Create a list to store connection status messages
        return "Soap Active";
    }

    public Collection<? extends ProtocolSPI> getProtocolSPIImplementationsList() {
        return ServiceLoader.load(ProtocolSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}