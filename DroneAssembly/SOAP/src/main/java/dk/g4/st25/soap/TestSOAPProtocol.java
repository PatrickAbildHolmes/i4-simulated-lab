package dk.g4.st25.soap;
import com.google.gson.JsonObject;
import kong.unirest.json.JSONObject;

public class TestSOAPProtocol {
    public static void main(String[] args) {
        // Use the protocol interface
        SOAP protocol = new SOAP();

        // Try to "connect"
        int connectionStatus = protocol.connect("http://localhost:8081/Service.asmx");
        System.out.println("Connection Status: " + connectionStatus);

        // Pick an item from tray 2
        JSONObject pickJson = new JSONObject();
        pickJson.put("action", "pick");
        pickJson.put("trayId", 2);
        int pickStatus = protocol.writeTo(pickJson.toString(), "http://localhost:8081/Service.asmx");
        System.out.println("Pick Status: " + pickStatus);

        // Insert a new item into tray 2
        JSONObject insertJson = new JSONObject();
        insertJson.put("action", "insert");
        insertJson.put("trayId", 2);
        insertJson.put("itemName", "Skildpadde");
        int insertStatus = protocol.writeTo(insertJson.toString(), "http://localhost:8081/Service.asmx");
        System.out.println("Insert Status: " + insertStatus);

        // Read inventory
        JsonObject inventory = protocol.readFrom("http://localhost:8081/Service.asmx", "getInventory");
        System.out.println("Inventory Snapshot:");
        System.out.println(inventory);
    }
}

