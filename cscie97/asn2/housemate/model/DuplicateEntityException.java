package cscie97.asn2.housemate.model;

/**
 * Thrown when an entity already exists.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class DuplicateEntityException extends HousemateModelServiceException {

    private final String entityType;
    private final String identifier;

    /**
     * Creates a duplicate-entity exception.
     *
     * @param entityType type of entity that already exists
     * @param identifier identifier of the existing entity
     */
    public DuplicateEntityException(
        String entityType,
        String identifier
    ) {
        super(
            entityType + " already exists: " + identifier
        );

        this.entityType = entityType;
        this.identifier = identifier;
    }

    /**
     * Returns the entity type.
     *
     * @return type of the duplicate entity
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Returns the entity identifier.
     *
     * @return identifier of the duplicate entity
     */
    public String getIdentifier() {
        return identifier;
    }
}