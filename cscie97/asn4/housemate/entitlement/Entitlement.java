package cscie97.asn4.housemate.entitlement;

import java.util.Objects;

/** Base type for permissions and role composites. */
public abstract class Entitlement implements Visitable {
    private final String guid;
    private final String name;
    private final String description;

    protected Entitlement(String guid, String name, String description) {
        this.guid = Objects.requireNonNull(guid, "guid");
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
    }

    public String getGuid() {
        return guid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    // acceptVisitor() is inherited from Visitable.
}