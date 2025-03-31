package dk.g4.st25.common.protocol;

import com.google.gson.JsonObject;

public interface ProtocolSPI {

    int connect(String endpoint);

    int writeTo(String message, String endpoint);

    int subscribeToTopic(String topic);

    // Returns a JSON object of all the requested data through a method lying on the machine
    JsonObject readFrom(String endpoint, String method);



}
