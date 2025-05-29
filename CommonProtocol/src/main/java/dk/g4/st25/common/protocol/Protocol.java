package dk.g4.st25.common.protocol;

import com.google.gson.JsonObject;
import dk.g4.st25.common.util.Connection;

public abstract class Protocol extends Connection implements ProtocolSPI{
    @Override
    public int connect(String endpoint) {
        return 0;
    }

    @Override
    public int writeTo(String message, String endpoint) {
        return 0;
    }

    @Override
    public int subscribeToTopic(String topic) {
        return 0;
    }

    @Override
    public JsonObject readFrom(String endpoint, String method) {
        return null;
    }
    // This class is a placeholder for the Protocol module.
    // The actual implementation will be provided in the actual protocol modules (REST, SOAP, MQTT)

}
