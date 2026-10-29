package cscie97.asn2.housemate.model;

/**
 * Base exception for command-line errors.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class CommandLineException extends Exception {

    /**
     * Creates an empty command-line exception.
     */
    public CommandLineException() {
        super();
    }

    /**
     * Creates a command-line exception with a message.
     *
     * @param message description of the error
     */
    public CommandLineException(String message) {
        super(message);
    }

    /**
     * Creates a command-line exception with a cause.
     *
     * @param cause cause of the error
     */
    public CommandLineException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a command-line exception with a message and cause.
     *
     * @param message description of the error
     * @param cause   cause of the error
     */
    public CommandLineException(
            String message,
            Throwable cause) {
        super(message, cause);
    }
}