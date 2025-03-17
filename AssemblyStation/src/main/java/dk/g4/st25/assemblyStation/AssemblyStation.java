package dk.g4.st25.assemblyStation;

import org.eclipse.paho.client.mqttv3.*;
import com.google.gson.JsonObject;

public class AssemblyStation {
    public static void main(String[] args) {
        String broker = "mqtt://localhost:9001"; //Broker URL
        String topic = "emulator/operation"; //Topic to which there should be published
        String clientId = "ClientID"; //IDK about this
        JsonObject message = new JsonObject();
        message.addProperty("CurrentOperation", 123);


        try{
            MqttClient client = new MqttClient(broker, clientId); //Create a new client with the broker and client ID
            MqttConnectOptions options = new MqttConnectOptions(); //Create a new options instance
            options.setCleanSession(true);

            client.connect(options); //Connect to the broker

            MqttMessage mqttMessage = new MqttMessage(message.toString().getBytes());
            mqttMessage.setQos(2);
            client.publish(topic, mqttMessage);
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("Hello World!");
    }
}
