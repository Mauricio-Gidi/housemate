// CloseDoorCommand.java

package cscie97.asn3.housemate.controller.command;

import java.util.List;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.HousemateModelServiceException;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Closes the door in the room where Ava received the command.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class CloseDoorCommand implements Command {

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
        public CloseDoorCommand(
                        ModelService modelService,
                        Event event,
                        String authToken) {
                this.modelService = modelService;
                this.event = event;
                this.authToken = authToken;
        }

        /** {@inheritDoc} */
        @Override
        public void execute() {

                String houseId = event.getHouseId();
                String roomId = event.getRoomId();

                try {
                        List<String> doors = modelService.findApplianceIdsByType(
                                        authToken,
                                        houseId,
                                        roomId,
                                        HouseMateVocabulary.DOOR);

                        if (doors.size() != 1) {
                                throw new IllegalStateException(
                                                "Expected exactly one door in "
                                                                + houseId + ":" + roomId
                                                                + ", found " + doors.size());
                        }

                        String doorId = doors.get(0);

                        modelService.setDeviceStatus(
                                        authToken,
                                        HouseMateVocabulary.APPLIANCE,
                                        houseId,
                                        roomId,
                                        doorId,
                                        HouseMateVocabulary.STATE,
                                        HouseMateVocabulary.CLOSED);

                        logAction("Set door " + houseId + ":" + roomId + ":" + doorId
                                        + " state=" + HouseMateVocabulary.CLOSED);

                } catch (HousemateModelServiceException e) {
                        throw new IllegalStateException(
                                        "Failed to close door in "
                                                        + houseId + ":" + roomId,
                                        e);
                }
        }
}
