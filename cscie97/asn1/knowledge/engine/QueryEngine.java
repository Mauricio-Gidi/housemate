// QueryEngine.java

package cscie97.asn1.knowledge.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

/**
 * The QueryEngine class supports executing Knowledge Graph queries.
 * Queries are specified as Triples in N-Triple format with the special “?” identifier
 * representing a query or a “wild card”. All matching Triples known by the Knowledge
 * Engine should be printed to stdout, preceded by the query string.
 *
 * The Query Engine supports two methods: one that accepts a single Query string,
 * and another that supports a list of queries input from a file.
 *
 * Malformed queries or problems accessing the input file should result in a
 * QueryEngineException. The QueryEngineException should include the query that caused the
 * exception and some details about the reason.
 *
 * @author Mauricio Gidi
 * Date: September 4, 2026
 */
public class QueryEngine {

    /**
     * Executes a single query on the Knowledge Graph.
     * Checks for a non-null and well-formed query string, parses the Subject,
     * Predicate, and Object identifiers, and prints the matching Triples.
     *
     * @param query the N-Triple formatted query to execute
     * @throws QueryEngineException if the query is null or malformed
     */
    public void executeQuery(String query) throws QueryEngineException {

        if (query == null) {
            throw new QueryEngineException("Error executing query, query can't be null.");
        }

        String cleanQuery = query.strip();

        if (!cleanQuery.matches(
                "^(\\w+|\\?)\\s+(\\w+|\\?)\\s+(\\w+|\\?)\\.$")) {
            throw new QueryEngineException("Error executing query: " + query);
        }

        String[] parts = cleanQuery.substring(
                0,
                cleanQuery.length() - 1
        ).split("\\s+");
        String subject = parts[0];
        String predicate = parts[1];
        String object = parts[2];

        Set<Triple> matchTriples = KnowledgeGraph.getInstance().executeQuery(subject, predicate, object);

        System.out.println(cleanQuery);
        if (matchTriples.isEmpty()) {
            System.out.println("<null>");
        } else {
            for (Triple triple : matchTriples) {
                System.out.println(triple.getIdentifier() + ".");
            }
        }
        System.out.println();
    }

    /**
     * Executes a set of queries read from an N-Triple formatted query file.
     * Reads the file one line at a time and delegates each individual query
     * to the executeQuery method.
     *
     * @param fileName the name of the input Query file
     * @throws QueryEngineException if the Query file cannot be accessed or read,
     *                              or if a query in the file cannot be executed
     */
    public void executeQueryFile(String fileName) throws QueryEngineException {

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            while ((line = br.readLine()) != null) {
                executeQuery(line);
            }

        } catch (IOException e) {
            throw new QueryEngineException("Error accessing or reading query file: " + fileName, e);
        }
    }

}