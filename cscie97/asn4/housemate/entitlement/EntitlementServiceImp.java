// EntitlementServiceImp.java

package cscie97.asn4.housemate.entitlement;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Singleton that manages HouseMate identities, entitlements, and access tokens.
 * 
 * @author Mauricio Gidi
 * @version November 17, 2026
 */
public final class EntitlementServiceImp implements EntitlementServiceApi {

    private static final EntitlementServiceImp INSTANCE = new EntitlementServiceImp();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int HASH_ITERATIONS = 120_000;

    private final EntitlementFactory factory = new DefaultEntitlementFactory();
    private final Map<String, AccessToken> authTokens = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Resource> resources = new HashMap<>();
    private final Map<String, Entitlement> entitlements = new HashMap<>();
    private String administratorId;

    private EntitlementServiceImp() {
    }

    /** Returns the shared Entitlement Service. */
    public static EntitlementServiceApi getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized void bootstrapAdministrator(
            String userId, String name, String username, String password) {

        if (!users.isEmpty()) {
            throw new AuthException("Administrator already initialized");
        }

        User user = factory.createUser(required(userId), required(name));
        user.setCredential(factory.createCredential(
                UUID.randomUUID().toString(), required(username), hashPassword(required(password))));
        users.put(userId, user);
        administratorId = userId;
    }

    @Override
    public synchronized void createUser(String adminTokenId, String userId, String name) {

        requireAdmin(adminTokenId);
        if (users.containsKey(required(userId))) {
            throw new AuthException("Duplicate user: " + userId, userId);
        }
        users.put(userId, factory.createUser(userId, required(name)));
    }

    @Override
    public synchronized void createRole(
            String adminTokenId, String roleId, String name, String description) {

        requireAdmin(adminTokenId);
        ensureNewEntitlement(roleId);
        entitlements.put(roleId, factory.createRole(roleId, required(name), required(description)));
    }

    @Override
    public synchronized void createPermission(
            String adminTokenId, String permissionId, String name, String description) {

        requireAdmin(adminTokenId);
        ensureNewEntitlement(permissionId);
        entitlements.put(permissionId,
                factory.createPermission(permissionId, required(name), required(description)));
    }

    @Override
    public synchronized void createResource(
            String adminTokenId, String resourceId, String description) {

        requireAdmin(adminTokenId);
        if (resources.containsKey(required(resourceId))) {
            throw new AuthException("Duplicate resource: " + resourceId, resourceId);
        }
        resources.put(resourceId, factory.createResource(resourceId, required(description)));
    }

    @Override
    public synchronized void createResourceRole(
            String adminTokenId, String resourceRoleId, String roleId, String resourceId) {

        requireAdmin(adminTokenId);
        ensureNewEntitlement(resourceRoleId);

        Role role = requireRole(roleId);
        Resource resource = resources.get(resourceId);

        if (resource == null) {
            throw new AuthException("Unknown resource: " + resourceId, resourceId);
        }
        entitlements.put(resourceRoleId, factory.createResourceRole(
                resourceRoleId, role.getName(), role.getDescription(), role, resource));
    }

    @Override
    public synchronized void addEntitlementToRole(
            String adminTokenId, String roleId, String entitlementId) {

        requireAdmin(adminTokenId);

        Role role = requireRole(roleId);
        Entitlement child = requireEntitlement(entitlementId);

        if (child instanceof ResourceRole) {
            throw new AuthException("Resource roles must be assigned directly to users", entitlementId);
        }
        try {
            role.addEntitlement(child);
        } catch (IllegalArgumentException e) {
            throw new AuthException(e.getMessage(), entitlementId);
        }
    }

    @Override
    public synchronized void addRoleToUser(String adminTokenId, String userId, String roleId) {
        requireAdmin(adminTokenId);
        assign(requireUser(userId), requireRole(roleId));
    }

    @Override
    public synchronized void addEntitlementToUser(
            String adminTokenId,
            String userId,
            String entitlementId) {

        requireAdmin(adminTokenId);
        assign(requireUser(userId), requireEntitlement(entitlementId));
    }

    @Override
    public synchronized void addResourceRoleToUser(
            String adminTokenId, String userId, String resourceRoleId) {

        requireAdmin(adminTokenId);

        Entitlement entitlement = requireEntitlement(resourceRoleId);
        if (!(entitlement instanceof ResourceRole)) {
            throw new AuthException("Not a resource role: " + resourceRoleId, resourceRoleId);
        }
        assign(requireUser(userId), entitlement);
    }

    @Override
    public synchronized void addUserCredential(
            String adminTokenId, String userId, String credentialType, String value) {

        requireAdmin(adminTokenId);
        User user = requireUser(userId);
        String credential = required(value);

        switch (required(credentialType).toLowerCase()) {
            case "password" -> {
                if (user.getCredential() != null || getUserByUsername(userId) != null) {
                    throw new AuthException("Password credential already registered: " + userId, userId);
                }
                user.setCredential(factory.createCredential(
                        UUID.randomUUID().toString(), userId, hashPassword(credential)));
            }
            case "voice_print" -> {
                if (user.getVoicePrint() != null || getUserByVoicePrint(credential) != null) {
                    throw new AuthException("Voiceprint already registered: " + userId, userId);
                }
                user.setVoicePrint(factory.createVoicePrint(
                        UUID.randomUUID().toString(), credential));
            }
            default -> throw new AuthException("Unknown credential type: " + credentialType);
        }
    }

