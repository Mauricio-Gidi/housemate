// Event.java

package cscie97.asn2.housemate.model.observer;

/**
 * Stores a device status change reported by the Model Service.
 * The Controller uses this information to select a command.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class Event {
    private final String houseId;
    private final String roomId;
    private final String deviceId;
    private final String deviceType;
    private final String statusName;
    private final String prevValue;
    private final String newValue;

    /**
     * Creates an event for a device status change.
     *
     * @param houseId    the house where the event occurred
     * @param roomId     the room where the event occurred
     * @param deviceId   the device that changed
     * @param deviceType the type of device
     * @param statusName the status that changed
     * @param prevValue  the previous status value
     * @param newValue   the new status value
     */
    public Event(String houseId, String roomId, String deviceId, String deviceType, String statusName, String prevValue,
            String newValue) {
        this.houseId = houseId;
        this.roomId = roomId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.statusName = statusName;
        this.prevValue = prevValue;
        this.newValue = newValue;
    }

    /**
     * Returns the device type.
     *
     * @return the device type
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Returns the name of the changed status.
     *
     * @return the status name
     */
    public String getStatusName() {
        return statusName;
    }

    /**
     * Returns the new status value.
     *
     * @return the new value
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * Returns the house identifier.
     *
     * @return the house identifier
     */
    public String getHouseId() {
        return houseId;
    }

    /**
     * Returns the room identifier.
     *
     * @return the room identifier
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Returns the device identifier.
     *
     * @return the device identifier
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Returns the previous status value.
     *
     * @return the previous value
     */
    public String getPrevValue() {
        return prevValue;
    }
}
