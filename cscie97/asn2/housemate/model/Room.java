package cscie97.asn2.housemate.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Models a room and its devices.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class Room {

    private final String type;
    private final int floor;
    private final String roomName;
    private final int windows;
    private final Map<String, Device> devices;

    /**
     * Creates a room.
     */
    public Room(String type, int floor, String roomName, int windows) {
        this.type = type;
        this.floor = floor;
        this.roomName = roomName;
        this.windows = windows;
        this.devices = new HashMap<>();
    }

    /**
     * Returns the room name.
     */
    public String getName() {
        return this.roomName;
    }

    /**
     * Adds a sensor to this room.
     */
    public void addSensor(
            String sensorName,
            String type) {
        devices.put(
                sensorName,
                new Sensor(sensorName, type));
    }

    /**
     * Adds an appliance to this room.
     */
    public void addAppliance(
            String applianceName,
            String type,
            int energyConsumption) {
        devices.put(
                applianceName,
                new Appliance(
                        applianceName,
                        type,
                        energyConsumption));
    }

    /**
     * Returns a device by ID.
     */
    public Device getDevice(String deviceId) {
        return devices.get(deviceId);
    }

    /**
     * Returns the room type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the floor number.
     */
    public int getFloor() {
        return this.floor;
    }

    /**
     * Returns the number of windows.
     */
    public int getWindows() {
        return this.windows;
    }

    /**
     * Returns the devices in this room.
     */
    public Map<String, Device> getDevices() {
        return this.devices;
    }
}