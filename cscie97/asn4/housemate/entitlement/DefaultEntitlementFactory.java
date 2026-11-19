package cscie97.asn4.housemate.entitlement;

/** Default factory for creating the modeled entitlement domain objects. */
public final class DefaultEntitlementFactory implements EntitlementFactory {

    @Override
    public Resource createResource(String guid, String description) {
        return new Resource(guid, description);
    }

    @Override
    public Permission createPermission(String guid, String name, String description) {
        return new Permission(guid, name, description);
    }

    @Override
    public Role createRole(String guid, String name, String description) {
        return new Role(guid, name, description);
    }

    @Override
    public ResourceRole createResourceRole(String guid, String name, String description, Role role, Resource resource) {
        return new ResourceRole(guid, name, description, role, resource);
    }

    @Override
    public User createUser(String guid, String name) {
        return new User(guid, name);
    }

    @Override
    public Credential createCredential(String guid, String userName, String passwordHash) {
        return new Credential(guid, userName, passwordHash);
    }

    @Override
    public VoicePrint createVoicePrint(String guid, String signature) {
        return new VoicePrint(guid, signature);
    }

    @Override
    public AccessToken createAccessToken(String guid, User user) {
        return new AccessToken(guid, user);
    }
}