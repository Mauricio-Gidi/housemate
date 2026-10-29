// Device.java

package cscie97.asn2.housemate.model;

/**
 * s
 * Abstract base class for Housemate devices.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public abstract class Device {

    private final String deviceId;
    private final String type;

    /**
     * Creates a device.
     *
     * @param deviceId unique identifier of the device
     * @param type     type of device
     */
    protected Device(
            String deviceId,
            String type) {
        this.deviceId = deviceId;
        this.type = type;
    }

    /**
     * Returns the device ID.
     *
     * @return unique identifier of the device
     */
    public String getId() {
        return this.deviceId;
    }

    /**
     * Returns the device type.
     *
     * @return type of device
     */
    public String getType() {
        return this.type;
    }
}