// OvenTimerCommand.java

package cscie97.asn3.housemate.controller.command;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.HousemateModelServiceException;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Turns off an oven when its cooking timer reaches zero.
 * Announces that the food is ready through Ava.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class OvenTimerCommand implements Command {

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
    public OvenTimerCommand(ModelService modelService, Event event, String authToken) {
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

        try {
            String power = modelService.showDeviceStatus(
                    authToken, HouseMateVocabulary.APPLIANCE,
                    house, room, event.getDeviceId(), HouseMateVocabulary.POWER);

            if (!HouseMateVocabulary.ON.equals(power)) {
                return;
            }

            modelService.setDeviceStatus(
                    authToken, HouseMateVocabulary.APPLIANCE,
                    house, room, event.getDeviceId(),
                    HouseMateVocabulary.POWER, HouseMateVocabulary.OFF);
            logAction("Set oven " + house + ":" + room + ":" + event.getDeviceId()
                    + " power=off");
            partial = true;

            String avaId = modelService.findApplianceIdsByType(
                    authToken, house, room, HouseMateVocabulary.AVA).get(0);

            modelService.setDeviceStatus(
                    authToken, HouseMateVocabulary.APPLIANCE,
                    house, room, avaId,
                    HouseMateVocabulary.SPEECH, "Food is ready.");
            logAction("Ava " + house + ":" + room + ":" + avaId
                    + " announced: Food is ready.");

            System.out.println("Ava: Food is ready.");
            partial = false;

        } catch (HousemateModelServiceException e) {
            throw new IllegalStateException("Oven timer response failed", e);
        }
    }
}
