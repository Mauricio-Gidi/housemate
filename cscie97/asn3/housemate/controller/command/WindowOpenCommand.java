// WindowOpenCommand.java

package cscie97.asn3.housemate.controller.command;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.HousemateModelServiceException;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Announces through Ava when a window is opened.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class WindowOpenCommand implements Command {

    private final ModelService modelService;
    private final Event event;
    private final String authToken;

    /**
     * Creates a command for the given event.
     *
     * @param modelService the Model Service
     * @param event        the event that triggered the command
     * @param authToken    the authentication token
     */
    public WindowOpenCommand(ModelService modelService, Event event, String authToken) {
        this.modelService = modelService;
        this.event = event;
        this.authToken = authToken;
    }

    /** {@inheritDoc} */
    public void execute() {
        String house = event.getHouseId();
        String room = event.getRoomId();
        String message = "Window " + event.getDeviceId() + " opened in " + room + ".";

        try {
            String ava = modelService.findApplianceIdsByType(
                    authToken, house, room, HouseMateVocabulary.AVA).get(0);

            modelService.setDeviceStatus(
                    authToken, HouseMateVocabulary.APPLIANCE,
                    house, room, ava, HouseMateVocabulary.SPEECH, message);

            logAction("Ava " + house + ":" + room + ":" + ava
                    + " announced: " + message);

            System.out.println("Ava: " + message);

        } catch (HousemateModelServiceException e) {
            throw new IllegalStateException("Window announcement failed", e);
        }
    }
}
