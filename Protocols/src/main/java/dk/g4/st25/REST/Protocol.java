package dk.g4.st25.REST;

import com.google.gson.JsonObject;

import java.util.Map;

public interface Protocol {
    public JsonObject get(String apiUrl);
    public JsonObject put(String apiUrl, Map<String, Object> requestBody);

}
