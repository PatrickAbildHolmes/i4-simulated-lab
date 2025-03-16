package com.semester.projekt.rest;

import com.semester.projekt.rest.iprotocol.Protocol;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public class REST implements Protocol {
    protected String apiUrl;

    public REST() {
        String apiKey = "REST_URL";
        // In testing working directory is swapped to this module which causes an error
        try {
            this.apiUrl = Dotenv.load().get(apiKey);
        } catch (Exception e) {
            this.apiUrl = Dotenv.configure().directory("../").load().get(apiKey);
        }
    }

    @Override
    public JsonObject get() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);

        return stringToJson(response);
    }

    @Override
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
}