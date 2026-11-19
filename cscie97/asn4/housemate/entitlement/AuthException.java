package cscie97.asn4.housemate.entitlement;

/** Base unchecked authentication/authorization exception with safe context. */
public class AuthException extends RuntimeException {
    private final String contextId;

    public AuthException(String message) {
        this(message, null);
    }

    public AuthException(String message, String contextId) {
        super(message);
        this.contextId = contextId;
    }

    public String getContextId() {
        return contextId;
    }
}