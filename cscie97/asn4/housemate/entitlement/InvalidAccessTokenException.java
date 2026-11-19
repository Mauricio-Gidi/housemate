package cscie97.asn4.housemate.entitlement;

/** Invalid, expired or revoked access token. */
public final class InvalidAccessTokenException extends AuthException {
    private final String tokenStatus;

    public InvalidAccessTokenException(String message) {
        this(message, null);
    }

    public InvalidAccessTokenException(String message, String tokenStatus) {
        super(message);
        this.tokenStatus = tokenStatus;
    }

    public String getTokenStatus() {
        return tokenStatus;
    }
}