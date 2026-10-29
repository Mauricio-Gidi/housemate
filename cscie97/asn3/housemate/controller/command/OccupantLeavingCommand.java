// OccupantLeavingCommand.java

package cscie97.asn3.housemate.controller.command;

import java.util.ArrayList;
import java.util.List;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.HousemateModelServiceException;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Handles an occupant leaving a room.
 * Removes the location and adjusts lights and temperature if the room is empty.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class OccupantLeavingCommand implements Command {

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
    public OccupantLeavingCommand(
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

        // Remove the occupant's location before checking the room.
        try {
            modelService.removeOccupantLocation(
                    authToken, occupant, house, room);
            logAction("Occupant " + occupant + " left " + house + ":" + room);
        } catch (HousemateModelServiceException e) {
            throw new IllegalStateException(
                    "Could not remove occupant location", e);
        }

        // Only continue if the room is now empty.
        try {
            if (modelService.countOccupantsInRoom(
                    authToken, house, room) != 0) {
                return;
            }

        } catch (HousemateModelServiceException e) {
            partial = true;

            throw new IllegalStateException(
                    "Cannot determine room occupancy", e);
        }

        List<String> failures = new ArrayList<>();

        // Turn off lights.
        try {
            new LightsOffCommand(
                    modelService, event, authToken).execute();

        } catch (RuntimeException e) {
            failures.add("Lights: " + e.getMessage());
        }

        // Decrease thermostat temperature.
        try {
            List<String> thermostats = modelService.findApplianceIdsByType(
                    authToken,
                    house,
                    room,
                    HouseMateVocabulary.THERMOSTAT);

            String thermostat = thermostats.get(0);

            int temperature = Integer.parseInt(
                    modelService.showDeviceStatus(
                            authToken,
                            HouseMateVocabulary.APPLIANCE,
                            house,
                            room,
                            thermostat,
                            HouseMateVocabulary.TEMPERATURE));

            modelService.setDeviceStatus(
                    authToken,
                    HouseMateVocabulary.APPLIANCE,
                    house,
                    room,
                    thermostat,
                    HouseMateVocabulary.TEMPERATURE,
                    Integer.toString(temperature - 1));
            logAction("Set thermostat " + house + ":" + room + ":" + thermostat
                    + " temperature=" + (temperature - 1));
        } catch (Exception e) {
            logActionFailure("Thermostat update failed: " + e.getMessage());
            failures.add("Thermostat: " + e.getMessage());
        }

        if (!failures.isEmpty()) {
            partial = true;

            throw new IllegalStateException(
                    String.join("; ", failures));
        }
    }

}
