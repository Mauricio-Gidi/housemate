package cscie97.asn4.housemate.entitlement;

import java.util.Objects;

/** A role restricted to the supplied resource scope. */
public final class ResourceRole extends Role {
    private final Role role;
    private final Resource resource;

    public ResourceRole(String guid, String name, String description,
            Role role, Resource resource) {
        super(guid, name, description);
        this.role = Objects.requireNonNull(role, "role");
        this.resource = Objects.requireNonNull(resource, "resource");
    }

    public Role getRole() {
        return role;
    }

    public Resource getResource() {
        return resource;
    }
}