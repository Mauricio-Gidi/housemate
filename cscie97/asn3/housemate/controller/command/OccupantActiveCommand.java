// OccupantActiveCommand.java

package cscie97.asn3.housemate.controller.command;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.HousemateModelServiceException;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Updates an occupant's status to active when detected by a camera.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class OccupantActiveCommand implements Command {

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
    public OccupantActiveCommand(
            ModelService modelService,
            Event event,
            String authToken) {
        this.modelService = modelService;
        this.event = event;
        this.authToken = authToken;
    }

    /** {@inheritDoc} */
    public void execute() {

        try {
            modelService.setOccupantStatus(
                    authToken,
                    event.getNewValue(),
                    HouseMateVocabulary.ACTIVE);
            logAction("Set occupant " + event.getNewValue()
                    + " status=" + HouseMateVocabulary.ACTIVE);
        } catch (HousemateModelServiceException e) {
            throw new IllegalStateException(
                    "Camera active command failed", e);
        }
    }
}
