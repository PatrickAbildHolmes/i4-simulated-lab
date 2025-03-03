package dk.g4.st25;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import dk.g4.st25.REST.REST;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

class AGVTest {

    private REST protocol;
    private AGV agv;

    @BeforeEach
    void setUp() {
        protocol = mock(REST.class);  // Mock the REST client
        agv = new AGV(System.getenv("REST"), protocol);
        Mockito.reset(protocol);
    }

    @org.junit.jupiter.api.Test
    void moveToAssembly() {
        when(protocol.put(eq(System.getenv("REST")), anyMap())).thenReturn(getFakeResponse("MoveToAssemblyOperation"));
        agv.moveToAssembly();
        testOperation("MoveToAssemblyOperation");
        verify(protocol).put(eq(System.getenv("REST")), anyMap());
    }

    @org.junit.jupiter.api.Test
    void getStatus() {
        agv.getStatus();
        verify(protocol).get(eq(System.getenv("REST")));
    }

    @org.junit.jupiter.api.Test
    void update() {
        when(protocol.get(eq(System.getenv("REST")))).thenReturn(getFakeResponse("None"));
        agv.update();
        testOperation("None");
        verify(protocol).get(eq(System.getenv("REST")));
    }

    @org.junit.jupiter.api.Test
    void execute() {
        when(protocol.put(eq(System.getenv("REST")), anyMap())).thenReturn(getFakeResponse("None"));
        agv.execute();
        testOperation("None");
        verify(protocol).put(eq(System.getenv("REST")), anyMap());
    }

    @org.junit.jupiter.api.Test
    void chargeBattery() {
        when(protocol.put(eq(System.getenv("REST")), anyMap())).thenReturn(getFakeResponse("MoveToChargerOperation"));
        agv.moveToAssembly();
        testOperation("MoveToChargerOperation");
        verify(protocol).put(eq(System.getenv("REST")), anyMap());
    }

    @org.junit.jupiter.api.Test
    void moveToStorage() {
        when(protocol.put(eq(System.getenv("REST")), anyMap())).thenReturn(getFakeResponse("MoveToStorageOperation"));
        agv.moveToStorage();
        testOperation("MoveToStorageOperation");
        verify(protocol).put(eq(System.getenv("REST")), anyMap());
    }

    @org.junit.jupiter.api.Test
    void putAssembly() {
        when(protocol.put(eq(System.getenv("REST")), anyMap())).thenReturn(getFakeResponse("PutAssemblyOperation"));
        agv.putAssembly();
        testOperation("PutAssemblyOperation");
        verify(protocol).put(eq(System.getenv("REST")), anyMap());
    }

    @org.junit.jupiter.api.Test
    void pickAssembly() {
        when(protocol.put(eq(System.getenv("REST")), anyMap())).thenReturn(getFakeResponse("PickAssemblyOperation"));
        agv.pickAssembly();
        testOperation("PickAssemblyOperation");
        verify(protocol).put(eq(System.getenv("REST")), anyMap());
    }

    @org.junit.jupiter.api.Test
    void pickWarehouse() {
        when(protocol.put(eq(System.getenv("REST")), anyMap())).thenReturn(getFakeResponse("PickWarehouseOperation"));
        agv.pickWarehouse();
        testOperation("PickWarehouseOperation");
        verify(protocol).put(eq(System.getenv("REST")), anyMap());
    }

    @org.junit.jupiter.api.Test
    void putWarehouse() {
        when(protocol.put(eq(System.getenv("REST")), anyMap())).thenReturn(getFakeResponse("PutWarehouseOperation"));
        agv.pickWarehouse();
        testOperation("PutWarehouseOperation");
        verify(protocol).put(eq(System.getenv("REST")), anyMap());
    }

    private JsonObject getFakeResponse(String operation) {
        JsonObject fakeResponse = new JsonObject();
        fakeResponse.addProperty("battery", 100);
        fakeResponse.addProperty("program name", operation);
        fakeResponse.addProperty("state", 1);
        fakeResponse.addProperty("timestamp", "year-month-dateTime");

        return fakeResponse;
    }

    private void testOperation(String operation) {
        assertEquals(operation, agv.getProgramName());
        assertEquals(1, agv.getState());
        assertEquals(100, agv.getBattery());
        assertEquals("year-month-dateTime", agv.getTimeStamp());
    }
}