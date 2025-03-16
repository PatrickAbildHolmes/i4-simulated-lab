package com.semester.projekt.avg;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.semester.projekt.rest.REST;
import org.junit.jupiter.api.BeforeEach;

class AGVTest {

    private REST protocol;
    private AGV agv;

    @BeforeEach
    void setUp() {
        protocol = mock(REST.class);
        agv = new AGV(protocol);
    }

    @org.junit.jupiter.api.Test
    void moveToAssembly() {
        JsonObject fakeResponse = new JsonObject();
        fakeResponse.addProperty("battery", 100);
        fakeResponse.addProperty("program name", "MoveToAssemblyOperation");
        fakeResponse.addProperty("state", 1);
        fakeResponse.addProperty("timestamp", "year-month-dateTime");

        when(protocol.put(anyMap())).thenReturn(fakeResponse);

        JsonObject res = agv.moveToAssembly();
        assertEquals(res, fakeResponse);
        verify(protocol).put(anyMap());
    }

    @org.junit.jupiter.api.Test
    void getStatus() {
        agv.getStatus();
        verify(protocol).get();
    }

    @org.junit.jupiter.api.Test
    void execute() {
        JsonObject fakeResponse = new JsonObject();
        fakeResponse.addProperty("battery", 100);
        fakeResponse.addProperty("program name", "MoveToAssemblyOperation");
        fakeResponse.addProperty("state", 2);
        fakeResponse.addProperty("timestamp", "year-month-dateTime");

        when(protocol.put(anyMap())).thenReturn(fakeResponse);

        JsonObject res = agv.execute();
        assertEquals(res, fakeResponse);
        verify(protocol).put(anyMap());
    }
}

