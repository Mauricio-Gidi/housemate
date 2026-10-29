// GenericCommand.java

package cscie97.asn3.housemate.controller.command;

import java.util.ArrayList;
import java.util.List;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;

/**
 * Applies an Ava instruction to a matching appliance in the room.
 * Sends a confirmation or failure message through Ava.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class GenericCommand implements Command {

        private final ModelService modelService;
        private final Event event;
        private final String authToken;

        private final String applianceType;
        private final String statusName;
        private final String value;
        private boolean partial = false;

        /**
         * Creates a command for the given event.
         *
         * @param modelService the Model Service
         * @param event        the event that triggered the command
         * @param authToken    the authentication token
         */
        public GenericCommand(
                        ModelService modelService,
                        Event event,
                        String authToken) {
                this.modelService = modelService;
                this.event = event;
                this.authToken = authToken;

                String speech = event.getNewValue().toLowerCase().trim();

                String[] parts = speech.split(" ", 3);

                if (parts.length != 3
                                || !parts[0].matches("[a-z0-9_\\-]+")
                                || !parts[1].matches("[a-z0-9]+")
                                || parts[2].isBlank()) {

                        throw new IllegalArgumentException(
                                        "Invalid generic Ava command");
                }

                // Prevent a generic command from generating
                // another Ava voicecommand event.
                if (HouseMateVocabulary.AVA.equals(parts[0])
                                && HouseMateVocabulary.VOICECOMMAND.equals(parts[1])) {

                        throw new IllegalArgumentException(
                                        "Ava voicecommand cannot be set generically");
                }

                this.applianceType = parts[0];
                this.statusName = parts[1];
                this.value = parts[2];
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
                boolean applied = false;

                // 1. Try to update the requested appliance.
                try {
                        List<String> matches = modelService.findApplianceIdsByType(
                                        authToken,
                                        house,
                                        room,
                                        applianceType);

                        if (matches.size() != 1) {
                                throw new IllegalStateException(
                                                "Expected one " + applianceType
                                                                + " appliance; found " + matches.size());
                        }

                        modelService.setDeviceStatus(
                                        authToken,
                                        HouseMateVocabulary.APPLIANCE,
                                        house,
                                        room,
                                        matches.get(0),
                                        statusName,
                                        value);

                        logAction("Set " + applianceType + " " + house + ":" + room + ":"
                                        + matches.get(0) + " " + statusName + "=" + value);

                        applied = true;
                        succeeded++;

                } catch (Exception e) {
                        logActionFailure("Appliance update failed: " + e.getMessage());
                        failures.add(
                                        "Appliance update: " + e.getMessage());
                }

                // Ava responds even if the appliance update fails.
                String response = "I " + (applied ? "set " : "could not set ")
                                + applianceType + " " + statusName + " to " + value + ".";

                try {
                        modelService.setDeviceStatus(
                                        authToken,
                                        HouseMateVocabulary.APPLIANCE,
                                        house,
                                        room,
                                        event.getDeviceId(),
                                        HouseMateVocabulary.SPEECH,
                                        response);
                        logAction("Ava " + house + ":" + room + ":" + event.getDeviceId()
                                        + " announced: " + response);
                        succeeded++;

                } catch (Exception e) {
                        logActionFailure("Ava response failed: " + e.getMessage());
                        failures.add("Ava response: " + e.getMessage());
                }

                // Console simulation of Ava's voice output.
                System.out.println("Ava: " + response);

                if (!failures.isEmpty()) {
                        partial = succeeded > 0;

                        throw new IllegalStateException(
                                        succeeded + " actions succeeded; failures: "
                                                        + String.join("; ", failures));
                }
        }

}
