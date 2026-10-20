// CommandLine.java

package cscie97.asn2.housemate.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Provides a command-line interface for the Housemate Model Service.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class CommandLine {

    private final Service service;
    private final String auth_token = "0";

    /**
     * Creates a command-line interface using the model service.
     *
     * @param service the model service
     */
    public CommandLine(Service service) {
        this.service = Objects.requireNonNull(
            service,
            "service"
        );
    }

    /**
     * Executes Housemate commands from a script.
     *
     * @param fileName the script file
     * @throws CommandLineException if the script cannot be read
     */
    public void executeScript(
        String fileName
    ) throws CommandLineException {
        try (BufferedReader br =
                new BufferedReader(new FileReader(fileName))) {

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
                        | HousemateModelServiceException e) {

                    System.err.println(
                        "Error on line "
                        + lineNumber
                        + ": "
                        + e.getMessage()
                    );
                }
            }

        } catch (IOException e) {

            throw new CommandLineException(
                "Error accessing or reading command file: "
                + fileName,
                e
            );
        }
    }

    /**
     * Validates and executes one Housemate command.
     *
     * @param command the command to execute
     * @throws InvalidCommandException if the command is invalid
     * @throws HousemateModelServiceException if the service rejects the command
     */
    public void executeCommand(
        String command
    ) throws InvalidCommandException,
            HousemateModelServiceException {

        command = command.strip();

        if (command.isEmpty()) {
            throw new InvalidCommandException(
                "Command cannot be empty"
            );
        }

        switch (
            command.split("\\s+")[0].toLowerCase()
        ) {

            case "define":
                validateDefine(command);
                break;

            case "add":
                validateAdd(command);
                break;

            case "set":
                validateSet(command);
                break;

            case "show":
                validateShow(command);
                break;

            default:
                throw new InvalidCommandException(
                    "Unknown command: " + command
                );
        }
    }

    /**
     * Validates and executes define commands.
     *
     * @param command the command to validate
     */
    private void validateDefine(
        String command
    ) throws InvalidCommandException,
            HousemateModelServiceException {
        // Syntax = define house <house_name> address <address>
        if (command.matches(
                "(?i)^define house \\S+ address .+$")) {

            String[] words = command.split("\\s+", 5);

            service.defineHouse(
                auth_token,
                words[2],
                words[4]
            );

        // Syntax = define room <room_name> floor <floor> type <room_type> house <house_name> windows <window_count>
        } else if (command.matches(
                "(?i)^define room \\S+ floor \\d+ type \\S+ house \\S+ windows \\d+$")) {

            String[] words = command.split("\\s+");

            service.defineRoom(
                auth_token,
                words[2],
                Integer.parseInt(words[4]),
                words[6],
                words[8],
                Integer.parseInt(words[10])
            );

        // Syntax = define occupant <occupant_name> type <occupant_type>
        } else if (command.matches(
                "(?i)^define occupant \\S+ type \\S+$")) {

            String[] words = command.split("\\s+");

            service.defineOccupant(
                auth_token,
                words[2],
                words[4]
            );

        // Syntax = define sensor <name> type <sensor_type> room <house_name>:<room_name>
        } else if (command.matches(
                "(?i)^define sensor \\S+ type \\S+ room \\S+:\\S+$")) {

            String[] words = command.split("\\s+");
            String[] room = words[6].split(":");

            service.defineSensor(
                auth_token,
                words[2],
                words[4],
                room[0],
                room[1]
            );

        // Syntax = define appliance <name> type <sensor_type> room <house_name>:<room_name> energy-use <energy-use>
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
                energyUse
            );
        } else {
            throw new InvalidCommandException(
                "Invalid define command: " + command
            );
        }
    }

    /**
     * Validates and executes add commands.
     *
     * @param command the command to validate
     */
    private void validateAdd(
        String command
    ) throws InvalidCommandException,
            HousemateModelServiceException {
        // Syntax = add occupant <occupant_name> to_house <house_name>
        if (!command.matches(
                "(?i)^add occupant \\S+ to_house \\S+$")) {

            throw new InvalidCommandException(
                "Invalid add command: " + command
            );
        }

        String[] words = command.split("\\s+");

        service.addOccupantToHouse(
            auth_token,
            words[2],
            words[4]
        );
    }

    /**
     * Validates and executes set commands.
     *
     * @param command the command to validate
     */
    private void validateSet(
        String command
    ) throws InvalidCommandException,
            HousemateModelServiceException {
        // Syntax = set sensor|appliance <house_name>:<room_name>:<name> status <status_name> value <value>
        if (!command.matches(
                "(?i)^set\\s+(sensor|appliance)\\s+\\S+:\\S+:\\S+"
                        + "\\s+status\\s+\\S+\\s+value\\s+.+$")) {

            throw new InvalidCommandException(
                "Invalid set command: " + command
            );
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
            words[6]
        );
    }

    /**
     * Validates and executes show commands.
     *
     * @param command the command to validate
     */
    private void validateShow(
        String command
    ) throws InvalidCommandException,
            HousemateModelServiceException {
        // Syntax = show sensor|appliance <house_name>:<room_name>:<name> status <status>
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
                        device[2]
                    );
                for (Map.Entry<String, String> entry : status.entrySet()) {
                    System.out.println(
                        entry.getKey() + " = " + entry.getValue()
                    );
                }


            } else {

                System.out.println(
                    service.showDeviceStatus(
                        auth_token,
                        words[1],
                        device[0],
                        device[1],
                        device[2],
                        words[4]
                    )
                );
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
                    service.showConfiguration(auth_token)
                );

            } else {

                String[] target = words[2].split(":");

                if (target.length == 1) {

                    System.out.println(
                        service.showConfiguration(
                            auth_token,
                            target[0]
                        )
                    );

                } else if (target.length == 2) {

                    System.out.println(
                        service.showConfiguration(
                            auth_token,
                            target[0],
                            target[1]
                        )
                    );

                } else if (target.length == 3) {

                    System.out.println(
                        service.showConfiguration(
                            auth_token,
                            target[0],
                            target[1],
                            target[2]
                        )
                    );

                } else {

                    throw new InvalidCommandException(
                        "Invalid show configuration command: "
                        + command
                    );
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
                    service.showEnergyUse(auth_token) + " W"
                );

            } else {

                String[] target = words[2].split(":");

                if (target.length == 1) {

                    System.out.println(
                        service.showEnergyUse(
                            auth_token,
                            target[0]
                        ) + " W"
                    );

                } else if (target.length == 2) {

                    System.out.println(
                        service.showEnergyUse(
                            auth_token,
                            target[0],
                            target[1]
                        ) + " W"
                    );

                } else if (target.length == 3) {

                    System.out.println(
                        service.showEnergyUse(
                            auth_token,
                            target[0],
                            target[1],
                            target[2]
                        ) + " W"
                    );

                } else {
                    throw new InvalidCommandException(
                        "Invalid show energy-use command: "
                        + command
                    );
                }
            }

        } else {

            throw new InvalidCommandException(
                "Invalid show command: " + command
            );
        }
    }
}