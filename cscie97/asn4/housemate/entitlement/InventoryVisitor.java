// InventoryVisitor.java

package cscie97.asn4.housemate.entitlement;

/**
 * Collects an inventory of the Entitlement Service.
 *
 * @author Mauricio Gidi
 * @version November 17, 2026
 */
public final class InventoryVisitor extends EntitlementVisitor {

    private final StringBuilder inventory = new StringBuilder();

    /** Traverses the registered objects in a consistent order. */
    @Override
    public void visitEntitlementService(EntitlementServiceImp service) {

        for (User user : service.getUsers()) {
            user.acceptVisitor(this);
        }

        for (Resource resource : service.getResources()) {
            resource.acceptVisitor(this);
        }

        for (Entitlement entitlement : service.getEntitlements()) {
            entitlement.acceptVisitor(this);
        }

        for (AccessToken token : service.getAuthTokens()) {
            token.acceptVisitor(this);
        }
    }

    /** Records a user and its assigned entitlements. */
    @Override
    public void visitUser(User user) {
        add("User", user.getGuid(), user.getName());

        for (Entitlement entitlement : user.getEntitlements()) {
            inventory.append("  Assigned: ")
                    .append(entitlement.getGuid()).append('\n');
        }
    }

    /** Records a role and its direct children. */
    @Override
    public void visitRole(Role role) {
        add("Role", role.getGuid(), role.getName());

        for (Entitlement child : role.getEntitlements()) {
            inventory.append("  Includes: ")
                    .append(child.getGuid()).append('\n');
        }
    }

    /** Records a permission. */
    @Override
    public void visitPermission(Permission permission) {
        add("Permission", permission.getGuid(), permission.getDescription());
    }

    /** Records a Resource Role and its associated role and resource. */
    @Override
    public void visitResourceRole(ResourceRole role) {
        add("ResourceRole", role.getGuid(),
                role.getRole().getGuid() + " @ " + role.getResource().getGuid());
    }

    /** Records a resource. */
    @Override
    public void visitResource(Resource resource) {
        add("Resource", resource.getGuid(), resource.getDescription());
    }

    /** Records token status. */
    @Override
    public void visitAuthToken(AccessToken token) {
        inventory.append("Access: ").append(token.getUser().getGuid())
                .append(", active=").append(token.isActive())
                .append(", expires=").append(token.expiresAt())
                .append('\n');
    }

    /** Appends an inventory entry. */
    private void add(String kind, String id, String description) {
        inventory.append(kind).append(": ").append(id)
                .append(" - ").append(description).append('\n');
    }

    /** Returns the collected inventory. */
    public String getInventory() {
        return inventory.toString();
    }
}
