// ImportException.java

package cscie97.asn1.knowledge.engine;

/**
 * The ImportException class represents errors that occur while importing
 * Triples or reading input files in the Importer.
 *
 * @author Mauricio Gidi
 * Date: September 8, 2026
 */
public class ImportException extends Exception {

    /**
     * Creates an ImportException with no detail message.
     */
    public ImportException() {
        super();
    }

    /**
     * Creates an ImportException with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public ImportException(String message) {
        super(message);
    }

    /**
     * Creates an ImportException with the specified detail message and cause.
     *
     * @param message the detail message describing the error
     * @param cause the cause of the exception
     */
    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }

}