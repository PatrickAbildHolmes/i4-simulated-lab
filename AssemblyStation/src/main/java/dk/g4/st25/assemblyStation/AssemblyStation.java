package dk.g4.st25.assemblyStation;

import org.eclipse.paho.client.mqttv3.*;
import com.google.gson.JsonObject;

//To run this:
//java --module-path mods-mvn --class-path "libs/*" --module=AssemblyStation/dk.g4.st25.assemblyStation.AssemblyStation
public class AssemblyStation {
    public static void main(String[] args) {
        // This module handles connection between control panel/GUI and the Assembly Station
        String broker = "tcp://localhost:1883";
        String topic = "emulator/operation"; //Topic to which there should be published
        String clientId = "ClientID"; //Unique ClientID for the broker to recognise this client
        JsonObject message = new JsonObject(); //JSON Object to store the message
        message.addProperty("ProcessID", 12345);


        try{
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            client.connect(options); //Connect to the broker

            MqttMessage mqttMessage = new MqttMessage(message.toString().getBytes());
            mqttMessage.setQos(2);
            client.publish(topic, mqttMessage);
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
