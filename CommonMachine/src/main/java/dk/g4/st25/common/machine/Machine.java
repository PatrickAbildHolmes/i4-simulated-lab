package dk.g4.st25.common.machine;

import dk.g4.st25.common.protocol.Protocol;

import java.util.HashMap;
import java.util.Map;

public abstract class Machine {
    protected Protocol protocol; // instantiate from a chosen protocol - for AssemblyStation, from MQTT
    protected SystemStatus systemStatus; // What it is currently doing (producing, idle, etc.)
    protected String command; // Latest received command
    protected Map<String, Integer> inventory = new HashMap<>(); // e.g. {"DroneComponents": 0, "Drones": 1}
    protected Drone product; // Assembles products, in our case, Drones
    public enum SystemStatus { // Implement this in inherited classes
    }
}
