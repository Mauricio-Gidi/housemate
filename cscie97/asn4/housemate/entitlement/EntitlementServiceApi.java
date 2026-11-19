// EntitlementServiceApi.java

package cscie97.asn4.housemate.entitlement;

/**
 * Public contract for HouseMate identity, entitlement administration,
 * authentication, authorization, and inventory.
 *
 * Token arguments are opaque String IDs. The implementation resolves
 * each ID to an AccessToken and validates its state before restricted
 * operations.
 *
 * The first administrator must be created using a controlled bootstrap
 * mechanism before these administration methods are available.
 *
 * @author Mauricio Gidi
 * @version November 17, 2026
 */
public interface EntitlementServiceApi extends Visitable {

        /**
         * Creates the first administrator exactly once, before any users exist.
         * Repeated initialization must be rejected.
         *
         * @param userId   administrator user ID
         * @param name     administrator name
         * @param username administrator login name
         * @param password administrator password
         * @throws AuthException if already initialized or credentials are invalid
         */
        void bootstrapAdministrator(
                        String userId,
                        String name,
                        String username,
                        String password) throws AuthException;

        /**
         * Registers a new user.
         *
         * @param adminTokenId authorized administrator token
         * @param userId       unique user identifier
         * @param name         user display name
         * @throws AuthException if creation is not permitted
         */
        void createUser(
                        String adminTokenId,
                        String userId,
                        String name) throws AuthException;

        /**
         * Creates a new role.
         *
         * @param adminTokenId administrator token
         * @param roleId       unique role ID
         * @param name         role name
         * @param description  role description
         * @throws AuthException if creation fails
         */
        void createRole(
                        String adminTokenId,
                        String roleId,
                        String name,
                        String description) throws AuthException;

        /**
         * Creates a new permission.
         *
         * @param adminTokenId administrator token
         * @param permissionId unique permission ID
         * @param name         permission name
         * @param description  permission description
         * @throws AuthException if creation fails
         */
        void createPermission(
                        String adminTokenId,
                        String permissionId,
                        String name,
                        String description) throws AuthException;

        /**
         * Creates a resource representing a protected HouseMate entity.
         *
         * @param adminTokenId administrator token
         * @param resourceId   unique resource ID
         * @param description  resource description
         * @throws AuthException if creation fails
         */
        void createResource(
                        String adminTokenId,
                        String resourceId,
                        String description) throws AuthException;

        /**
         * Creates a resource-scoped role.
         *
         * @param adminTokenId   administrator token
         * @param resourceRoleId unique resource-role ID
         * @param roleId         existing role
         * @param resourceId     resource defining the scope
         * @throws AuthException if creation fails
         */
        void createResourceRole(
                        String adminTokenId,
                        String resourceRoleId,
                        String roleId,
                        String resourceId) throws AuthException;

        /**
         * Adds a role or permission to another role.
         * The implementation must prevent cyclic role relationships.
         *
         * @param adminTokenId  administrator token
         * @param roleId        target role
         * @param entitlementId child entitlement
         * @throws AuthException if the operation fails
         */
        void addEntitlementToRole(
                        String adminTokenId,
                        String roleId,
                        String entitlementId) throws AuthException;

        /**
         * Assigns a role to a user.
         *
         * @param adminTokenId administrator token
         * @param userId       target user
         * @param roleId       role to assign
         * @throws AuthException if assignment fails
         */
        void addRoleToUser(
                        String adminTokenId,
                        String userId,
                        String roleId) throws AuthException;

        /**
         * Assigns a Role or Permission directly to a user.
         *
         * @param adminTokenId  administrator token
         * @param userId        target user
         * @param entitlementId Role or Permission ID
         * @throws AuthException if the assignment fails
         */
        void addEntitlementToUser(
                        String adminTokenId,
                        String userId,
                        String entitlementId) throws AuthException;

        /**
         * Assigns a resource-scoped role to a user.
         *
         * @param adminTokenId   administrator token
         * @param userId         target user
         * @param resourceRoleId resource role to assign
         * @throws AuthException if assignment fails
         */
        void addResourceRoleToUser(
                        String adminTokenId,
                        String userId,
                        String resourceRoleId) throws AuthException;

        /**
         * Registers a user's credential.
         *
         * Supported credential types are "password" and "voice_print".
         * Passwords must be salted and hashed by the implementation.
         *
         * @param adminTokenId   administrator token
         * @param userId         target user
         * @param credentialType credential type
         * @param value          credential value
         * @throws AuthException if registration fails
         */
        void addUserCredential(
                        String adminTokenId,
                        String userId,
                        String credentialType,
                        String value) throws AuthException;

        /**
         * Authenticates a user with username and password.
         *
         * @param username username
         * @param password supplied password
         * @return ID of a new access token
         * @throws AuthenticationException if authentication fails
         */
        String loginWithUsername(
                        String username,
                        String password) throws AuthenticationException;

        /**
         * Authenticates a user with a simulated voiceprint.
         *
         * @param voicePrint voice signature, such as "--sam--"
         * @return ID of a new access token
         * @throws AuthenticationException if authentication fails
         */
        String loginWithVoicePrint(
                        String voicePrint) throws AuthenticationException;

        /**
         * Invalidates the specified access token.
         *
         * @param tokenId access token ID
         * @throws InvalidAccessTokenException if the token is invalid
         */
        void logout(
                        String tokenId) throws InvalidAccessTokenException;

        /**
         * Checks permission to access the specified resource.
         *
         * The implementation validates the token and traverses the user's
         * permissions and nested roles, respecting resource restrictions.
         *
         * @param tokenId      access token ID
         * @param permissionId required permission ID
         * @param resourceId   target resource ID
         * @return true when access is authorized
         * @throws InvalidAccessTokenException for invalid or expired tokens
         * @throws AccessDeniedException       when permission is denied
         */
        boolean checkAccess(
                        String tokenId,
                        String permissionId,
                        String resourceId)
                        throws InvalidAccessTokenException, AccessDeniedException;

        /**
         * Returns an inventory of the Entitlement Service.
         * The inventory must not expose passwords or token secrets.
         *
         * @param adminTokenId administrator token
         * @return formatted inventory
         * @throws AuthException if the request is unauthorized
         */
        String getInventory(
                        String adminTokenId) throws AuthException;

        // acceptVisitor(EntitlementVisitor) is inherited from Visitable.
}
