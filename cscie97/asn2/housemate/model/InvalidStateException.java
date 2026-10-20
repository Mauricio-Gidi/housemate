package cscie97.asn2.housemate.model;

/**
 * Thrown when a requested device state cannot be found.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class InvalidStateException extends HousemateModelServiceException {

    private final String deviceId;
    private final String state;

    /**
     * Creates an invalid-state exception.
     */
    public InvalidStateException(
        String deviceId,
        String state
    ) {
        super(
            "State not found: "
                + state
                + " for device "
                + deviceId
        );

        this.deviceId = deviceId;
        this.state = state;
    }

    /**
     * Returns the device ID.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Returns the requested state.
     */
    public String getState() {
        return state;
    }
}