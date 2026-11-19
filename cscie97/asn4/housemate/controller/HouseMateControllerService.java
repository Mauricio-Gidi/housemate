// HouseMateControllerService.java

package cscie97.asn4.housemate.controller;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.concurrent.ConcurrentHashMap;

import cscie97.asn4.housemate.model.HouseMateVocabulary;
import cscie97.asn4.housemate.model.ModelService;
import cscie97.asn4.housemate.model.observer.Event;
import cscie97.asn4.housemate.controller.command.BeerCountCommand;
import cscie97.asn4.housemate.controller.command.CloseDoorCommand;
import cscie97.asn4.housemate.controller.command.Command;
import cscie97.asn4.housemate.controller.command.FireModeCommand;
import cscie97.asn4.housemate.controller.command.GenericCommand;
import cscie97.asn4.housemate.controller.command.LightsOffCommand;
import cscie97.asn4.housemate.controller.command.LightsOnCommand;
import cscie97.asn4.housemate.controller.command.OccupantActiveCommand;
import cscie97.asn4.housemate.controller.command.OccupantDetectedCommand;
import cscie97.asn4.housemate.controller.command.OccupantInactiveCommand;
import cscie97.asn4.housemate.controller.command.OccupantLeavingCommand;
import cscie97.asn4.housemate.controller.command.OpenDoorCommand;
import cscie97.asn4.housemate.controller.command.OvenTimerCommand;
import cscie97.asn4.housemate.controller.command.ResponseCommand;
import cscie97.asn4.housemate.controller.command.WhereIsCommand;
import cscie97.asn4.housemate.controller.command.WindowOpenCommand;
import cscie97.asn4.housemate.controller.logger.Logger;
import cscie97.asn4.housemate.controller.logger.LoggerEntry;
import cscie97.asn4.housemate.controller.logger.Outcome;
import cscie97.asn4.housemate.entitlement.EntitlementServiceApi;
import cscie97.asn4.housemate.entitlement.AuthenticationException;
import cscie97.asn4.housemate.entitlement.AccessDeniedException;

/**
 * Receives Model Service events and creates commands for the matching rules.
 * Commands are queued and executed by a background worker.
 *
 * @author Mauricio Gidi
 * @version November 16, 2026
 */
public class HouseMateControllerService implements ControllerService {

    private final ArrayDeque<Command> commandQueue;
    private final ModelService modelService;
    private final Logger logger;
    private final Supplier<String> systemToken;

    private final EntitlementServiceApi entitlementService;

    private final Thread commandWorker;
    private boolean shutdownRequested = false;

    private final Map<String, ResponseCommand> pendingResponses = new ConcurrentHashMap<>();

    /**
     * Creates the Controller Service and starts its command worker.
     *
     * @param modelService       the Model Service
     * @param logger             the logger for command results
     * @param systemToken        supplies the current authenticated system token
     * @param entitlementService the authentication and authorization service
     */
    public HouseMateControllerService(
            ModelService modelService,
            Logger logger,
            Supplier<String> systemToken,
            EntitlementServiceApi entitlementService) {
        this.commandQueue = new ArrayDeque<Command>();
        this.modelService = modelService;
        this.logger = logger;
        this.systemToken = Objects.requireNonNull(systemToken, "systemToken");
        this.entitlementService = Objects.requireNonNull(
                entitlementService, "entitlementService");

        this.commandWorker = new Thread(
                this::processCommands,
                "HouseMate-CommandWorker");

        this.commandWorker.start();
    }

    /** {@inheritDoc} */
    public void update(Event event) {

        if (event == null) {
            return;
        }

        try {
            Command command = evaluateEvent(event);

            if (command != null) {

                queueCommand(command);

                logger.logEntry(
                        new LoggerEntry(
                                "ControllerEvaluation",
                                "Queued " + command.getClass().getSimpleName(),
                                Instant.now(),
                                Outcome.SUCCESS));

            } else if (HouseMateVocabulary.AVA.equals(
                    event.getDeviceType())
                    && HouseMateVocabulary.VOICECOMMAND.equals(
                            event.getStatusName())) {

                logger.logEntry(
                        new LoggerEntry(
                                "ControllerEvaluation",
                                "Unrecognized Ava voice command",
                                Instant.now(),
                                Outcome.FAIL));
            }

        } catch (AuthenticationException e) {
            logger.logEntry(new LoggerEntry(
                    "AvaAuthentication",
                    "Voice authentication failed: " + e.getMessage(),
                    Instant.now(),
                    Outcome.FAIL));

        } catch (RuntimeException e) {
            logger.logEntry(
                    new LoggerEntry(
                            "ControllerEvaluation",
                            "Event evaluation failed: "
                                    + e.getClass().getSimpleName(),
                            Instant.now(),
                            Outcome.FAIL));
        }
    }

