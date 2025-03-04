package dk.g4.st25;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import dk.g4.st25.REST.REST;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

class AGVTest {

    private REST protocol;
    private AGV agv;
    private Dotenv dotenv = Dotenv.configure().directory("../").load();
    private String url = dotenv.get("REST_URL");

    @BeforeEach
    void setUp() {
        protocol = mock(REST.class);
        agv = new AGV(protocol);
        Mockito.reset(protocol);
    }

    @org.junit.jupiter.api.Test
    void moveToAssembly() {
        JsonObject fakeResponse = new JsonObject();
        fakeResponse.addProperty("battery", 100);
        fakeResponse.addProperty("program name", "MoveToAssemblyOperation");
        fakeResponse.addProperty("state", 1);
        fakeResponse.addProperty("timestamp", "year-month-dateTime");

        when(protocol.put(eq(url), anyMap())).thenReturn(fakeResponse);

        agv.moveToAssembly();

        assertEquals("MoveToAssemblyOperation", agv.getProgramName());
        assertEquals(1, agv.getState());
        assertEquals(100, agv.getBattery());
        assertEquals("year-month-dateTime", agv.getTimeStamp());

        verify(protocol).put(eq(url), anyMap());
    }

    @org.junit.jupiter.api.Test
    void getStatus() {
        agv.getStatus();
        verify(protocol).get(eq(url));
    }

    @org.junit.jupiter.api.Test
    void execute() {
        JsonObject fakeResponse = new JsonObject();
        fakeResponse.addProperty("battery", 100);
        fakeResponse.addProperty("program name", "MoveToAssemblyOperation");
        fakeResponse.addProperty("state", 2);
        fakeResponse.addProperty("timestamp", "year-month-dateTime");

        when(protocol.put(eq(url), anyMap())).thenReturn(fakeResponse);

        agv.execute();

        assertEquals("MoveToAssemblyOperation", agv.getProgramName());
        assertEquals(2, agv.getState());
        assertEquals(100, agv.getBattery());
        assertEquals("year-month-dateTime", agv.getTimeStamp());
        verify(protocol).put(eq(url), anyMap());
    }
}