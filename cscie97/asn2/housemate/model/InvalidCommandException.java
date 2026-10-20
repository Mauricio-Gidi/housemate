package cscie97.asn2.housemate.model;

/**
 * Thrown when a Housemate command is invalid.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class InvalidCommandException extends CommandLineException {

    /**
     * Creates an empty invalid-command exception.
     */
    public InvalidCommandException() {
        super();
    }

    /**
     * Creates an invalid-command exception with a message.
     */
    public InvalidCommandException(String message) {
        super(message);
    }

    /**
     * Creates an invalid-command exception with a message and cause.
     */
    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an invalid-command exception with a cause.
     */
    public InvalidCommandException(Throwable cause) {
        super(cause);
    }
}