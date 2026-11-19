// CommandLine.java

package cscie97.asn4.housemate.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cscie97.asn4.housemate.entitlement.AuthException;
import cscie97.asn4.housemate.entitlement.EntitlementServiceApi;
import cscie97.asn4.housemate.entitlement.InvalidAccessTokenException;
import java.util.Map;
import java.util.Objects;

/**
 * Provides a command-line interface for the Housemate Model Service.
 *
 * @author Mauricio Gidi
 * @version November 17, 2026
 */
public class CommandLine {

    private final ModelService service;
    private final EntitlementServiceApi entitlements;
    private String auth_token;

    /**
     * Creates a script processor for the Model and Entitlement Services.
     *
     * @param service      Model Service
     * @param entitlements Entitlement Service
     */
    public CommandLine(ModelService service, EntitlementServiceApi entitlements) {
        this.service = Objects.requireNonNull(service, "service");
        this.entitlements = Objects.requireNonNull(entitlements, "entitlements");
    }

    /**
     * Returns the token obtained by the most recent successful script login.
     *
     * @return current token ID, or null if not logged in
     */
    public String getAuthToken() {
        return auth_token;
    }

    /**
     * Executes Housemate commands from a script.
     *
     * @param fileName the script file
     * @throws CommandLineException if the script cannot be read
     */
    public void executeScript(
            String fileName) throws CommandLineException {
        try (BufferedReader br = Files.newBufferedReader(
                Path.of(fileName), StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {

                lineNumber++;
                line = line.strip();

                if (line.isEmpty()
                        || line.startsWith("#")
                        || line.startsWith("*")
                        || line.startsWith("-")
                        || line.startsWith("_")) {

                    continue;
                }

                try {

                    executeCommand(line);

                } catch (InvalidCommandException
                        | HousemateModelServiceException
                        | AuthException e) {

                    System.err.println(
                            "Error on line "
                                    + lineNumber
                                    + ": "
                                    + e.getMessage());
                }
            }

        } catch (IOException e) {

            throw new CommandLineException(
                    "Error accessing or reading command file: "
                            + fileName,
                    e);
        }
    }

    /**
     * Validates and executes one HouseMate or Entitlement command.
     *
     * @param command the command to execute
     * @throws InvalidCommandException        if the command is invalid
     * @throws HousemateModelServiceException if the Model rejects the command
     */
    public void executeCommand(String command)
            throws InvalidCommandException, HousemateModelServiceException {

        command = command.strip();

        if (command.isEmpty()) {
            throw new InvalidCommandException("Command cannot be empty");
        }

        String verb = command.split("[,\\s]+", 2)[0].toLowerCase();

        switch (verb) {
            case "bootstrap_admin", "define_permission", "define_role",
                    "create_user", "create_resource", "add_entitlement_to_role",
                    "add_user_credential", "add_role_to_user",
                    "add_entitlement_to_user",
                    "create_resource_role", "add_resource_role_to_user",
                    "login", "logout", "check_access", "inventory" ->
                executeEntitlementCommand(verb, command.substring(verb.length()));

            case "define" -> {
                requireLogin();
                validateDefine(command);
            }

            case "add" -> {
                requireLogin();
                validateAdd(command);
            }

            case "set" -> {
                requireLogin();
                validateSet(command);
            }

            case "show" -> {
                requireLogin();
                validateShow(command);
            }

            default -> throw new InvalidCommandException(
                    "Unknown command: " + command);
        }
    }

    /**
     * Requires an authenticated script session.
     *
     * @return current token ID
     */
    private String requireLogin() {
        if (auth_token == null || auth_token.isBlank()) {
            throw new InvalidAccessTokenException(
                    "Login required before restricted commands");
        }
        return auth_token;
    }

    /**
     * Executes Entitlement Service commands.
     *
     * @param verb command name
     * @param tail command arguments
     * @throws InvalidCommandException if the arguments are invalid
     */
    private void executeEntitlementCommand(String verb, String tail)
            throws InvalidCommandException {

        List<String> arguments = parseArguments(tail);

        switch (verb) {
            case "bootstrap_admin" -> {
                requireCount(arguments, 4, verb);
                entitlements.bootstrapAdministrator(
                        arguments.get(0),
                        arguments.get(1),
                        arguments.get(2),
                        arguments.get(3));
            }

            case "login" -> {
                auth_token = null;

                if (arguments.size() == 1) {
                    String[] parts = arguments.get(0).split("\\s+");

                    if (parts.length == 2
                            && parts[0].equalsIgnoreCase("voiceprint")) {
                        auth_token = entitlements.loginWithVoicePrint(parts[1]);
                    } else if (parts.length == 2) {
                        auth_token = entitlements.loginWithUsername(
                                parts[0], parts[1]);
                    } else {
                        throw new InvalidCommandException("Invalid login command");
                    }

                } else if (arguments.size() == 2) {
                    String first = arguments.get(0);
                    String second = arguments.get(1);

                    if (first.startsWith("user ")
                            && second.startsWith("password ")) {
                        auth_token = entitlements.loginWithUsername(
                                first.substring(5).strip(),
                                second.substring(9).strip());
                    } else {
                        auth_token = entitlements.loginWithUsername(
                                first, second);
                    }

                } else {
                    throw new InvalidCommandException("Invalid login command");
                }

                System.out.println("Login successful");
            }

            case "logout" -> {
                if (arguments.size() > 1) {
                    throw new InvalidCommandException("Invalid logout command");
                }

                String token = arguments.isEmpty()
                        || arguments.get(0).equals("auth_token")
                                ? requireLogin()
                                : arguments.get(0);

                entitlements.logout(token);

                if (token.equals(auth_token)) {
                    auth_token = null;
                }

                System.out.println("Logout successful");
            }

            case "define_permission" -> {
                requireCount(arguments, 3, verb);
                entitlements.createPermission(
                        requireLogin(),
                        arguments.get(0),
                        arguments.get(1),
                        arguments.get(2));
            }

            case "define_role" -> {
                requireCount(arguments, 3, verb);
                entitlements.createRole(
                        requireLogin(),
                        arguments.get(0),
                        arguments.get(1),
                        arguments.get(2));
            }

            case "create_user" -> {
                requireCount(arguments, 2, verb);
                entitlements.createUser(
                        requireLogin(),
                        arguments.get(0),
                        arguments.get(1));
            }

            case "create_resource" -> {
                requireCount(arguments, 2, verb);
                entitlements.createResource(
                        requireLogin(),
                        arguments.get(0),
                        arguments.get(1));
            }

            case "add_entitlement_to_role" -> {
                requireCount(arguments, 2, verb);
                entitlements.addEntitlementToRole(
                        requireLogin(),
                        arguments.get(0),
                        arguments.get(1));
            }

            case "add_user_credential" -> {
                requireCount(arguments, 3, verb);
                entitlements.addUserCredential(
                        requireLogin(),
                        arguments.get(0),
                        arguments.get(1),
                        arguments.get(2));
            }

            case "add_role_to_user" -> {
                requireCount(arguments, 2, verb);
                entitlements.addRoleToUser(
                        requireLogin(),
                        arguments.get(0),
                        arguments.get(1));
            }

            case "add_entitlement_to_user" -> {
                requireCount(arguments, 2, verb);
                entitlements.addEntitlementToUser(
                        requireLogin(),
                        arguments.get(0),
                        arguments.get(1));
            }

            case "create_resource_role" -> {
                requireCount(arguments, 3, verb);
                entitlements.createResourceRole(
                        requireLogin(),
                        arguments.get(0),
                        arguments.get(1),
                        arguments.get(2));
            }

            case "add_resource_role_to_user" -> {
                requireCount(arguments, 2, verb);
                entitlements.addResourceRoleToUser(
                        requireLogin(),
                        arguments.get(0),
                        arguments.get(1));
            }

            case "check_access" -> {
                requireCount(arguments, 3, verb);

                String token = arguments.get(0).equals("auth_token")
                        ? requireLogin()
                        : arguments.get(0);

                System.out.println(entitlements.checkAccess(
                        token,
                        arguments.get(1),
                        arguments.get(2)));
            }

            case "inventory" -> {
                requireCount(arguments, 0, verb);
                System.out.println(entitlements.getInventory(requireLogin()));
            }

            default -> throw new InvalidCommandException(
                    "Unknown command: " + verb);
        }
    }

    /**
     * Parses comma-separated arguments while preserving quoted values.
     *
     * @param input command arguments
     * @return parsed values
     * @throws InvalidCommandException if a quoted value is incomplete
     */
    private List<String> parseArguments(String input)
            throws InvalidCommandException {

        String text = input.strip();

        if (text.startsWith(",")) {
            text = text.substring(1).strip();
        }

        List<String> result = new ArrayList<>();

        if (text.isEmpty()) {
            return result;
        }

        StringBuilder field = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '“') {
                quoted = true;
            } else if (c == '”') {
                quoted = false;
            } else if (c == '"') {
                quoted = !quoted;
            } else if (c == ',' && !quoted) {
                result.add(field.toString().strip());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }

        if (quoted) {
            throw new InvalidCommandException(
                    "Unterminated quoted argument");
        }

        result.add(field.toString().strip());
        return result;
    }

