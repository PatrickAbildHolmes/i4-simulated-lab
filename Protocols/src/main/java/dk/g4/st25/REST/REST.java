package dk.g4.st25.REST;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class REST implements Protocol {

    @Override
    public JsonObject get(String apiUrl) {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);

        return stringToJson(response);
    }

    public JsonObject put(String apiUrl, Map<String, Integer> requestBody) {
        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Create a map to represent the body {"State": 2}

        // Create HttpEntity containing the body and any headers (optional)
        HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(requestBody);

        // Send the PUT request
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, entity, String.class);

        // Print the response
        System.out.println("Response: " + response.getBody());

        return stringToJson(response.getBody());
    }

    public JsonObject stringToJson(String string) {
        return JsonParser.parseString(string).getAsJsonObject();
    }
}