package com.semester.projekt.protocols;

import com.google.gson.JsonObject;

import java.util.Map;

public interface Protocol {
    public JsonObject get();
    public JsonObject put(Map<String, Object> requestBody);

}