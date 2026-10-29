// OccupantInactiveCommand.java

package cscie97.asn3.housemate.controller.command;

import java.util.ArrayList;
import java.util.List;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Handles an occupant becoming inactive.
 * Dims the lights and marks the occupant as resting when alone.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class OccupantInactiveCommand implements Command {

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
    public OccupantInactiveCommand(
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
        String occupant = event.getNewValue();

        int succeeded = 0;
        List<String> failures = new ArrayList<>();

        // The rule applies only when exactly one occupant
        // is present, and it is the detected occupant.
        try {
            if (modelService.countOccupantsInRoom(
                    authToken, house, room) != 1
                    || !modelService.getOccupantLocations(
                            authToken, occupant).contains(house + ":" + room)) {

                return;
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot check room occupancy", e);
        }

        // Dim the lights.
        try {
            List<String> lights = modelService.findApplianceIdsByType(
                    authToken,
                    house,
                    room,
                    HouseMateVocabulary.LIGHT);

            if (lights.isEmpty()) {
                failures.add("No lights in room");
            }

            for (String id : lights) {
                try {
                    modelService.setDeviceStatus(
                            authToken,
                            HouseMateVocabulary.APPLIANCE,
                            house,
                            room,
                            id,
                            HouseMateVocabulary.BRIGHTNESS,
                            "50");
                    logAction("Set light " + house + ":" + room + ":" + id + " brightness=50");
                    succeeded++;

                } catch (Exception e) {
                    logActionFailure("Light " + id + " brightness update failed: " + e.getMessage());
                    failures.add(
                            "Light " + id + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            failures.add("Light lookup: " + e.getMessage());
        }

        // Update the occupant even if a light fails.
        try {
            modelService.setOccupantStatus(
                    authToken,
                    occupant,
                    HouseMateVocabulary.RESTING);
            logAction("Set occupant " + occupant
                    + " status=" + HouseMateVocabulary.RESTING);
            succeeded++;

        } catch (Exception e) {
            logActionFailure("Occupant status update failed: " + e.getMessage());
            failures.add(
                    "Occupant status: " + e.getMessage());
        }

        if (!failures.isEmpty()) {
            partial = succeeded > 0;

            throw new IllegalStateException(
                    succeeded + " actions succeeded; failures: "
                            + String.join("; ", failures));
        }
    }

}
