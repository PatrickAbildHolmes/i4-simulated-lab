package dk.g4.st25.rest;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
class RESTTest {
    private REST rest = new REST();

    // Integration test
    @Test
    void get() {
//        rest.get();
    }

    @Test
    void stringToJson() {
        String test = "{\"state\":2}";
        assertInstanceOf(JsonObject.class, rest.stringToJson(test));
    }
}