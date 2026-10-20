// Importer.java

package cscie97.asn1.knowledge.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * The Importer class is responsible for reading triples from input files using N-Triple format.
 * The Importer class creates a Triple instance for each line read from the input file and passes the
 * resulting Triples to the KnowledgeGraph.importTriple() method. Also, only fully qualified Triples
 * (i.e., subject, predicate, object all have identifiers) should be added to the Knowledge Graph.
 * Trim extra leading and trailing whitespace from identifier names. The importTripleFile method
 * throws an ImportException on error processing the input file.
 *
 * @author Mauricio Gidi
 * Date: September 29, 2026
 */
public class Importer {

    /**
     * Imports triples from an N-Triple formatted file into the KnowledgeGraph.
     * Reads the input file one line at a time, processes fully qualified Triples,
     * and adds them to the Knowledge Graph.
     *
     * @param fileName the name of the input Triple file
     * @throws ImportException if the Triple file cannot be accessed or read
     */
    public void importTripleFile(String fileName) throws ImportException {

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            KnowledgeGraph knowledgeGraph = KnowledgeGraph.getInstance();

            while ((line = br.readLine()) != null) {

                line = line.strip();
                if (!line.matches("^\\w+\\s+\\w+\\s+\\w+\\.$")) {
                    continue;
                }

                String[] parts = line.substring(0, line.length() - 1).split("\\s+");
                String subject = parts[0];
                String predicate = parts[1];
                String object = parts[2];

                knowledgeGraph.importTriple(subject, predicate, object);
            }

        } catch (IOException e) {
            throw new ImportException("Error accessing or reading triple file: " + fileName, e);
        }
    }

}