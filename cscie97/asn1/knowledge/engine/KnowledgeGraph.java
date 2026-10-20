// KnowledgeGraph.java

package cscie97.asn1.knowledge.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The KnowledgeGraph manages the set of active Nodes, Predicates, and Triples.
 * Per the requirements, the active Triples are assumed to fit within available
 * memory, and no persistence is required.
 *
 * The KnowledgeGraph is a singleton, meaning there is only one instance of this
 * class. The static getInstance() method provides access to the single
 * KnowledgeGraph instance.
 *
 * The importTriple() method adds a fully qualified Triple to the KnowledgeGraph.
 * Triples are unique based on the combination of Subject, Predicate, and Object.
 *
 * The executeQuery() method executes queries specified by a Subject, Predicate,
 * and Object. The special “?” identifier represents a wildcard. Matching Triples
 * are located through the queryMapSet association and returned as a Set.
 *
 * To improve query performance, queryMapSet stores all possible combinations of
 * Subject, Predicate, and Object identifiers replaced with “?”. When a new Triple
 * is added, all eight query permutations are computed and associated with that
 * Triple.
 *
 * @author Mauricio Gidi
 * Date: September 29, 2026
 */
public class KnowledgeGraph {

    private static KnowledgeGraph instance;

    // Private association for maintaining the active set of Nodes (i.e., Subjects and/or
    // Objects). The map key is the node identifier, and the value is the associated
    // Node. Node identifiers are case-insensitive.
    private final Map<String, Node> nodeMap = new HashMap<>();

    // Private association for maintaining the active set of Predicates. Map key is the
    // predicate identifier, and value is the associated Predicate. Predicate identifiers
    // are case-insensitive.
    private final Map<String, Predicate> predicateMap = new HashMap<>();

    // Private association for maintaining the active set of Triples. The map key is the
    // Triple identifier, and the value is the associated Triple.
    private final Map<String, Triple> tripleMap = new HashMap<>();

    // Private association for maintaining a fast query lookup map. Map key is the query
    // string (e.g., “Bill ? ?”), and value is a Set of matching Triples.
    private final Map<String, Set<Triple>> queryMapSet = new HashMap<>();

    /**
     * Private constructor used to enforce the Singleton design pattern.
     */
    private KnowledgeGraph() {}

    /**
     * Adds a Triple to the KnowledgeGraph.
     * Gets the associated Subject, Predicate, and Object instances and ensures
     * that only one Triple exists for each unique combination of the three.
     *
     * @param subjectIdentifier the identifier of the Subject
     * @param predicateIdentifier the identifier of the Predicate
     * @param objectIdentifier the identifier of the Object
     */
    public void importTriple(String subjectIdentifier, String predicateIdentifier, String objectIdentifier) {

        Node subject = getNode(subjectIdentifier);
        Predicate predicate = getPredicate(predicateIdentifier);
        Node object = getNode(objectIdentifier);

        getTriple( subject, predicate, object );

    }

    /**
     * Executes a query against the KnowledgeGraph using the queryMapSet.
     * Each query field may contain an identifier or the “?” wildcard.
     * Query identifiers are case-insensitive. If no matching Triples are found,
     * an empty Set is returned.
     *
     * @param subject the Subject identifier or “?” wildcard
     * @param predicate the Predicate identifier or “?” wildcard
     * @param object the Object identifier or “?” wildcard
     * @return a Set containing the Triples that match the query
     */
    public Set<Triple> executeQuery(String subject, String predicate, String object) {

        String query = subject.toLowerCase() + " " + predicate.toLowerCase() + " " + object.toLowerCase();
        Set<Triple> set = queryMapSet.get(query);

        if (set == null) {
            return new HashSet<>();
        } else {
            return new HashSet<>(set);
        }
    }

    /**
     * Returns a reference to the single static instance of the KnowledgeGraph.
     * Creates the instance the first time this method is called.
     *
     * @return the single KnowledgeGraph instance
     */
    public static KnowledgeGraph getInstance() {

        if (instance == null) {
            instance = new KnowledgeGraph();
        }
        return instance;
    }

    /**
     * Returns the Node instance for the given identifier.
     * Uses nodeMap to look up the Node. If the Node does not exist, creates it
     * and adds it to nodeMap. Node identifiers are case-insensitive.
     *
     * @param identifier the identifier of the Node
     * @return the Node associated with the identifier
     */
    public Node getNode(String identifier) {

        identifier = identifier.toLowerCase();
        Node node = nodeMap.get(identifier);
        if (node == null) {
            node = new Node(identifier);
            nodeMap.put(identifier, node);
        }
        return node;
    }

