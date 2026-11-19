// Role.java

package cscie97.asn4.housemate.entitlement;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A composite of permissions and other roles.
 * 
 * @author Mauricio Gidi
 * @version November 16, 2026
 */
public class Role extends Entitlement {
    private final Set<Entitlement> children = new LinkedHashSet<>();

    /** Creates a role with a unique identifier and description. */
    public Role(String guid, String name, String description) {
        super(guid, name, description);
    }

    /**
     * Adds a permission or nested role, rejecting duplicates and cycles.
     *
     * @param entitlement the entitlement to add
     * @throws IllegalArgumentException for duplicate or cyclic membership
     */
    public void addEntitlement(Entitlement entitlement) {
        Objects.requireNonNull(entitlement, "entitlement");

        if (entitlement == this
                || (entitlement instanceof Role role && role.containsRole(this))) {
            throw new IllegalArgumentException("Cyclic role: " + entitlement.getGuid());
        }

        for (Entitlement child : children) {
            if (child.getGuid().equals(entitlement.getGuid())) {
                throw new IllegalArgumentException("Duplicate entitlement: " + entitlement.getGuid());
            }
        }
        children.add(entitlement);
    }

    /** Returns an immutable snapshot of direct child entitlements. */
    public Collection<Entitlement> getEntitlements() {
        return List.copyOf(children);
    }

    private boolean containsRole(Role target) {
        if (this == target) {
            return true;
        }
        for (Entitlement child : children) {
            if (child instanceof Role role && role.containsRole(target)) {
                return true;
            }
        }
        return false;
    }
}
