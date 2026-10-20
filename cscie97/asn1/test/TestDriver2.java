// TestDriver2.java

package cscie97.asn1.test;

import java.util.Set;

import cscie97.asn1.knowledge.engine.KnowledgeGraph;
import cscie97.asn1.knowledge.engine.Node;
import cscie97.asn1.knowledge.engine.Predicate;
import cscie97.asn1.knowledge.engine.Triple;

/**
 * The TestDriver2 class provides additional tests for the KnowledgeGraph.
 *
 * @author Mauricio Gidi
 * Date: September 11, 2026
 */
public class TestDriver2 {

    /**
     * Tests KnowledgeGraph operations and prints the expected and actual results.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        KnowledgeGraph kGraph = KnowledgeGraph.getInstance();

        kGraph.importTriple("Joe", "plays_sport", "Hockey");
        kGraph.importTriple("Joe", "plays_sport", "Soccer");
        kGraph.importTriple("Joe", "works_at", "Starbucks");
        kGraph.importTriple("Lucy", "works_at", "Starbucks");
        kGraph.importTriple("Lucy", "plays_sport", "Soccer");
        kGraph.importTriple("Lucy", "works_at", "HarvardCoop");
        kGraph.importTriple("Starbucks", "sells", "Coffee");
        kGraph.importTriple("Starbucks", "sells", "Cake");
        kGraph.importTriple("Starbucks", "has_store_location", "HarvardSquare");
        kGraph.importTriple("HarvardCoop", "sells", "Coffee");
        kGraph.importTriple("HarvardCoop", "has_store_location", "HarvardSquare");

        kGraph.importTriple("Joe", "plays_sport", "Hockey");
        kGraph.importTriple("JOE", "PLAYS_SPORT", "HOCKEY");

        KnowledgeGraph kGraph2 = KnowledgeGraph.getInstance();

        System.out.println();
        System.out.println("Same KnowledgeGraph instance");
        System.out.println("Expected: True");
        System.out.println("Result: " + (kGraph == kGraph2));

        System.out.println();
        Node joe1 = kGraph.getNode("Joe");
        Node joe2 = kGraph.getNode("JOE");
        System.out.println("Same node for Joe and JOE");
        System.out.println("Expected: True");
        System.out.println("Result: " + (joe1 == joe2));

        System.out.println();
        Predicate plays_sport1 = kGraph.getPredicate("plays_sport");
        Predicate plays_sport2 = kGraph.getPredicate("PLAYS_SPORT");
        System.out.println("Same predicate for plays_sport and PLAYS_SPORT");
        System.out.println("Expected: True");
        System.out.println("Result: " + (plays_sport1 == plays_sport2));

        System.out.println();
        Triple triple1 = kGraph.getTriple(kGraph.getNode("Joe"), kGraph.getPredicate("plays_sport"), kGraph.getNode("Hockey"));
        Triple triple2 = kGraph.getTriple(kGraph.getNode("JOE"), kGraph.getPredicate("PLAYS_SPORT"), kGraph.getNode("HOCKEY"));
        System.out.println("Same triple");
        System.out.println("Expected: True");
        System.out.println("Result: " + (triple1 == triple2));

        System.out.println();
        System.out.println("Joe identifier: " + joe1.getIdentifier());
        System.out.println("Plays sport identifier: " + plays_sport1.getIdentifier());
        System.out.println("Triple identifier: " + triple1.getIdentifier());

        System.out.println();
        System.out.println("Joe timestamp: " + joe1.getCreateDate());
        System.out.println("Plays sport timestamp: " + plays_sport1.getCreateDate());
        System.out.println("Triple timestamp: " + triple1.getCreateDate());

        System.out.println();
        Set<Triple> triples = kGraph.executeQuery("?", "?", "?");
        System.out.println("Number of Triples");
        System.out.println("Expected: 11");
        System.out.println("Result: " + triples.size());

        System.out.println();
        Set<Triple> query = kGraph.executeQuery("JOE", "PLAYS_SPORT", "HOCKEY");
        System.out.println("JOE PLAYS_SPORT HOCKEY");
        System.out.println("Expected: 1");
        System.out.println("Result: " + query.size());

        System.out.println();
        Set<Triple> query2 = kGraph.executeQuery("joe", "plays_sport", "?");
        System.out.println("joe plays_sport ?");
        System.out.println("Expected: 2");
        System.out.println("Result: " + query2.size());

        System.out.println();
    }

}