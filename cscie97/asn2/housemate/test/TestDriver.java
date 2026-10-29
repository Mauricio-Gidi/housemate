// TestDriver.java

package cscie97.asn2.housemate.test;

import cscie97.asn2.housemate.model.CommandLine;
import cscie97.asn2.housemate.model.HousemateModelService;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn3.housemate.controller.HouseMateControllerService;
import cscie97.asn3.housemate.controller.logger.Logger;

/**
 * Runs HouseMate configuration and test scripts.
 * Connects the Controller to the Model Service to process device events.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class TestDriver {

    /**
     * Starts the model service and runs the given script.
     *
     * @param args command-line arguments containing the script file name
     */
    public static void main(String[] args) {

        String auth_token = "0";

        if (args.length != 1) {
            System.out.println(
                    "Usage: java -cp . cscie97.asn2.housemate.test.TestDriver <script_file>");
            return;
        }

        String fileName = args[0];

        try {
            ModelService modelService = HousemateModelService.getInstance();

            Logger logger = new Logger();
            HouseMateControllerService controller = new HouseMateControllerService(modelService, logger, auth_token);

            modelService.registerObserver(controller);

            CommandLine commandLine = new CommandLine(modelService, auth_token);

            try {
                commandLine.executeScript(fileName);

            } finally {
                controller.shutdownAndWait();
            }

        } catch (Exception e) {
            System.err.println(
                    "Error: " + e.getMessage());
        }
    }
}