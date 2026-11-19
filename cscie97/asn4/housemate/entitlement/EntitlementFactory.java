package cscie97.asn4.housemate.entitlement;

/**
 * Abstract Factory contract.
 */
public interface EntitlementFactory {
    Resource createResource(String guid, String description);

    Permission createPermission(String guid, String name, String description);

    Role createRole(String guid, String name, String description);

    ResourceRole createResourceRole(String guid, String name, String description, Role role, Resource resource);

    User createUser(String guid, String name);

    Credential createCredential(String guid, String userName, String passwordHash);

    VoicePrint createVoicePrint(String guid, String signature);

    AccessToken createAccessToken(String guid, User user);
}