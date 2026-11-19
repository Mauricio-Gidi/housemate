package cscie97.asn4.housemate.entitlement;

import java.util.Objects;

/** A physical or logical HouseMate resource. */
public final class Resource implements Visitable {
    private final String guid;
    private final String description;

    public Resource(String guid, String description) {
        this.guid = Objects.requireNonNull(guid, "guid");
        this.description = Objects.requireNonNull(description, "description");
    }

    public String getGuid() {
        return guid;
    }

    public String getDescription() {
        return description;
    }
}