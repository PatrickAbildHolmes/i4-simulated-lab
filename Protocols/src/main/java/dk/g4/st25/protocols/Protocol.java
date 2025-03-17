package dk.g4.st25.protocols;

import com.google.gson.JsonObject;

import java.util.Map;

public interface Protocol {
    JsonObject get();
    JsonObject put(Map<String, Object> requestBody);

}