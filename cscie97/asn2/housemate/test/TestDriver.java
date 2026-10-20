// TestDrive.java

package cscie97.asn2.housemate.test;

import cscie97.asn2.housemate.model.CommandLine;
import cscie97.asn2.housemate.model.HousemateModelService;
import cscie97.asn2.housemate.model.Service;

/**
 * Runs Housemate Model Service configuration scripts.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class TestDriver {

    /**
     * Starts the model service and runs the given script.
     *
     * @param args command-line arguments containing the script file name
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println(
                "Usage: java -cp . cscie97.asn2.housemate.test.TestDriver <script_file>"
            );
            return;
        }

        String fileName = args[0];

        try {
            Service service = HousemateModelService.getInstance();
            CommandLine commandLine = new CommandLine(service);

            commandLine.executeScript(fileName);

        } catch (Exception e) {
            System.err.println(
                "Error: " + e.getMessage()
            );
        }
    }
}