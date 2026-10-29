// ResponseCommand.java

package cscie97.asn3.housemate.controller.command;

/**
 * Defines a command that can receive an occupant's follow-up response.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public interface ResponseCommand extends Command {

    /**
     * Processes an occupant's response to an earlier command.
     *
     * @param response the occupant's answer
     * @return the follow-up command, or null if the answer is not recognized
     */
    Command respond(String response);
}
