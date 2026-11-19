// Node.java

package cscie97.asn1.knowledge.engine;

import java.time.Instant;

/**
 * The Node class represents instances of Subjects and Objects within the
 * KnowledgeGraph. Each Node has a unique String identifier. A single Node
 * instance can represent both a Subject and an Object within the Knowledge Graph.
 * Node identifiers are case-insensitive.
 *
 * @author Mauricio Gidi
 * Date: September 8, 2026
 */
public class Node {

    // Private, unique, non-mutable identifier for the Node. Node identifiers are
    // case-insensitive.
    private final String identifier;

    // Unix timestamp when the Node was created.
    private final long createDate;

    /**
     * Creates a Node with the specified identifier and records its creation date.
     *
     * @param identifier the identifier of the Node
     */
    Node(String identifier) {
        this.identifier = identifier;
        this.createDate = Instant.now().getEpochSecond();
    }

    /**
     * Returns the Node identifier.
     *
     * @return the identifier of the Node
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Returns the Node creation date as a Unix timestamp.
     *
     * @return the creation date of the Node
     */
    public long getCreateDate() {
        return this.createDate;
    }

}