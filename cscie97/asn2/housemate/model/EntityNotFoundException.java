package cscie97.asn2.housemate.model;

/**
 * Thrown when a requested entity cannot be found.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class EntityNotFoundException extends HousemateModelServiceException {

    private final String entityType;
    private final String identifier;

    /**
     * Creates an entity-not-found exception.
     *
     * @param entityType type of entity that could not be found
     * @param identifier identifier of the missing entity
     */
    public EntityNotFoundException(
        String entityType,
        String identifier
    ) {
        super(
            entityType + " not found: " + identifier
        );

        this.entityType = entityType;
        this.identifier = identifier;
    }

    /**
     * Returns the entity type.
     *
     * @return type of the missing entity
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Returns the entity identifier.
     *
     * @return identifier of the missing entity
     */
    public String getIdentifier() {
        return identifier;
    }
}