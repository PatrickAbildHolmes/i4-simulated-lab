package dk.g4.st25.common.util;

public enum DronePart {
    PROPELLER("propeller"),
    MOTOR("motor"),
    FLIGHT_CONTROLLER("flight controller"),
    BATTERY("battery"),
    FRAME("frame"),
    CAMERA("camera"),
    ESC("ESC"), // Electronic Speed Controller
    RECEIVER("receiver"),
    GPS_MODULE("GPS module"),
    LANDING_GEAR("landing gear");

    private final String itemName;

    DronePart(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }
}