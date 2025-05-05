package dk.g4.st25.mqtt;

import com.google.gson.Gson;
import dk.g4.st25.common.protocol.Protocol;
import dk.g4.st25.common.protocol.ProtocolSPI;
import org.eclipse.paho.client.mqttv3.*;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTT extends Protocol implements ProtocolSPI {

    private MqttClient client;

    // Constructor to create a MQTT object
    public MQTT(){
//        try {
            // Creates a broker with the endpoint port on localhost using tcp
//            String endpoint = "tcp://localhost:" + endpointPort;
            // Assigns the client to the broker with the client ID
//            this.client = new MqttClient(endpoint, MqttClient.generateClientId());
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public int connect(String endpoint) {
        // endpoint is the address of the intended machine, e.g.:"tcp://localhost:1883"
        try {
            // Assigns the client to the broker with the client ID
            this.client = new MqttClient(endpoint, MqttClient.generateClientId());
            // Create a new MqttConnectOptions object which will store all connection settings
            MqttConnectOptions options = new MqttConnectOptions();
            // The broker will remove all information about this client when it disconnects
            // The client will not receive any old messages when it reconnects
            options.setCleanSession(true);
            // Connects to the MQTT using the connection options
            client.connect(options);
            // Return 1 for success
            return 1;
        } catch (MqttException e) {
            e.printStackTrace();
            // Return 0 for failure
            return 0;
        }
    }

    @Override
    public int writeTo(String message, String topic) {
        /* It uses the input parameter topic instead of the endpoint parameter in ProtocolSPI
         The endpoint is saved in the MQTTClient instance, and it is thereby not needed
         Topic is needed instead, as the client needs to know which topic to publish to */

        // Convert String message to JSON
        Gson gson = new Gson();
        message = gson.toJson(message);

        // Creates Mqtt message by converting String message to bytes format
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        // Sets the Quality of Service for the MQTT message to level 2
        // Guarantees that the message will be delivered exactly once
        // Most reliable but slowest delivery method, highest overhead in network traffic
        mqttMessage.setQos(2);
        try{
            client.publish(topic, mqttMessage);
            return 1;
        }catch (MqttException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int subscribeToTopic(String topic) {
    // Subscribes to a topic on the MQTT client
        try {
            client.subscribe(topic);
            return 1;
        } catch (MqttException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void disconnect() {
        try {
            // Disconnects the client from the MQTT
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JsonObject readFrom(String topic, String method) {
        // Method is unused. Topic must be either "emulator/checkhealth" or "emulator/status"
        if (subscribeToTopic(topic)==1) {
            try {
                // Waits for response, converts it to a String, and then to a JSON that is returned
                IMqttToken response = client.subscribeWithResponse(topic);
                response.waitForCompletion();
                String stringified =response.toString();
                Gson gson = new Gson();
                JsonObject returnStatement = gson.fromJson(stringified, JsonObject.class);
                return returnStatement;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
