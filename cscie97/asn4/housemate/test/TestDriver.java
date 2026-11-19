// TestDriver.java

package cscie97.asn4.housemate.test;

import cscie97.asn4.housemate.controller.HouseMateControllerService;
import cscie97.asn4.housemate.controller.logger.Logger;
import cscie97.asn4.housemate.entitlement.EntitlementServiceApi;
import cscie97.asn4.housemate.entitlement.EntitlementServiceImp;
import cscie97.asn4.housemate.model.CommandLine;
import cscie97.asn4.housemate.model.HousemateModelService;
import cscie97.asn4.housemate.model.ModelService;

/**
 * Runs a HouseMate script with the Model, Controller and Entitlement services.
 *
 * @author Mauricio Gidi
 * @version November 16, 2026
 */
public final class TestDriver {

    private TestDriver() {
    }

    /**
     * Executes the supplied HouseMate script.
     *
     * @param args one script file name
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: TestDriver <script_file>");
            return;
        }

        EntitlementServiceApi entitlements = EntitlementServiceImp.getInstance();
        ModelService model = HousemateModelService.getInstance(entitlements);
        CommandLine commands = new CommandLine(model, entitlements);
        HouseMateControllerService controller = new HouseMateControllerService(
                model, new Logger(), commands::getAuthToken, entitlements);

        model.registerObserver(controller);

        try {
            commands.executeScript(args[0]);
        } catch (Exception e) {
            System.err.println("Script failed: " + e.getMessage());
        } finally {
            try {
                controller.shutdownAndWait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while shutting down Controller");
            } finally {
                model.removeObserver(controller);
            }
        }
    }
}
