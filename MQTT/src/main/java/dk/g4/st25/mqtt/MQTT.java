package dk.g4.st25.mqtt;

import com.google.gson.Gson;
import dk.g4.st25.common.protocol.Protocol;
import dk.g4.st25.common.protocol.ProtocolSPI;
import io.github.cdimascio.dotenv.Dotenv;
import org.eclipse.paho.client.mqttv3.*;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;

public class MQTT extends Protocol {

    private MqttClient client;
    private String endpointPort;
    private String endpoint;

    // Constructor to create a MQTT object
    public MQTT(){
        try {
            this.endpointPort = Dotenv.load().get("MQTT_ENDPOINTPORT");
        } catch (Exception e) {
            this.endpointPort = Dotenv.configure().directory("../").load().get("MQTT_ENDPOINTPORT");
        }

        this.endpoint = "tcp://localhost:" + endpointPort;
        try {
//             Creates a broker with the endpoint port on localhost using tcp
//             Assigns the client to the broker with the client ID
            this.client = new MqttClient(this.endpoint, MqttClient.generateClientId(), null);
            System.out.println("CLIENT: " + this.client);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int connect(String endpoint) {
        // endpoint is the address of the intended machine, e.g.:"tcp://localhost:1883"
        try {
            // Assigns the client to the broker with the client ID
            this.client = new MqttClient(endpoint, MqttClient.generateClientId(), null);
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
        connect(this.endpoint);

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
            disconnect();
            return 1;
        }catch (MqttException e) {
            e.printStackTrace();
            disconnect();
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
            this.client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JsonObject readFrom(String topic, String method) {
        try {
            connect(endpoint);

            if (this.client.isConnected()) {
                // Create a variable to store the received message
                final JsonObject[] receivedMessage = new JsonObject[1];
                final boolean[] messageReceived = {false}; // Flag to indicate if the message is received

                // Set the callback before subscribing
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        System.out.println("Connection lost: " + cause.getMessage());
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
//                        System.out.println("Message arrived on topic " + topic + ": " + message.toString());

                        // Parse the received message payload into a JSON object
                        Gson gson = new Gson();
                        receivedMessage[0] = gson.fromJson(new String(message.getPayload()), JsonObject.class);

                        // Mark that the message is received
                        messageReceived[0] = true;
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        // Not needed for subscribe, this is used when publishing
                    }
                });

                // Subscribe to the "emulator/status" topic with QoS level 1
                client.subscribe(topic, 1);

                // Wait until a message is received (blocking loop)
                int waitCount = 0;
                while (!messageReceived[0] && waitCount < 30) {  // Wait for a max of 30 * 100ms = 3 seconds
                    Thread.sleep(100);  // Sleep for 100ms to prevent busy waiting
                    waitCount++;
                }

                // If a message was received, return it
                if (messageReceived[0]) {
                    return receivedMessage[0];  // Return the received message
                } else {
                    System.out.println("No message received within timeout");
                }

                // Disconnect after receiving the message or timeout
                disconnect();

            } else {
                System.out.println("Client not connected");
                disconnect();
            }
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
            disconnect();
        }

        return null;  // Return null if no message was received or an error occurred
    }
}

