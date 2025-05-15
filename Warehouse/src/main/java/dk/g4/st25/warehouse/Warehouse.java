package dk.g4.st25.warehouse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.g4.st25.common.machine.DroneComponent;
import dk.g4.st25.common.machine.ItemConfirmationI;
import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.machine.Tray;
import dk.g4.st25.common.protocol.ProtocolSPI;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.soap.SoapService;
import dk.g4.st25.soap.SOAP;


import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class Warehouse implements MachineSPI, IExecuteCommand, IMonitorStatus, ItemConfirmationI {
    // SOAP service object for interacting with the warehouse system
    private final SOAP soapTest = new SOAP();
    // Counter to track the number of items successfully fetched
    private int itemsFetched = 0;
    private final String endpoint = "http://localhost:8081/Service.asmx";
    private Tray[] trays; // Trays for delivery and pick-up. Fixed number
    private Object mostRecentlyReceived;



    @Override
    public int taskCompletion() {
        try {
            // Attempt to fetch an item from tray 1
            String message = "{\"action\":\"pick\", \"trayId\":1}";
            soapTest.writeTo(message, endpoint);
            //{"action":"pick", "trayId":2}
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

    /**
    * Vi vil gerne tjekke for at den item der bliver puttet i et tray er i warehouset lige nu.
    * Hvis det er, så skal den finde id'et som er placeret i warehouset og bruge writeTo kommandoen som er i message
    *
     * Derefter skulle den gerne følge resten af logikken fra samme metode i "assembly-line" klassen, som også har
    * en confirmItemDelivery() metode
     */
    @Override
    public boolean confirmItemDelivery() { // Add object to parameter
        for (Tray tray: trays){
            if (tray.isAvailable()){
                tray.setContent(new DroneComponent());
                tray.setAvailable(false);
                if (mostRecentlyReceived instanceof DroneComponent){
                    JsonObject inventory = soapTest.readFrom(endpoint, "getInventory");
                    if (inventory.has(mostRecentlyReceived.getClass().getName())){
                        // Item exists in warehouse
                        JsonElement pickedItem = inventory.get(mostRecentlyReceived.getClass().getName());
                        String message = "{\"action\":\"pick\", \"trayId\":"+ pickedItem.getAsJsonObject().get("trayId") +"}";
                        soapTest.writeTo(message, endpoint);
                        return true;
                    } else {
                        // Item doesn't exist in warehouse
                        tray.setContent(null);
                        tray.setAvailable(true);
                        return false;
                    }
                }else {
                    tray.setContent(null);
                    tray.setAvailable(true);
                    return false;
                }
            }
            return false;
        }
        return false;
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
    public void setMostRecentlyReceived(Object mostRecentlyReceived) {

    }

    @Override
    public JsonObject sendCommand(String commandType) {
        return null;
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
    public JsonObject sendCommand(String commandType, String commandName, String commandParam) {
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