    /**
     * Verifies the number of command arguments.
     *
     * @param arguments parsed arguments
     * @param count     required number
     * @param verb      command name
     * @throws InvalidCommandException if the arguments are invalid
     */
    private void requireCount(
            List<String> arguments,
            int count,
            String verb) throws InvalidCommandException {

        if (arguments.size() != count
                || arguments.stream().anyMatch(String::isBlank)) {
            throw new InvalidCommandException(
                    "Invalid " + verb + " command");
        }
    }

    /**
     * Validates and executes define commands.
     *
     * @param command the command to validate
     */
    private void validateDefine(
            String command) throws InvalidCommandException,
            HousemateModelServiceException {
        // Syntax = define house <house_name> address <address>
        if (command.matches(
                "(?i)^define house \\S+ address .+$")) {

            String[] words = command.split("\\s+", 5);

            service.defineHouse(
                    auth_token,
                    words[2],
                    words[4]);

            // Syntax = define room <room_name> floor <floor> type <room_type> house
            // <house_name> windows <window_count>
        } else if (command.matches(
                "(?i)^define room \\S+ floor \\d+ type \\S+ house \\S+ windows \\d+$")) {

            String[] words = command.split("\\s+");

            service.defineRoom(
                    auth_token,
                    words[2],
                    Integer.parseInt(words[4]),
                    words[6],
                    words[8],
                    Integer.parseInt(words[10]));

            // Syntax = define occupant <occupant_name> type <occupant_type>
        } else if (command.matches(
                "(?i)^define occupant \\S+ type \\S+$")) {

            String[] words = command.split("\\s+");

            service.defineOccupant(
                    auth_token,
                    words[2],
                    words[4]);

            // Syntax = define sensor <name> type <sensor_type> room
            // <house_name>:<room_name>
        } else if (command.matches(
                "(?i)^define sensor \\S+ type \\S+ room \\S+:\\S+$")) {

            String[] words = command.split("\\s+");
            String[] room = words[6].split(":");

            service.defineSensor(
                    auth_token,
                    words[2],
                    words[4],
                    room[0],
                    room[1]);

            // Syntax = define appliance <name> type <sensor_type> room
            // <house_name>:<room_name> energy-use <energy-use>
        } else if (command.matches(
                "(?i)^define\\s+appliance\\s+\\S+"
                        + "\\s+type\\s+\\S+"
                        + "\\s+room\\s+\\S+:\\S+"
                        + "(\\s+energy-use\\s+\\d+)?$")) {

            String[] words = command.split("\\s+");
            String[] room = words[6].split(":");

            int energyUse = 0;

            if (words.length == 9) {
                energyUse = Integer.parseInt(words[8]);
            }

            service.defineAppliance(
                    auth_token,
                    words[2],
                    words[4],
                    room[0],
                    room[1],
                    energyUse);
        } else {
            throw new InvalidCommandException(
                    "Invalid define command: " + command);
        }
    }

