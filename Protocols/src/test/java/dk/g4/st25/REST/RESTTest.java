package dk.g4.st25.REST;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class RESTTest {
    private REST rest = new REST();
    private Dotenv dotenv = Dotenv.configure().directory("../").load();
    private String url = dotenv.get("REST");

    @org.junit.jupiter.api.Test
    void get() {
        rest.get(url);
    }

    @org.junit.jupiter.api.Test
    void put() {
        Map<String, Object> body = new HashMap<>();
        body.put("state", 1);
        rest.put(url, body);
    }

    @org.junit.jupiter.api.Test
    void stringToJson() {
        String test = "{\"state\":2}";
        assertInstanceOf(JsonObject.class, rest.stringToJson(test));
    }
}