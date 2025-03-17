package dk.g4.st25.protocols;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class RESTTest {
    private RESTProtocol rest = new RESTProtocol();

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