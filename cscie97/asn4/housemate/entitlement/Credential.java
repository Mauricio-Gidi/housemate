package cscie97.asn4.housemate.entitlement;

import java.util.Objects;

/** Username credential. */
public final class Credential {
    private final String guid;
    private final String userName;
    private final String passwordHash;

    public Credential(String guid, String userName, String passwordHash) {
        this.guid = Objects.requireNonNull(guid, "guid");
        this.userName = Objects.requireNonNull(userName, "userName");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
    }

    public String getGuid() {
        return guid;
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}