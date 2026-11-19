// AccessVisitor.java

package cscie97.asn4.housemate.entitlement;

/**
 * Traverses entitlements to determine whether a user has access.
 *
 * @author Mauricio Gidi
 * @version November 17, 2026
 */
public final class AccessVisitor extends EntitlementVisitor {

    private final String tokenId;
    private final String permissionId;
    private final String resourceId;
    private boolean allowed;
    private boolean inScope;

    /** Creates a visitor for the requested permission and resource. */
    public AccessVisitor(String tokenId, String permissionId, String resourceId) {
        this.tokenId = tokenId;
        this.permissionId = permissionId;
        this.resourceId = resourceId;
    }

    /** Starts traversal at the Entitlement Service. */
    @Override
    public void visitEntitlementService(EntitlementServiceImp service) {

        AccessToken token = service.validateToken(tokenId);

        if (service.isAdministrator(token.getUser())) {
            allowed = true;
        } else {
            token.acceptVisitor(this);
        }

        if (allowed) {
            token.refreshExpiration();
        }
    }

    /** Traverses from the token to its user. */
    @Override
    public void visitAuthToken(AccessToken token) {
        token.getUser().acceptVisitor(this);
    }

    /** Checks the user's assigned entitlements. */
    @Override
    public void visitUser(User user) {
        for (Entitlement entitlement : user.getEntitlements()) {
            entitlement.acceptVisitor(this);
            if (allowed) {
                return;
            }
        }
    }

    /** Recursively visits the permissions and roles within a role. */
    @Override
    public void visitRole(Role role) {
        for (Entitlement entitlement : role.getEntitlements()) {
            entitlement.acceptVisitor(this);
            if (allowed) {
                return;
            }
        }
    }

    /** Checks whether the required permission matches. */
    @Override
    public void visitPermission(Permission permission) {
        if (permissionId.equals(permission.getGuid())) {
            allowed = true;
        }
    }

    /** Checks resource scope before visiting the referenced role. */
    @Override
    public void visitResourceRole(ResourceRole role) {
        role.getResource().acceptVisitor(this);

        if (inScope) {
            role.getRole().acceptVisitor(this);
        }
    }

    /** Determines whether the requested resource is within the role's scope. */
    @Override
    public void visitResource(Resource resource) {
        String scope = resource.getGuid();
        inScope = resourceId.equals(scope)
                || resourceId.startsWith(scope + ":");
    }

    /** Returns the authorization decision. */
    public boolean isAllowed() {
        return allowed;
    }
}
