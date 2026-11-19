// User.java

package cscie97.asn4.housemate.entitlement;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A registered user with credentials and assigned entitlements.
 * 
 * @author Mauricio Gidi
 * @version November 16, 2026
 */
public final class User implements Visitable {
    private final String guid;
    private final String name;
    private final Set<Entitlement> entitlements = new LinkedHashSet<>();
    private Credential credential;
    private VoicePrint voicePrint;

    /** Creates a registered user. */
    public User(String guid, String name) {
        this.guid = Objects.requireNonNull(guid, "guid");
        this.name = Objects.requireNonNull(name, "name");
    }

    /** Returns the user ID. */
    public String getGuid() {
        return guid;
    }

    /** Returns the user name. */
    public String getName() {
        return name;
    }

    /** Returns the username/password credential, if registered. */
    public Credential getCredential() {
        return credential;
    }

    /** Sets the username/password credential. */
    public void setCredential(Credential credential) {
        this.credential = Objects.requireNonNull(credential, "credential");
    }

    /** Returns the simulated voiceprint, if registered. */
    public VoicePrint getVoicePrint() {
        return voicePrint;
    }

    /** Sets the simulated voiceprint. */
    public void setVoicePrint(VoicePrint voicePrint) {
        this.voicePrint = Objects.requireNonNull(voicePrint, "voicePrint");
    }

    /**
     * Assigns a permission, role, or resource role to this user.
     *
     * @param entitlement the entitlement to assign
     * @throws IllegalArgumentException if already assigned
     */
    public void assignEntitlement(Entitlement entitlement) {
        Objects.requireNonNull(entitlement, "entitlement");
        for (Entitlement assigned : entitlements) {
            if (assigned.getGuid().equals(entitlement.getGuid())) {
                throw new IllegalArgumentException("Duplicate entitlement: " + entitlement.getGuid());
            }
        }
        entitlements.add(entitlement);
    }

    /** Returns an immutable snapshot of the user's entitlements. */
    public Collection<Entitlement> getEntitlements() {
        return List.copyOf(entitlements);
    }
}
