// BeerCountCommand.java

package cscie97.asn3.housemate.controller.command;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.HousemateModelServiceException;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Prints a simulated order request if the occupant says yes.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class BeerCountCommand implements ResponseCommand {

    private final ModelService modelService;
    private final Event event;
    private final String authToken;
    private final String answer;

    /**
     * Creates a command for the given event.
     *
     * @param modelService the Model Service
     * @param event        the event that triggered the command
     * @param authToken    the authentication token
     */
    public BeerCountCommand(ModelService modelService, Event event, String authToken) {
        this(modelService, event, authToken, null);
    }

    /**
     * Creates a command to handle a confirmed yes or no response.
     *
     * @param modelService the Model Service
     * @param event        the original refrigerator event
     * @param authToken    the authentication token
     * @param answer       the occupant's answer
     */
    private BeerCountCommand(ModelService modelService, Event event,
            String authToken, String answer) {
        this.modelService = modelService;
        this.event = event;
        this.authToken = authToken;
        this.answer = answer;
    }

    /** {@inheritDoc} */
    public Command respond(String reply) {
        if ("yes".equals(reply) || "no".equals(reply)) {
            return new BeerCountCommand(modelService, event, authToken, reply);
        }

        return null;
    }

    /** {@inheritDoc} */
    public void execute() {
        String response;

        if (answer == null) {
            response = "Beer count is " + event.getNewValue()
                    + ". Would you like to order more?";
        } else if ("yes".equals(answer)) {
            response = "Beer order confirmed.";
        } else {
            response = "Okay, no beer ordered.";
        }

        try {
            String ava = modelService.findApplianceIdsByType(
                    authToken, event.getHouseId(), event.getRoomId(),
                    HouseMateVocabulary.AVA).get(0);

            modelService.setDeviceStatus(
                    authToken,
                    HouseMateVocabulary.APPLIANCE,
                    event.getHouseId(),
                    event.getRoomId(),
                    ava,
                    HouseMateVocabulary.SPEECH,
                    response);
            logAction("Ava " + event.getHouseId() + ":" + event.getRoomId()
                    + ":" + ava + " announced: " + response);

            System.out.println("Ava: " + response);

            if ("yes".equals(answer)) {
                System.out.println(
                        "SIMULATED EMAIL TO STORE: Send more beer to "
                                + event.getHouseId());
                logAction("SIMULATED EMAIL TO STORE: Send more beer to "
                        + event.getHouseId());
            }

        } catch (HousemateModelServiceException e) {
            throw new IllegalStateException("Beer ordering failed", e);
        }
    }
}
