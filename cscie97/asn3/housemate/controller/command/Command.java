// Command.java

package cscie97.asn3.housemate.controller.command;

import java.time.Instant;
import cscie97.asn3.housemate.controller.logger.Logger;
import cscie97.asn3.housemate.controller.logger.LoggerEntry;
import cscie97.asn3.housemate.controller.logger.Outcome;

/**
 * Defines an action that the Controller can queue and execute.
 * Also provides methods for logging individual actions.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public interface Command {

    Logger ACTION_LOGGER = new Logger();

    /**
     * Executes the command's action.
     */
    void execute();

    /**
     * Logs a successful action.
     *
     * @param description what the action did
     */
    default void logAction(String description) {
        ACTION_LOGGER.logEntry(new LoggerEntry(
                getClass().getSimpleName(),
                description,
                Instant.now(),
                Outcome.SUCCESS));
    }

    /**
     * Logs a failed action.
     *
     * @param description what went wrong
     */
    default void logActionFailure(String description) {
        ACTION_LOGGER.logEntry(new LoggerEntry(
                getClass().getSimpleName(),
                description,
                Instant.now(),
                Outcome.FAIL));
    }

    /**
     * Reports whether some actions succeeded before a failure.
     *
     * @return true if the command partially completed, otherwise false
     */
    default boolean partiallyCompleted() {
        return false;
    }
}
