// Logger.java

package cscie97.asn3.housemate.controller.logger;

/**
 * Prints Controller events, actions, and command results to the console.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class Logger {

    /**
     * Prints a structured log entry to the console.
     *
     * @param entry the entry to record
     * @throws NullPointerException if entry is null
     */
    public void logEntry(LoggerEntry entry) {
        if (entry == null) {
            throw new NullPointerException("entry");
        }

        System.out.printf(
                "[%s] [%s] %s: %s%n",
                entry.getTimestamp(),
                entry.getOutcome(),
                entry.getName(),
                entry.getDescription());
    }

    /**
     * Prints a general diagnostic message.
     *
     * @param message the message to print
     * @throws NullPointerException if message is null
     */
    public void log(String message) {
        if (message == null) {
            throw new NullPointerException("message");
        }
        System.out.println(message);
    }
}
