// LightsOnCommand.java

package cscie97.asn3.housemate.controller.command;

import java.util.ArrayList;
import java.util.List;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Turns on all lights in a room.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class LightsOnCommand implements Command {

    private final ModelService modelService;
    private final Event event;
    private final String authToken;
    private boolean partial = false;

    /**
     * Creates a command for the given event.
     *
     * @param modelService the Model Service
     * @param event        the event that triggered the command
     * @param authToken    the authentication token
     */
    public LightsOnCommand(
            ModelService modelService,
            Event event,
            String authToken) {
        this.modelService = modelService;
        this.event = event;
        this.authToken = authToken;
    }

    /** {@inheritDoc} */
    public boolean partiallyCompleted() {
        return partial;
    }

    /** {@inheritDoc} */
    public void execute() {

        String house = event.getHouseId();
        String room = event.getRoomId();

        int succeeded = 0;
        List<String> failures = new ArrayList<>();

        try {
            List<String> lights = modelService.findApplianceIdsByType(
                    authToken,
                    house,
                    room,
                    HouseMateVocabulary.LIGHT);

            if (lights.isEmpty()) {
                failures.add("No lights in " + house + ":" + room);
            }

            // A failure on one light must not stop the others.
            for (String lightId : lights) {
                try {
                    modelService.setDeviceStatus(
                            authToken,
                            HouseMateVocabulary.APPLIANCE,
                            house,
                            room,
                            lightId,
                            HouseMateVocabulary.POWER,
                            HouseMateVocabulary.ON);

                    logAction("Set light " + house + ":" + room + ":" + lightId
                            + " power=" + HouseMateVocabulary.ON);

                    succeeded++;

                } catch (Exception e) {
                    logActionFailure("Light " + lightId + " power update failed: " + e.getMessage());
                    failures.add(
                            "Light " + lightId + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            failures.add("Light lookup: " + e.getMessage());
        }

        if (!failures.isEmpty()) {
            partial = succeeded > 0;

            throw new IllegalStateException(
                    succeeded + " light updates succeeded; failures: "
                            + String.join("; ", failures));
        }
    }

}