    /**
     * Validates and executes add commands.
     *
     * @param command the command to validate
     */
    private void validateAdd(
            String command) throws InvalidCommandException,
            HousemateModelServiceException {
        // Syntax = add occupant <occupant_name> to_house <house_name>
        if (!command.matches(
                "(?i)^add occupant \\S+ to_house \\S+$")) {

            throw new InvalidCommandException(
                    "Invalid add command: " + command);
        }

        String[] words = command.split("\\s+");

        service.addOccupantToHouse(
                auth_token,
                words[2],
                words[4]);
    }

    /**
     * Validates and executes set commands.
     *
     * @param command the command to validate
     */
    private void validateSet(
            String command) throws InvalidCommandException,
            HousemateModelServiceException {
        // Syntax = set sensor|appliance <house_name>:<room_name>:<name> status
        // <status_name> value <value>
        if (!command.matches(
                "(?i)^set\\s+(sensor|appliance)\\s+\\S+:\\S+:\\S+"
                        + "\\s+status\\s+\\S+\\s+value\\s+.+$")) {

            throw new InvalidCommandException(
                    "Invalid set command: " + command);
        }

        String[] words = command.split("\\s+", 7);
        String[] device = words[2].split(":");

        service.setDeviceStatus(
                auth_token,
                words[1],
                device[0],
                device[1],
                device[2],
                words[4],
                words[6]);
    }

