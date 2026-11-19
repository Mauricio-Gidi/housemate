package cscie97.asn4.housemate.entitlement;

import java.util.Objects;

/**
 * A domain object participating in the Entitlement Visitor pattern.
 * This single inherited default method performs type-specific dispatch;
 * participating concrete classes must not redeclare acceptVisitor().
 */
public interface Visitable {
    /** Dispatch to the visitor overload for the runtime domain type. */
    default void acceptVisitor(EntitlementVisitor visitor) {
        Objects.requireNonNull(visitor, "visitor");
        if (this instanceof EntitlementServiceImp) {
            visitor.visitEntitlementService((EntitlementServiceImp) this);
        } else if (this instanceof ResourceRole) {
            // Check before Role: ResourceRole extends Role.
            visitor.visitResourceRole((ResourceRole) this);
        } else if (this instanceof Role) {
            visitor.visitRole((Role) this);
        } else if (this instanceof Permission) {
            visitor.visitPermission((Permission) this);
        } else if (this instanceof User) {
            visitor.visitUser((User) this);
        } else if (this instanceof Resource) {
            visitor.visitResource((Resource) this);
        } else if (this instanceof AccessToken) {
            visitor.visitAuthToken((AccessToken) this);
        } else {
            throw new IllegalStateException(
                    "Unsupported Visitable type: " + getClass().getName());
        }
    }
}