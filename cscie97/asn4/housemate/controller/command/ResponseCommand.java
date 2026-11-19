// ResponseCommand.java

package cscie97.asn4.housemate.controller.command;

/**
 * Defines a command that can receive an occupant's follow-up response.
 *
 * @author Mauricio Gidi
 * @version November 16, 2026
 */
public interface ResponseCommand extends Command {

    /**
     * Processes an occupant's response to an earlier command.
     *
     * @param response  the occupant's answer
     * @param authToken authenticated speaker token
     * @return the follow-up command, or null if the answer is not recognized
     */
    Command respond(String response, String authToken);
}
