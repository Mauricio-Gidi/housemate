// WhereIsCommand.java

package cscie97.asn3.housemate.controller.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Answers questions about an occupant's location.
 * Uses the Model Service to find the location and responds through Ava.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class WhereIsCommand implements Command {

    private final ModelService modelService;
    private final Event event;
    private final String authToken;
    private final String occupantName;
    private boolean partial = false;

    /**
     * Creates a command for the given event.
     *
     * @param modelService the Model Service
     * @param event        the event that triggered the command
     * @param authToken    the authentication token
     */
    public WhereIsCommand(
            ModelService modelService,
            Event event,
            String authToken) {
        this.modelService = modelService;
        this.event = event;
        this.authToken = authToken;

        String speech = event.getNewValue().toLowerCase().trim();

        if (!speech.startsWith(HouseMateVocabulary.WHEREIS)) {
            throw new IllegalArgumentException(
                    "Invalid Ava location question");
        }

        String name = speech.substring(
                HouseMateVocabulary.WHEREIS.length()).strip();

        // Question mark is optional.
        if (name.endsWith("?")) {
            name = name.substring(0, name.length() - 1).strip();
        }

        if (!name.matches("[a-z0-9_\\-]+")) {
            throw new IllegalArgumentException(
                    "Invalid occupant name");
        }

        this.occupantName = name;
    }

    /** {@inheritDoc} */
    public boolean partiallyCompleted() {
        return partial;
    }

    /** {@inheritDoc} */
    public void execute() {

        int succeeded = 0;
        List<String> failures = new ArrayList<>();
        String response;

        // Find the occupant's location.
        try {
            Set<String> locations = modelService.getOccupantLocations(
                    authToken,
                    occupantName);

            succeeded++;

            if (locations.isEmpty()) {
                response = "location unknown for " + occupantName;

            } else {
                response = occupantName + " "
                        + HouseMateVocabulary.IS_LOCATED_IN
                        + " "
                        + locations.iterator().next();
            }

        } catch (Exception e) {
            failures.add(
                    "Location query: " + e.getMessage());

            response = "location unavailable for " + occupantName;
        }

        // Send the answer through Ava.
        try {
            modelService.setDeviceStatus(
                    authToken,
                    HouseMateVocabulary.APPLIANCE,
                    event.getHouseId(),
                    event.getRoomId(),
                    event.getDeviceId(),
                    HouseMateVocabulary.SPEECH,
                    response);
            logAction("Ava " + event.getHouseId() + ":" + event.getRoomId()
                    + ":" + event.getDeviceId() + " announced: " + response);
            succeeded++;

        } catch (Exception e) {
            logActionFailure("Ava response failed: " + e.getMessage());

            failures.add("Ava response: " + e.getMessage());
        }

        // Simulate Ava speaking through the console.
        System.out.println("Ava: " + response);

        if (!failures.isEmpty()) {
            partial = succeeded > 0;

            throw new IllegalStateException(
                    succeeded + " actions succeeded; failures: "
                            + String.join("; ", failures));
        }
    }

}