    @Override
    public synchronized String loginWithUsername(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null || password == null
                || !matchesPassword(password, user.getCredential().getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }
        return createAuthToken(user);
    }

    @Override
    public synchronized String loginWithVoicePrint(String voicePrint) {

        User user = getUserByVoicePrint(voicePrint);
        if (user == null) {
            throw new AuthenticationException("Unrecognized voiceprint");
        }
        return createAuthToken(user);
    }

    @Override
    public synchronized void logout(String tokenId) {
        validateToken(tokenId).invalidate();
    }

    @Override
    public synchronized boolean checkAccess(
            String tokenId, String permissionId, String resourceId) {

        AccessVisitor visitor = new AccessVisitor(
                tokenId, required(permissionId), required(resourceId));

        validateToken(tokenId);

        if (!"housemate".equals(resourceId)
                && !resources.containsKey(resourceId)) {
            throw new AccessDeniedException(
                    "Unknown resource: " + resourceId,
                    permissionId,
                    resourceId);
        }

        acceptVisitor(visitor);

        if (!visitor.isAllowed()) {
            throw new AccessDeniedException(
                    "Access denied: " + permissionId + " on " + resourceId,
                    permissionId, resourceId);
        }

        return true;
    }

    @Override
    public synchronized String getInventory(String adminTokenId) {

        requireAdmin(adminTokenId);
        InventoryVisitor visitor = new InventoryVisitor();
        acceptVisitor(visitor);
        return visitor.getInventory();
    }

    /** Returns registered users for package-local visitor traversal. */
    synchronized Collection<User> getUsers() {

        return List.copyOf(users.values());
    }

    /** Returns registered resources for package-local visitor traversal. */
    synchronized Collection<Resource> getResources() {

        return List.copyOf(resources.values());
    }

    /** Returns registered entitlements for package-local visitor traversal. */
    synchronized Collection<Entitlement> getEntitlements() {

        return List.copyOf(entitlements.values());
    }

    /** Returns issued tokens for package-local visitor traversal. */
    synchronized Collection<AccessToken> getAuthTokens() {

        return List.copyOf(authTokens.values());
    }

    /** Identifies the original administrator for authorization traversal. */
    synchronized boolean isAdministrator(User user) {

        return user != null && user.getGuid().equals(administratorId);
    }

    /** Finds and validates a token without extending its lifetime. */
    synchronized AccessToken validateToken(String tokenId) {

        AccessToken token = authTokens.get(tokenId);
        if (token == null || !token.isValid()) {
            throw new InvalidAccessTokenException("Invalid or expired access token", "invalid");
        }
        return token;
    }

    private void requireAdmin(String tokenId) {

        checkAccess(tokenId, "user_admin", "housemate");
    }

    private User getUserByUsername(String username) {

        if (username == null) {
            return null;
        }
        for (User user : users.values()) {
            if (user.getCredential() != null
                    && user.getCredential().getUserName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private User getUserByVoicePrint(String voicePrint) {

        if (voicePrint == null) {
            return null;
        }
        for (User user : users.values()) {
            if (user.getVoicePrint() != null
                    && user.getVoicePrint().getSignature().equals(voicePrint)) {
                return user;
            }
        }
        return null;
    }

    private String createAuthToken(User user) {

        String id = UUID.randomUUID().toString();
        authTokens.put(id, factory.createAccessToken(id, user));
        return id;
    }

    private void ensureNewEntitlement(String id) {

        if (entitlements.containsKey(required(id))) {
            throw new AuthException("Duplicate entitlement: " + id, id);
        }
    }

    private Entitlement requireEntitlement(String id) {

        Entitlement entitlement = entitlements.get(id);
        if (entitlement == null) {
            throw new AuthException("Unknown entitlement: " + id, id);
        }
        return entitlement;
    }

    private Role requireRole(String id) {

        Entitlement entitlement = requireEntitlement(id);
        if (!(entitlement instanceof Role role) || role instanceof ResourceRole) {
            throw new AuthException("Unknown role: " + id, id);
        }
        return role;
    }

    private User requireUser(String id) {

        User user = users.get(id);
        if (user == null) {
            throw new AuthException("Unknown user: " + id, id);
        }
        return user;
    }

    private static void assign(User user, Entitlement entitlement) {

        try {
            user.assignEntitlement(entitlement);
        } catch (IllegalArgumentException e) {
            throw new AuthException(e.getMessage(), entitlement.getGuid());
        }
    }

    private static String required(String value) {

        if (value == null || value.isBlank()) {
            throw new AuthException("Required value is missing");
        }
        return value;
    }

    private static String hashPassword(String password) {

        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(derive(password, salt));
    }

    private static boolean matchesPassword(String password, String stored) {

        String[] parts = stored.split(":", 2);

        if (parts.length != 2) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);
            return MessageDigest.isEqual(expected, derive(password, salt));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derive(String password, byte[] salt) {

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, 256);

        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing is unavailable", e);
        } finally {
            spec.clearPassword();
        }
    }
}