    /**
     * Validates and executes show commands.
     *
     * @param command the command to validate
     */
    private void validateShow(
            String command) throws InvalidCommandException,
            HousemateModelServiceException {
        // Syntax = show sensor|appliance <house_name>:<room_name>:<name> status
        // <status>
        // Syntax = show sensor|appliance <house_name>:<room_name>:<name>
        if (command.matches(
                "(?i)^show\\s+(sensor|appliance)\\s+\\S+:\\S+:\\S+(\\s+status\\s+\\S+)?$")) {

            String[] words = command.split("\\s+");
            String[] device = words[2].split(":");

            if (words.length == 3) {

                Map<String, String> status = service.showDeviceStatus(
                        auth_token,
                        words[1],
                        device[0],
                        device[1],
                        device[2]);
                for (Map.Entry<String, String> entry : status.entrySet()) {
                    System.out.println(
                            entry.getKey() + " = " + entry.getValue());
                }

            } else {

                System.out.println(
                        service.showDeviceStatus(
                                auth_token,
                                words[1],
                                device[0],
                                device[1],
                                device[2],
                                words[4]));
            }

            // Syntax = show configuration
            // Syntax = show configuration <house_name>
            // Syntax = show configuration <house_name>:<room_name>
            // Syntax = show configuration <house_name>:<room_name>:<appliance>
        } else if (command.matches(
                "(?i)^show configuration( \\S+)?$")) {

            String[] words = command.split("\\s+");

            if (words.length == 2) {

                System.out.println(
                        service.showConfiguration(auth_token));

            } else {

                String[] target = words[2].split(":");

                if (target.length == 1) {

                    System.out.println(
                            service.showConfiguration(
                                    auth_token,
                                    target[0]));

                } else if (target.length == 2) {

                    System.out.println(
                            service.showConfiguration(
                                    auth_token,
                                    target[0],
                                    target[1]));

                } else if (target.length == 3) {

                    System.out.println(
                            service.showConfiguration(
                                    auth_token,
                                    target[0],
                                    target[1],
                                    target[2]));

                } else {

                    throw new InvalidCommandException(
                            "Invalid show configuration command: "
                                    + command);
                }
            }

            // Syntax = show energy-use
            // Syntax = show energy-use <house_name>
            // Syntax = show energy-use <house_name>:<room_name>
            // Syntax = show energy-use <house_name>:<room_name>:<appliance>
        } else if (command.matches(
                "(?i)^show energy-use( \\S+)?$")) {

            String[] words = command.split("\\s+");

            if (words.length == 2) {

                System.out.println(
                        service.showEnergyUse(auth_token) + " W");

            } else {

                String[] target = words[2].split(":");

                if (target.length == 1) {

                    System.out.println(
                            service.showEnergyUse(
                                    auth_token,
                                    target[0]) + " W");

                } else if (target.length == 2) {

                    System.out.println(
                            service.showEnergyUse(
                                    auth_token,
                                    target[0],
                                    target[1]) + " W");

                } else if (target.length == 3) {

                    System.out.println(
                            service.showEnergyUse(
                                    auth_token,
                                    target[0],
                                    target[1],
                                    target[2]) + " W");

                } else {
                    throw new InvalidCommandException(
                            "Invalid show energy-use command: "
                                    + command);
                }
            }

        } else {

            throw new InvalidCommandException(
                    "Invalid show command: " + command);
        }
    }
}