    /**
     * Returns the Predicate instance for the given identifier.
     * Uses predicateMap to look up the Predicate. If the Predicate does not exist,
     * creates it and adds it to predicateMap. Predicate identifiers are
     * case-insensitive.
     *
     * @param identifier the identifier of the Predicate
     * @return the Predicate associated with the identifier
     */
    public Predicate getPredicate(String identifier) {

        identifier = identifier.toLowerCase();
        Predicate predicate = predicateMap.get(identifier);
        if (predicate == null) {
            predicate = new Predicate(identifier);
            predicateMap.put(identifier, predicate);
        }
        return predicate;
    }

    /**
     * Returns the Triple instance for the given Subject, Predicate, and Object.
     * Uses tripleMap to look up the Triple. If the Triple does not exist, creates it,
     * adds it to tripleMap, and updates queryMapSet with all eight combinations of
     * identifiers and “?” wildcards.
     *
     * @param subject the Subject Node of the Triple
     * @param predicate the Predicate of the Triple
     * @param object the Object Node of the Triple
     * @return the Triple associated with the Subject, Predicate, and Object
     */
    public Triple getTriple(Node subject, Predicate predicate, Node object) {

        String tripleIdentifier = subject.getIdentifier() + " " + predicate.getIdentifier() + " " + object.getIdentifier();
        Triple triple = tripleMap.get(tripleIdentifier);

        if ( triple == null ) {
            triple = new Triple(subject, predicate, object);
            tripleMap.put(tripleIdentifier, triple);

            // Index the Triple under all eight combinations of identifiers and wildcards.
            for (int i = 0; i < 2 ; i++) {
                for (int j = 0; j < 2 ; j++) {
                    for (int k = 0; k < 2 ; k++) {

                        String subjectIdentifier = (i==0) ? subject.getIdentifier() : "?";
                        String predicateIdentifier = (j==0) ? predicate.getIdentifier() : "?";
                        String objectIdentifier = (k==0) ? object.getIdentifier() : "?";

                        String query = subjectIdentifier + " " + predicateIdentifier + " " + objectIdentifier;

                        Set<Triple> set = queryMapSet.get(query);
                        if (set == null) {
                            set = new HashSet<>();
                        }
                        set.add(triple);
                        queryMapSet.put(query, set);
                    }
                }
            }
        }
        return triple;
    }

    /**
     * Removes a Triple from the KnowledgeGraph.
     *
     * @param subjectIdentifier the identifier of the Subject
     * @param predicateIdentifier the identifier of the Predicate
     * @param objectIdentifier the identifier of the Object
     */
    public void deleteTriple(
        String subjectIdentifier,
        String predicateIdentifier,
        String objectIdentifier
    ) {
        subjectIdentifier = subjectIdentifier.toLowerCase();
        predicateIdentifier = predicateIdentifier.toLowerCase();
        objectIdentifier = objectIdentifier.toLowerCase();
        String tripleIdentifier = subjectIdentifier + " " + predicateIdentifier + " " + objectIdentifier;

        Triple triple = tripleMap.remove(tripleIdentifier);

        if (triple == null) {
            return;
        }

        // Remove the Triple from all eight query combinations.
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {

                    String subject = (i == 0) ? subjectIdentifier : "?";

                    String predicate = (j == 0) ? predicateIdentifier : "?";

                    String object = (k == 0) ? objectIdentifier : "?";

                    String query = subject + " " + predicate + " " + object;

                    Set<Triple> set = queryMapSet.get(query);

                    if (set != null) {

                        set.remove(triple);

                        if (set.isEmpty()) {
                            queryMapSet.remove(query);
                        }
                    }
                }
            }
        }



    }

    /**
     * Replaces the current value for a Subject and Predicate.
     *
     * @param subjectIdentifier the identifier of the Subject
     * @param predicateIdentifier the identifier of the Predicate
     * @param objectIdentifier the new Object identifier
     */
    public void updateTriple(
        String subjectIdentifier,
        String predicateIdentifier,
        String objectIdentifier
    ) {

        Set<Triple> existingTriples = executeQuery(
            subjectIdentifier,
            predicateIdentifier,
            "?"
        );

        for (Triple triple : existingTriples) {

            deleteTriple(
                subjectIdentifier,
                predicateIdentifier,
                triple.getObjectIdentifier()
            );
        }

        importTriple(
            subjectIdentifier,
            predicateIdentifier,
            objectIdentifier
        );
    }

}