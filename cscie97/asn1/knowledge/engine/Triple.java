// Triple.java

package cscie97.asn1.knowledge.engine;

import java.time.Instant;

/**
 * The Triple class represents a unique Triple consisting of a Subject,
 * Predicate, and Object within the KnowledgeGraph. A Triple contains references
 * to its associated Subject, Predicate, and Object. The Triple is uniquely
 * identified by the combination of their identifiers.
 *
 * @author Mauricio Gidi
 * Date: September 29, 2026
 */
public class Triple {

    // Private, unique, non-mutable identifier for the Triple. The identifier is
    // formed from the Subject, Predicate, and Object identifiers.
    private final String identifier;

    // Unix timestamp when the Triple was created.
    private final long createDate;

    // Private, non-mutable association to the associated Subject Node.
    private final Node subject;

    // Private, non-mutable association to the associated Predicate.
    private final Predicate predicate;

    // Private, non-mutable association to the associated Object Node.
    private final Node object;

    /**
     * Creates a Triple with the specified Subject, Predicate, and Object and
     * records its creation date.
     *
     * @param subject the Subject Node of the Triple
     * @param predicate the Predicate of the Triple
     * @param object the Object Node of the Triple
     */
    Triple(Node subject, Predicate predicate, Node object) {
        this.identifier = subject.getIdentifier() + " " + predicate.getIdentifier() + " " + object.getIdentifier();
        this.createDate = Instant.now().getEpochSecond();
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    /**
     * Returns the Triple identifier.
     *
     * @return the identifier of the Triple
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Returns the Triple creation date as a Unix timestamp.
     *
     * @return the creation date of the Triple
     */
    public long getCreateDate() {
        return this.createDate;
    }

    /**
     * Returns the Object identifier.
     *
     * @return the Object identifier
     */
    String getObjectIdentifier() {
        return object.getIdentifier();
    }

    /**
     * Returns the Predicate.
     *
     * @return the Predicate
     */
    public Predicate getPredicate() {
        return this.predicate;
    }

    /**
     * Returns the Object Node.
     *
     * @return the Object Node
     */
    public Node getObject() {
        return this.object;
    }

}