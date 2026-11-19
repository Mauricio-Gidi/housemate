// AccessToken.java

package cscie97.asn4.housemate.entitlement;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A user session that expires after a configurable period of inactivity.
 * 
 * @author Mauricio Gidi
 * @version November 16, 2026
 */
public final class AccessToken implements Visitable {

    private static final Duration TIMEOUT = Duration.ofSeconds(
            Long.getLong("housemate.tokenTimeoutSeconds", 1800L));

    private final String guid;
    private final User user;
    private final Instant issued;
    private Instant lastUsed;
    private boolean active = true;

    /** Creates an active token for the authenticated user. */
    public AccessToken(String guid, User user) {
        this.guid = Objects.requireNonNull(guid, "guid");
        this.user = Objects.requireNonNull(user, "user");
        this.issued = Instant.now();
        this.lastUsed = issued;
    }

    /** Returns the token ID. */
    public String getGuid() {
        return guid;
    }

    /** Returns the authenticated user. */
    public User getUser() {
        return user;
    }

    /** Returns the time the token was issued. */
    public Instant getIssued() {
        return issued;
    }

    /** Returns the time of the last authorized use. */
    public synchronized Instant getLastUsed() {
        return lastUsed;
    }

    /** Returns when the token will expire if it is not used again. */
    public synchronized Instant expiresAt() {
        return lastUsed.plus(TIMEOUT);
    }

    /** Checks the token state and marks an expired token inactive. */
    public synchronized boolean isValid() {
        if (active && !Instant.now().isBefore(expiresAt())) {
            active = false;
        }
        return active;
    }

    /** Returns whether the token is currently active and unexpired. */
    public synchronized boolean isActive() {
        return isValid();
    }

    /** Refreshes the inactivity deadline after a successful authorization. */
    public synchronized void refreshExpiration() {
        if (!isValid()) {
            throw new InvalidAccessTokenException("Token is expired or inactive");
        }
        lastUsed = Instant.now();
    }

    /** Revokes the token, preventing further use. */
    public synchronized void invalidate() {
        active = false;
    }
}