    /**
     * Finds the command that matches a device status change.
     *
     * @param event the status change
     * @return the matching command, or null if no rule applies
     */
    private Command evaluateEvent(Event event) {

        if (event == null || event.getDeviceType() == null) {
            return null;
        }

        switch (event.getDeviceType().toLowerCase()) {

            case HouseMateVocabulary.AVA:
                return evaluateAvaEvent(event);

            case HouseMateVocabulary.CAMERA:
                return evaluateCameraEvent(event);

            case "smoke_detector":
                if (event.getStatusName().equals("mode")
                        && event.getNewValue().equals("fire")) {
                    return new FireModeCommand(modelService, event, systemToken.get());
                }
                return null;

            case "window":
                if (event.getStatusName().equals("state")
                        && event.getNewValue().equals("open")) {
                    return new WindowOpenCommand(modelService, event, systemToken.get());
                }
                return null;

            case "refrigerator":

                if (!event.getStatusName().equals("beercount")
                        || Integer.parseInt(event.getNewValue()) >= 3) {
                    return null;
                }

                String roomId = event.getHouseId() + ":" + event.getRoomId();
                BeerCountCommand command = new BeerCountCommand(modelService, event, systemToken.get());

                if (pendingResponses.putIfAbsent(roomId, command) != null) {
                    return null;
                }

                return command;

            case "oven":
                if (event.getStatusName().equals("timetocook")
                        && event.getNewValue().equals("0")) {
                    return new OvenTimerCommand(modelService, event, systemToken.get());
                }
                return null;

            default:
                return null;
        }
    }

    /**
     * Adds a command to the queue for the worker to execute.
     *
     * @param command the command to queue
     */
    public void queueCommand(Command command) {
        if (command == null) {
            return;
        }

        synchronized (commandQueue) {
            commandQueue.addLast(command);
            commandQueue.notifyAll();
        }
    }

