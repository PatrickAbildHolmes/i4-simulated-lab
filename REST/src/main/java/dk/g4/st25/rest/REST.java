package dk.g4.st25.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.g4.st25.common.protocol.ProtocolSPI;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class REST implements ProtocolSPI {
    protected String apiUrl;

    public REST() {
        String apiKey = "REST_URL";

        // In testing the working directory is swapped to this module which makes it unable to find .env file
        try {
            this.apiUrl = Dotenv.load().get(apiKey);
        } catch (Exception e) {
            this.apiUrl = Dotenv.configure().directory("../").load().get(apiKey);
        }
    }

    public JsonObject get() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);

        return stringToJson(response);
    }

    public JsonObject put(Map<String, Object> requestBody) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody);

        // Send request
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, entity, String.class);

        return stringToJson(response.getBody());
    }

    public JsonObject stringToJson(String string) {
        return JsonParser.parseString(string).getAsJsonObject();
    }

    @Override
    public int connect(String endpoint) {
        return 0;
    }

    @Override
    public int writeTo(String message, String endpoint) {
        return 0;
    }

    @Override
    public int subscribeToTopic(String topic) {
        return 0;
    }

    @Override
    public JsonObject readFrom(String endpoint, String method) {
        return null;
    }
}
