package dk.g4.st25.soap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.g4.st25.common.protocol.ProtocolSPI;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

public class SOAP implements ProtocolSPI {

    private final SoapService soapService = new SoapService();
    @Override
    public int connect(String endpoint) {
        try {
            // Checks to see if the response equals 200, and returns a 0 if successful
            // SOAP doesn't have a consistent connection, so it only checks the URL once
            HttpResponse<String> response = Unirest.get(endpoint).asString();
            return response.getStatus() == 200 ? 0 : 1;
        } catch (Exception e) {
            // Returns a -1 if an error occurs
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int writeTo(String message, String endpoint) {
        // Write to has to be a JSON string with an "action" followed by the parameters for the function
        // example:
        // {"action":"pick", "trayId":2}
        // or {"action":"insert", "trayId":2, "itemName":"Cupcake"}
        try {
            JSONObject json = new JSONObject(message);
            String action = json.getString("action");
            int trayId = json.getInt("trayId");

            // Checks if the user choses to "pick" an item from the warehouse
            if ("pick".equalsIgnoreCase(action)) {
                soapService.pickItem(trayId);
            }
            // Checks if the user choses to "insert" an item into the warehouse
            else if ("insert".equalsIgnoreCase(action)) {
                String name = json.getString("itemName");
                soapService.insertItem(trayId, name);
            }
            // Does not know what action to perform:
            else {
                return -1;
            }
            // Returns 0 if success
            return 0;

            // Exception handler
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int subscribeToTopic(String topic) {
        // SOAP can't use this
        return 0;
    }

    @Override
    public JsonObject readFrom(String endpoint, String method) {
        try {
            // If the method is getInventory, then do the following:
            if ("getInventory".equalsIgnoreCase(method)) {
                // Creates a JsonObject
                JSONObject jsonInventory = soapService.getInventory();
                // As the readfrom is a "JsonObject" and not a JSONObject, it is then a gson
                // This is parsed as a gson then
                return JsonParser.parseString(jsonInventory.toString()).getAsJsonObject();
            }
            return null;

            // Exception handler
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