    /**
     * Runs queued commands one at a time and logs the result.
     */
    private void processCommands() {

        while (true) {

            Command command;

            synchronized (commandQueue) {

                // Wait until work arrives or shutdown is requested.
                while (commandQueue.isEmpty()
                        && !shutdownRequested) {

                    try {
                        commandQueue.wait();

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // Shut down after all queued commands are processed.
                if (commandQueue.isEmpty()
                        && shutdownRequested) {
                    return;
                }

                command = commandQueue.removeFirst();
            }

            try {
                command.execute();

                logger.logEntry(
                        new LoggerEntry(
                                command.getClass().getSimpleName(),
                                "Command executed successfully",
                                Instant.now(),
                                Outcome.SUCCESS));

            } catch (AccessDeniedException e) {
                logger.logEntry(new LoggerEntry(
                        command.getClass().getSimpleName(),
                        "Access denied: " + e.getMessage(),
                        Instant.now(),
                        Outcome.FAIL));

            } catch (RuntimeException e) {
                logger.logEntry(
                        new LoggerEntry(
                                command.getClass().getSimpleName(),
                                "Command execution failed: " + e,
                                Instant.now(),
                                command.partiallyCompleted()
                                        ? Outcome.PARTIAL
                                        : Outcome.FAIL));
            }

        }
    }

    /**
     * Authenticates an Ava speaker and resolves the spoken command.
     *
     * @param event the Ava voice event
     * @return the matching command, or null if unrecognized
     */
    private Command evaluateAvaEvent(Event event) {
        if (!HouseMateVocabulary.VOICECOMMAND.equals(event.getStatusName())
                || event.getNewValue() == null) {
            return null;
        }

        String value = event.getNewValue().strip();
        int separator = value.indexOf(' ');

        if (separator < 5) {
            return null;
        }

        String voicePrint = value.substring(0, separator);

        if (!voicePrint.startsWith("--") || !voicePrint.endsWith("--")) {
            return null;
        }

        String speech = value.substring(separator + 1)
                .strip().toLowerCase(Locale.ROOT);

        if (speech.isEmpty()) {
            return null;
        }

        String speakerToken = entitlementService.loginWithVoicePrint(voicePrint);

        Event spokenEvent = new Event(
                event.getHouseId(),
                event.getRoomId(),
                event.getDeviceId(),
                event.getDeviceType(),
                event.getStatusName(),
                event.getPrevValue(),
                speech);

        String roomId = event.getHouseId() + ":" + event.getRoomId();
        ResponseCommand pending = pendingResponses.get(roomId);

        if (pending != null) {
            Command response = pending.respond(speech, speakerToken);

            if (response != null
                    && pendingResponses.remove(roomId, pending)) {
                return response;
            }
        }

        return switch (speech) {
            case HouseMateVocabulary.OPENDOOR ->
                new OpenDoorCommand(
                        modelService, spokenEvent, speakerToken);

            case HouseMateVocabulary.CLOSEDOOR ->
                new CloseDoorCommand(
                        modelService, spokenEvent, speakerToken);

            case HouseMateVocabulary.LIGHTSON ->
                new LightsOnCommand(
                        modelService, spokenEvent, speakerToken);

            case HouseMateVocabulary.LIGHTSOFF ->
                new LightsOffCommand(
                        modelService, spokenEvent, speakerToken);

            default ->
                evaluateAvaSpeech(spokenEvent, speech, speakerToken);
        };
    }

    /**
     * Resolves free-form Ava commands after speaker authentication.
     *
     * @param event  event containing only the spoken command
     * @param speech normalized command
     * @param token  authenticated speaker token
     * @return matching command or null
     */
    private Command evaluateAvaSpeech(
            Event event, String speech, String token) {

        if (speech.startsWith(HouseMateVocabulary.WHEREIS)) {
            String name = speech.substring(
                    HouseMateVocabulary.WHEREIS.length()).strip();

            if (name.endsWith("?")) {
                name = name.substring(0, name.length() - 1).strip();
            }

            return name.matches("[a-z0-9_\\-]+")
                    ? new WhereIsCommand(modelService, event, token)
                    : null;
        }

        String[] words = speech.split("\\s+", 3);

        if (words.length != 3
                || !words[0].matches("[a-z0-9_\\-]+")
                || !words[1].matches("[a-z0-9]+")
                || words[2].isBlank()
                || (HouseMateVocabulary.AVA.equals(words[0])
                        && HouseMateVocabulary.VOICECOMMAND.equals(words[1]))) {
            return null;
        }

        return new GenericCommand(modelService, event, token);
    }

    /**
     * Finds the command for a camera occupant event.
     *
     * @param event the camera event
     * @return the matching command, or null if not recognized
     */
    private Command evaluateCameraEvent(Event event) {

        if (event.getNewValue() == null
                || event.getNewValue().isBlank()) {
            return null;
        }

        switch (event.getStatusName()) {

            case HouseMateVocabulary.OCCUPANTDETECTED:
                return new OccupantDetectedCommand(
                        modelService, event, systemToken.get());

            case HouseMateVocabulary.OCCUPANTLEAVING:
                return new OccupantLeavingCommand(
                        modelService, event, systemToken.get());

            case HouseMateVocabulary.OCCUPANTINACTIVE:
                return new OccupantInactiveCommand(
                        modelService, event, systemToken.get());

            case HouseMateVocabulary.OCCUPANTACTIVE:
                return new OccupantActiveCommand(
                        modelService, event, systemToken.get());

            default:
                return null;
        }
    }

    /**
     * Finishes queued commands and waits for the worker to stop.
     *
     * @throws InterruptedException if the waiting thread is interrupted
     */
    public void shutdownAndWait() throws InterruptedException {

        synchronized (commandQueue) {
            shutdownRequested = true;
            commandQueue.notifyAll();
        }

        commandWorker.join();
    }

}