package cscie97.asn4.housemate.entitlement;

/** Insufficient entitlement for the requested permission/resource. */
public final class AccessDeniedException extends AuthException {
    private final String permissionId;
    private final String resourceId;

    public AccessDeniedException(String message) {
        this(message, null, null);
    }

    public AccessDeniedException(String message, String permissionId, String resourceId) {
        super(message);
        this.permissionId = permissionId;
        this.resourceId = resourceId;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public String getResourceId() {
        return resourceId;
    }
}