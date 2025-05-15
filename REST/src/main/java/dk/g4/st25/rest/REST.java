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

import java.util.Map;

public class REST extends Protocol implements ProtocolSPI {
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
        if (!get().equals(null)) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public int writeTo(String message, String endpoint) {
//        switch (message) {
//            case ""
//        }
        return 1;
    }

    @Override
    public int subscribeToTopic(String topic) {
        return 0;
    }

    @Override
    public JsonObject readFrom(String endpoint, String method) {
        if (method.equals("getStatus")) {
            return get();
        } else return null;
    }

}
