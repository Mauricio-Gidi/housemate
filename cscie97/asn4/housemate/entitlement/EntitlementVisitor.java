package cscie97.asn4.housemate.entitlement;

/** Visitor contract for inventory and authorization traversal. */
public abstract class EntitlementVisitor {
    public abstract void visitEntitlementService(EntitlementServiceImp service);

    public abstract void visitUser(User user);

    public abstract void visitRole(Role role);

    public abstract void visitResource(Resource resource);

    public abstract void visitAuthToken(AccessToken authToken);

    public abstract void visitPermission(Permission permission);

    public abstract void visitResourceRole(ResourceRole resourceRole);
}