package dk.g4.st25;

import com.google.gson.JsonObject;
import dk.g4.st25.REST.Protocol;
import dk.g4.st25.REST.REST;

import java.util.HashMap;
import java.util.Map;


public class AGV {
    private final String url = "http://localhost:8082/v1/status";
    private final Protocol protocol = new REST();

    public JsonObject getStatus() {
        return protocol.get(url);
    }

    public JsonObject execute() {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("State", 2);
        return protocol.put(url, requestBody);
    }

    public JsonObject idle() {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("State", 1);
        return protocol.put(url, requestBody);
    }

    public static void main(String[] args) {
        AGV agv = new AGV();
        System.out.println(agv.getStatus());
        System.out.println(agv.execute());

        //System.out.println(agv.idle());

    }
}
