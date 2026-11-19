package cscie97.asn4.housemate.entitlement;

/** Failed username/password or simulated voiceprint authentication. */
public final class AuthenticationException extends AuthException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, String userId) {
        super(message, userId);
    }
}