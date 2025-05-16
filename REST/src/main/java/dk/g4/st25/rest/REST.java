package dk.g4.st25.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.g4.st25.common.protocol.Protocol;
import dk.g4.st25.common.protocol.ProtocolSPI;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class REST extends Protocol implements ProtocolSPI {
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
        if (hasProgram) return null; // else AGV will freeze
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("State", 2);
        JsonObject res = put(requestBody, endpoint);
        hasProgram = true;
        return res;
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

    public int restExecutionCommand(String programName, String endpoint) {
        /**
         * Handles the process of first loading a program to the endpoint, and afterwards
         * executing that program
         */
        JsonObject responseLoad = loadProgram(programName, endpoint);
        JsonObject execute = execute(endpoint);

        if (responseLoad != null && execute != null) {
            return 1;
        } else {
            return 0;
        }
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
