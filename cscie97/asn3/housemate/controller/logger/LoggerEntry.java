// LoggerEntry.java

package cscie97.asn3.housemate.controller.logger;

import java.time.Instant;

/**
 * Stores the details of one Controller log entry.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public final class LoggerEntry {

    private final String name;
    private final String description;
    private final Instant timestamp;
    private final Outcome outcome;

    /**
     * Creates a log entry.
     *
     * @param name        name of the rule or command
     * @param description explanation of what happened
     * @param timestamp   time at which it happened
     * @param outcome     result of the rule evaluation or command
     */
    public LoggerEntry(
            String name,
            String description,
            Instant timestamp,
            Outcome outcome) {

        this.name = name;
        this.description = description;
        this.timestamp = timestamp;
        this.outcome = outcome;
    }

    /** Returns the rule or command name. */
    public String getName() {
        return name;
    }

    /** Returns the description of what happened. */
    public String getDescription() {
        return description;
    }

    /** Returns the timestamp for this entry. */
    public Instant getTimestamp() {
        return timestamp;
    }

    /** Returns the outcome for this entry. */
    public Outcome getOutcome() {
        return outcome;
    }
}
