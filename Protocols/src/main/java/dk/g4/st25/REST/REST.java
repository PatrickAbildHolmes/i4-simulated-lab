package dk.g4.st25.REST;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public class REST implements Protocol {

    @Override
    public JsonObject get(String apiUrl) {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);

        return stringToJson(response);
    }

    @Override
    public JsonObject put(String apiUrl, Map<String, Object> requestBody) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody);

        // Send request
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, entity, String.class);

        return stringToJson(response.getBody());
    }

    public JsonObject stringToJson(String string) {
        return JsonParser.parseString(string).getAsJsonObject();
    }

    public static void main(String[] args) {
        REST rest = new REST();

        Map<String, String> env = System.getenv();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }


    }
}