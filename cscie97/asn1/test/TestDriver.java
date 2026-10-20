// TestDriver.java

package cscie97.asn1.test;

import cscie97.asn1.knowledge.engine.ImportException;
import cscie97.asn1.knowledge.engine.Importer;
import cscie97.asn1.knowledge.engine.QueryEngine;
import cscie97.asn1.knowledge.engine.QueryEngineException;

/**
 * The TestDriver class provides the entry point for importing triples and executing queries.
 *
 * @author Mauricio Gidi
 * Date: September 11, 2026
 */
public class TestDriver {

    /**
     * Imports triples from the input Triple file and executes queries from the input Query file.
     *
     * @param args command-line arguments containing the input Triple file and input Query file
     */
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Error: Invalid command. Expected: <triple-file> <query-file>");
            return;
        }

        try {
            Importer importer = new Importer();
            QueryEngine queryEngine = new QueryEngine();

            importer.importTripleFile(args[0]);
            queryEngine.executeQueryFile(args[1]);

        } catch (ImportException e) {
            System.out.println("Import error: " + e.getMessage());

        } catch (QueryEngineException e) {
            System.out.println("Query error: " + e.getMessage());
        }
    }

}