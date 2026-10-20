package cscie97.asn2.housemate.model;

/**
 * Base exception for Housemate Model Service errors.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class HousemateModelServiceException extends Exception {

    /**
     * Creates an empty Housemate Model Service exception.
     */
    public HousemateModelServiceException() {
        super();
    }

    /**
     * Creates a Housemate Model Service exception with a message.
     *
     * @param message description of the error
     */
    public HousemateModelServiceException(String message) {
        super(message);
    }

    /**
     * Creates a Housemate Model Service exception with a cause.
     *
     * @param cause cause of the error
     */
    public HousemateModelServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a Housemate Model Service exception with a message and cause.
     *
     * @param message description of the error
     * @param cause cause of the error
     */
    public HousemateModelServiceException(
        String message,
        Throwable cause
    ) {
        super(message, cause);
    }
}