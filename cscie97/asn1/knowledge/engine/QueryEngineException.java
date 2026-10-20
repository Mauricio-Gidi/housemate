// QueryEngineException.java

package cscie97.asn1.knowledge.engine;

/**
 * The QueryEngineException class represents errors that occur while executing
 * queries or reading query files in the QueryEngine.
 *
 * @author Mauricio Gidi
 * Date: September 8, 2026
 */
public class QueryEngineException extends Exception {

    /**
     * Creates a QueryEngineException with no detail message.
     */
    public QueryEngineException() {
        super();
    }

    /**
     * Creates a QueryEngineException with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public QueryEngineException(String message) {
        super(message);
    }

    /**
     * Creates a QueryEngineException with the specified detail message and cause.
     *
     * @param message the detail message describing the error
     * @param cause the cause of the exception
     */
    public QueryEngineException(String message, Throwable cause) {
        super(message, cause);
    }

}