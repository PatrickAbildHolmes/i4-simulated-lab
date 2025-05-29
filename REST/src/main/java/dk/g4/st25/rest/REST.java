package dk.g4.st25.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.g4.st25.common.protocol.Protocol;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class REST extends Protocol {
    private boolean hasProgram = false;

    private JsonObject loadProgram(String programName, String endpoint) {
        /**
         * Loads the program onto the endpoint, and readies it for execution
         */
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Program name", programName);
        requestBody.put("State", 1);
        JsonObject res = put(requestBody, endpoint);
        hasProgram = true;
        return res;
    }

    public JsonObject execute(String endpoint) {
        /**
         * Executes the loaded program
         */
        if (!hasProgram) return null; // else AGV will freeze
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("State", 2);
        return put(requestBody, endpoint);
    }

    public JsonObject get(String endpoint) {
        /**
         * Sends an actual get request to the endpoint
         */
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(endpoint, String.class);

        return stringToJson(response);
    }

    public JsonObject put(Map<String, Object> requestBody, String endpoint) {
        /**
         * Sends an actual post request to the endpoint
         */
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody);

        // Send request
        ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.PUT, entity, String.class);

        return stringToJson(response.getBody());
    }

    public JsonObject stringToJson(String string) {
        return JsonParser.parseString(string).getAsJsonObject();
    }

    private boolean isTaskInProgress(String endpoint) {
        JsonObject status = get(endpoint);
        if (status == null || !status.has("state")) return false;

        int state = status.get("state").getAsInt();
        return state == 2; // 2 = task is executing
    }

    public boolean waitForTaskCompletion(String endpoint) {
        int attempts = 0;
        while (attempts < 30) {  // Wait up to 30 seconds
            if (!isTaskInProgress(endpoint)) {
                return true;  // Task is done
            }
            try {
                Thread.sleep(1000);  // Wait 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            attempts++;
        }
        return false;  // Timeout
    }


    public int restExecutionCommand(String programName, String endpoint) {
        if (!waitForTaskCompletion(endpoint)) {
            System.out.println("Timeout waiting for previous task to complete");
            return 0;
        }

        JsonObject responseLoad = loadProgram(programName, endpoint);
        if (responseLoad == null) {
            return 0;
        }

        JsonObject execute = execute(endpoint);
        return execute != null ? 1 : 0;
    }

    @Override
    public int connect(String endpoint) {
        if (get(endpoint) != null) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public int writeTo(String message, String endpoint) {
        System.out.println("Inside REST writeTo");
        switch (message.toLowerCase()) {
            // Collected all cases as they all run the same
            case "movetochargeroperation":
            case "movetoassemblyoperation":
            case "movetostorageoperation":
            case "putassemblyoperation":
            case "putwarehouseoperation":
            case "pickwarehouseoperation":
            case "pickassemblyoperation":
                return restExecutionCommand(message, endpoint);
            default:
                break;

        }
        return 0;
    }

    @Override
    public int subscribeToTopic(String topic) {
        return 0;
    }

    @Override
    public JsonObject readFrom(String endpoint, String method) {
        /**
         * @return is the state that the machine is currently in as a JsonObject
         */
        if (method.equals("getStatus")) {
            return get(endpoint);
        } else return null;
    }

}
