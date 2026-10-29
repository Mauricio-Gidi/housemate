// HouseMateControllerService.java

package cscie97.asn3.housemate.controller;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cscie97.asn2.housemate.model.HouseMateVocabulary;
import cscie97.asn2.housemate.model.ModelService;
import cscie97.asn2.housemate.model.observer.Event;
import cscie97.asn3.housemate.controller.command.BeerCountCommand;
import cscie97.asn3.housemate.controller.command.CloseDoorCommand;
import cscie97.asn3.housemate.controller.command.Command;
import cscie97.asn3.housemate.controller.command.FireModeCommand;
import cscie97.asn3.housemate.controller.command.GenericCommand;
import cscie97.asn3.housemate.controller.command.LightsOffCommand;
import cscie97.asn3.housemate.controller.command.LightsOnCommand;
import cscie97.asn3.housemate.controller.command.OccupantActiveCommand;
import cscie97.asn3.housemate.controller.command.OccupantDetectedCommand;
import cscie97.asn3.housemate.controller.command.OccupantInactiveCommand;
import cscie97.asn3.housemate.controller.command.OccupantLeavingCommand;
import cscie97.asn3.housemate.controller.command.OpenDoorCommand;
import cscie97.asn3.housemate.controller.command.OvenTimerCommand;
import cscie97.asn3.housemate.controller.command.ResponseCommand;
import cscie97.asn3.housemate.controller.command.WhereIsCommand;
import cscie97.asn3.housemate.controller.command.WindowOpenCommand;
import cscie97.asn3.housemate.controller.logger.Logger;
import cscie97.asn3.housemate.controller.logger.LoggerEntry;
import cscie97.asn3.housemate.controller.logger.Outcome;

/**
 * Receives Model Service events and creates commands for the matching rules.
 * Commands are queued and executed by a background worker.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class HouseMateControllerService implements ControllerService {

    private final ArrayDeque<Command> commandQueue;
    private final ModelService modelService;
    private final Logger logger;
    private final String authToken;

    private final Thread commandWorker;
    private boolean shutdownRequested = false;

    private final Map<String, ResponseCommand> pendingResponses = new ConcurrentHashMap<>();

    /**
     * Creates the Controller Service and starts its command worker.
     *
     * @param modelService the Model Service
     * @param logger       the logger for command results
     * @param authToken    the authentication token
     */
    public HouseMateControllerService(ModelService modelService, Logger logger, String authToken) {
        this.commandQueue = new ArrayDeque<Command>();
        this.modelService = modelService;
        this.logger = logger;
        this.authToken = authToken;

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
                    return new FireModeCommand(modelService, event, authToken);
                }
                return null;

            case "window":
                if (event.getStatusName().equals("state")
                        && event.getNewValue().equals("open")) {
                    return new WindowOpenCommand(modelService, event, authToken);
                }
                return null;

            case "refrigerator":

                if (!event.getStatusName().equals("beercount")
                        || Integer.parseInt(event.getNewValue()) >= 3) {
                    return null;
                }

                String roomId = event.getHouseId() + ":" + event.getRoomId();
                BeerCountCommand command = new BeerCountCommand(modelService, event, authToken);

                if (pendingResponses.putIfAbsent(roomId, command) != null) {
                    return null;
                }

                return command;

            case "oven":
                if (event.getStatusName().equals("timetocook")
                        && event.getNewValue().equals("0")) {
                    return new OvenTimerCommand(modelService, event, authToken);
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
     * Finds the command for an Ava voice instruction or response.
     *
     * @param event the Ava voice event
     * @return the matching command, or null if not recognized
     */
    private Command evaluateAvaEvent(Event event) {

        if (!HouseMateVocabulary.VOICECOMMAND.equals(event.getStatusName())
                || event.getNewValue() == null) {
            return null;
        }

        String speech = event.getNewValue().toLowerCase().strip();
        String roomId = event.getHouseId() + ":" + event.getRoomId();
        ResponseCommand pending = pendingResponses.get(roomId);

        if (pending != null) {
            Command responseCommand = pending.respond(speech);

            if (responseCommand != null && pendingResponses.remove(roomId, pending)) {
                return responseCommand;
            }
        }

        // Fixed Ava commands
        switch (speech) {

            case HouseMateVocabulary.OPENDOOR:
                return new OpenDoorCommand(
                        modelService, event, authToken);

            case HouseMateVocabulary.CLOSEDOOR:
                return new CloseDoorCommand(
                        modelService, event, authToken);

            case HouseMateVocabulary.LIGHTSON:
                return new LightsOnCommand(
                        modelService, event, authToken);

            case HouseMateVocabulary.LIGHTSOFF:
                return new LightsOffCommand(
                        modelService, event, authToken);

            default:
                break;
        }

        // Location question: where is <occupant>?
        if (speech.startsWith(HouseMateVocabulary.WHEREIS)) {

            String occupantName = speech.substring(
                    HouseMateVocabulary.WHEREIS.length()).strip();

            if (occupantName.endsWith("?")) {
                occupantName = occupantName
                        .substring(0, occupantName.length() - 1)
                        .strip();
            }

            if (occupantName.matches("[a-z0-9_\\-]+")) {
                return new WhereIsCommand(
                        modelService, event, authToken);
            }

            return null;
        }

        // Generic command:
        // <appliance_type> <status_name> <value>
        String[] tokens = speech.split(" ", 3);

        if (tokens.length == 3
                && tokens[0].matches("[a-z0-9_\\-]+")
                && tokens[1].matches("[a-z0-9]+")
                && !tokens[2].isBlank()) {

            // Avoid a loop caused by Ava changing its own voicecommand status.
            if (HouseMateVocabulary.AVA.equals(tokens[0])
                    && HouseMateVocabulary.VOICECOMMAND.equals(tokens[1])) {

                return null;
            }

            return new GenericCommand(
                    modelService,
                    event,
                    authToken);
        }

        return null;
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
                        modelService, event, authToken);

            case HouseMateVocabulary.OCCUPANTLEAVING:
                return new OccupantLeavingCommand(
                        modelService, event, authToken);

            case HouseMateVocabulary.OCCUPANTINACTIVE:
                return new OccupantInactiveCommand(
                        modelService, event, authToken);

            case HouseMateVocabulary.OCCUPANTACTIVE:
                return new OccupantActiveCommand(
                        modelService, event, authToken);

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