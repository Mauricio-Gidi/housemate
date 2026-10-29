// FireModeCommand.java

package cscie97.asn3.housemate.controller.command;

import java.util.List;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.HousemateModelServiceException;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Responds to a fire alert by turning on lights and warning occupants.
 * Also simulates an emergency call.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class FireModeCommand implements Command {

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
    public FireModeCommand(
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

        System.out.println(
                "SIMULATED 911 call: fire in " + house + ":" + room);
        logAction("SIMULATED 911 call: fire in " + house + ":" + room);
        try {
            List<String> rooms = modelService.getRoomNames(
                    authToken, house);

            int occupants = 0;

            for (String roomName : rooms) {
                occupants += modelService.countOccupantsInRoom(
                        authToken, house, roomName);
            }

            if (occupants == 0) {
                return;
            }

            for (String roomName : rooms) {
                String warning = "Fire in " + room
                        + "; please leave the house immediately.";

                if (modelService.getRoomFloor(authToken, house, roomName) == 1
                        && modelService.getRoomWindows(authToken, house, roomName) > 0) {
                    warning += " Exit through a window if safe.";
                }

                for (String light : modelService.findApplianceIdsByType(
                        authToken, house, roomName,
                        HouseMateVocabulary.LIGHT)) {

                    modelService.setDeviceStatus(
                            authToken,
                            HouseMateVocabulary.APPLIANCE,
                            house, roomName, light,
                            HouseMateVocabulary.POWER,
                            HouseMateVocabulary.ON);
                    logAction("Set light " + house + ":" + roomName + ":" + light
                            + " power=on");
                    partial = true;
                }

                for (String ava : modelService.findApplianceIdsByType(
                        authToken, house, roomName,
                        HouseMateVocabulary.AVA)) {

                    modelService.setDeviceStatus(
                            authToken,
                            HouseMateVocabulary.APPLIANCE,
                            house, roomName, ava,
                            HouseMateVocabulary.SPEECH,
                            warning);
                    logAction("Ava " + house + ":" + roomName + ":" + ava
                            + " announced: " + warning);
                    partial = true;

                    System.out.println(
                            "Ava (" + roomName + "): " + warning);
                }
            }

        } catch (HousemateModelServiceException e) {
            throw new IllegalStateException(
                    "Fire response failed", e);
        }
    }
}
