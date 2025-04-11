package dk.g4.st25.rest;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class RESTTest {
    private REST rest = new REST();

    // Integration test
    @org.junit.jupiter.api.Test
    void get() {
        rest.get();
    }

    @org.junit.jupiter.api.Test
    void stringToJson() {
        String test = "{\"state\":2}";
        assertInstanceOf(JsonObject.class, rest.stringToJson(test));
    }

    @Test
    void apiUrlNotNull() {
        assertNotNull(rest.apiUrl, "apiUrl is null");
    }
}