package dk.g4.st25.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import com.google.gson.JsonObject;

public final class MQTT {
    // Singleton instance of the MQTT Connection
    private static MQTT mqttInstance;
    private MqttClient connection;

    private MQTT(){
        // Constructor to create a single instance, and then establish a connection
        try {
            this.connection = connectToBroker();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MqttClient connectToBroker() {
        try {
            String broker = "tcp://localhost:1883";
            String clientId = "JavaClient";
            //Creates a new client with the broker and client ID
            MqttClient mqttClient = new MqttClient(broker, clientId);
            // Create a new MqttConnectOptions object which will store all connection settings
            MqttConnectOptions options = new MqttConnectOptions();
            // The broker will remove all information about this client when it disconnects
            // The client will not receive any old messages when it reconnects
            options.setCleanSession(true);
            // Connects to the MQTT using the connection options
            mqttClient.connect(options);
            return mqttClient;
        } catch (MqttException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MQTT getMqttInstance() {
    // Get singleton instance
        if (mqttInstance == null) {
            mqttInstance = new MQTT();
        }
        return mqttInstance;
    }

    public MqttClient getMqttClient() {
    // Get connection on the single instance
        return this.connection;
    }

    public void subscribeToTopic(String topic) {
    // Subscribes to a topic on the singleton MQTT client
        try {
            getMqttInstance().getMqttClient().subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String topic, JsonObject message) {
        // Creates a new MQTT message with the JSON message
        MqttMessage mqttMessage = new MqttMessage(message.toString().getBytes());
        // Sets the Quality of Service for the MQTT message to level 2
        // Guarantees that the message will be delivered exactly once
        // Most reliable but slowest delivery method, highest overhead in network traffic
        mqttMessage.setQos(2);
        try {
            getMqttInstance().getMqttClient().publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
    // Disconnects the client from the MQTT
        try {
            getMqttInstance().getMqttClient().disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
