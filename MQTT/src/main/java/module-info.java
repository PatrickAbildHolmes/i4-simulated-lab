import dk.g4.st25.common.protocol.ProtocolSPI;

module MQTT {
    requires Common;
    requires CommonProtocol;
    requires com.google.gson;
    requires org.eclipse.paho.client.mqttv3;
    provides ProtocolSPI with dk.g4.st25.mqtt.MQTT;
}