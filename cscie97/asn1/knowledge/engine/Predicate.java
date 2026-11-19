// Predicate.java

package cscie97.asn1.knowledge.engine;

import java.time.Instant;

/**
 * The Predicate class represents the Predicate portion of a Triple within the
 * KnowledgeGraph. Each Predicate has a unique String identifier.
 * Predicate identifiers are case-insensitive.
 *
 * @author Mauricio Gidi
 * Date: September 8, 2026
 */
public class Predicate {

    // Private, unique, non-mutable identifier for the Predicate. Predicate identifiers are
    // case-insensitive.
    private final String identifier;

    // Unix timestamp when the Predicate was created.
    private final long createDate;

    /**
     * Creates a Predicate with the specified identifier and records its creation date.
     *
     * @param identifier the identifier of the Predicate
     */
    Predicate(String identifier) {
        this.identifier = identifier;
        this.createDate = Instant.now().getEpochSecond();
    }

    /**
     * Returns the Predicate identifier.
     *
     * @return the identifier of the Predicate
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Returns the Predicate creation date as a Unix timestamp.
     *
     * @return the creation date of the Predicate
     */
    public long getCreateDate() {
        return this.createDate;
    }

}