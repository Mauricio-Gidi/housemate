// ModelService.java

package cscie97.asn2.housemate.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cscie97.asn2.housemate.model.observer.Subject;

/**
 * Public API for configuring and querying
 * the Housemate Model Service.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public interface ModelService extends Subject {

        // DEFINE

        /**
         * Defines a house.
         *
         * @param authToken authorization token
         * @param houseName unique name of the house
         * @param address   address of the house
         * @throws HousemateModelServiceException if the house cannot be defined
         */
        void defineHouse(
                        String authToken,
                        String houseName,
                        String address) throws HousemateModelServiceException;

        /**
         * Defines a room in a house.
         *
         * @param authToken authorization token
         * @param roomName  name of the room
         * @param floor     floor where the room is located
         * @param roomType  type of room
         * @param houseName name of the house containing the room
         * @param windows   number of windows in the room
         * @throws HousemateModelServiceException if the room cannot be defined
         */
        void defineRoom(
                        String authToken,
                        String roomName,
                        int floor,
                        String roomType,
                        String houseName,
                        int windows) throws HousemateModelServiceException;

        /**
         * Defines an occupant.
         *
         * @param authToken    authorization token
         * @param occupantName name of the occupant
         * @param occupantType type of occupant
         * @throws HousemateModelServiceException if the occupant cannot be defined
         */
        void defineOccupant(
                        String authToken,
                        String occupantName,
                        String occupantType) throws HousemateModelServiceException;

        /**
         * Defines a sensor in a room.
         *
         * @param authToken  authorization token
         * @param sensorName name of the sensor
         * @param sensorType type of sensor
         * @param houseName  name of the house containing the sensor
         * @param roomName   name of the room containing the sensor
         * @throws HousemateModelServiceException if the sensor cannot be defined
         */
        void defineSensor(
                        String authToken,
                        String sensorName,
                        String sensorType,
                        String houseName,
                        String roomName) throws HousemateModelServiceException;

        /**
         * Defines an appliance in a room.
         *
         * @param authToken     authorization token
         * @param applianceName name of the appliance
         * @param applianceType type of appliance
         * @param houseName     name of the house containing the appliance
         * @param roomName      name of the room containing the appliance
         * @param energyUse     energy consumption of the appliance in watts
         * @throws HousemateModelServiceException if the appliance cannot be defined
         */
        void defineAppliance(
                        String authToken,
                        String applianceName,
                        String applianceType,
                        String houseName,
                        String roomName,
                        int energyUse) throws HousemateModelServiceException;

        // ADD

        /**
         * Adds an occupant to a house.
         *
         * @param authToken    authorization token
         * @param occupantName name of the occupant
         * @param houseName    name of the house
         * @throws HousemateModelServiceException if the occupant cannot be added
         */
        void addOccupantToHouse(
                        String authToken,
                        String occupantName,
                        String houseName) throws HousemateModelServiceException;

        // SET

        /**
         * Updates one status value for a device.
         *
         * @param authToken  authorization token
         * @param deviceType type of device
         * @param houseName  name of the house containing the device
         * @param roomName   name of the room containing the device
         * @param deviceName name of the device
         * @param statusName name of the status to update
         * @param value      new status value
         * @throws HousemateModelServiceException if the status cannot be updated
         */
        void setDeviceStatus(
                        String authToken,
                        String deviceType,
                        String houseName,
                        String roomName,
                        String deviceName,
                        String statusName,
                        String value) throws HousemateModelServiceException;

        // SHOW

        /**
         * Returns all status values for a device.
         *
         * @param authToken  authorization token
         * @param deviceType type of device
         * @param houseName  name of the house containing the device
         * @param roomName   name of the room containing the device
         * @param deviceName name of the device
         * @return map containing the device status names and values
         * @throws HousemateModelServiceException if the device status cannot be
         *                                        returned
         */
        Map<String, String> showDeviceStatus(
                        String authToken,
                        String deviceType,
                        String houseName,
                        String roomName,
                        String deviceName) throws HousemateModelServiceException;

        /**
         * Returns one status value for a device.
         *
         * @param authToken  authorization token
         * @param deviceType type of device
         * @param houseName  name of the house containing the device
         * @param roomName   name of the room containing the device
         * @param deviceName name of the device
         * @param statusName name of the requested status
         * @return value of the requested status
         * @throws HousemateModelServiceException if the device status cannot be
         *                                        returned
         */
        String showDeviceStatus(
                        String authToken,
                        String deviceType,
                        String houseName,
                        String roomName,
                        String deviceName,
                        String statusName) throws HousemateModelServiceException;

        /**
         * Returns the full Housemate configuration.
         *
         * @param authToken authorization token
         * @return full Housemate configuration
         * @throws HousemateModelServiceException if the configuration cannot be
         *                                        returned
         */
        String showConfiguration(
                        String authToken) throws HousemateModelServiceException;

        /**
         * Returns the configuration for one house.
         *
         * @param authToken authorization token
         * @param houseName name of the house
         * @return configuration for the house
         * @throws HousemateModelServiceException if the configuration cannot be
         *                                        returned
         */
        String showConfiguration(
                        String authToken,
                        String houseName) throws HousemateModelServiceException;

        /**
         * Returns the configuration for one room.
         *
         * @param authToken authorization token
         * @param houseName name of the house containing the room
         * @param roomName  name of the room
         * @return configuration for the room
         * @throws HousemateModelServiceException if the configuration cannot be
         *                                        returned
         */
        String showConfiguration(
                        String authToken,
                        String houseName,
                        String roomName) throws HousemateModelServiceException;

        /**
         * Returns the configuration for one device.
         *
         * @param authToken  authorization token
         * @param houseName  name of the house containing the device
         * @param roomName   name of the room containing the device
         * @param deviceName name of the device
         * @return configuration for the device
         * @throws HousemateModelServiceException if the configuration cannot be
         *                                        returned
         */
        String showConfiguration(
                        String authToken,
                        String houseName,
                        String roomName,
                        String deviceName) throws HousemateModelServiceException;

        /**
         * Returns the total energy use for the system.
         *
         * @param authToken authorization token
         * @return total system energy use in watts
         * @throws HousemateModelServiceException if the energy use cannot be returned
         */
        int showEnergyUse(
                        String authToken) throws HousemateModelServiceException;

        /**
         * Returns the total energy use for one house.
         *
         * @param authToken authorization token
         * @param houseName name of the house
         * @return total house energy use in watts
         * @throws HousemateModelServiceException if the energy use cannot be returned
         */
        int showEnergyUse(
                        String authToken,
                        String houseName) throws HousemateModelServiceException;

        /**
         * Returns the total energy use for one room.
         *
         * @param authToken authorization token
         * @param houseName name of the house containing the room
         * @param roomName  name of the room
         * @return total room energy use in watts
         * @throws HousemateModelServiceException if the energy use cannot be returned
         */
        int showEnergyUse(
                        String authToken,
                        String houseName,
                        String roomName) throws HousemateModelServiceException;

        /**
         * Returns the energy use for one appliance.
         *
         * @param authToken     authorization token
         * @param houseName     name of the house containing the appliance
         * @param roomName      name of the room containing the appliance
         * @param applianceName name of the appliance
         * @return current appliance energy use in watts
         * @throws HousemateModelServiceException if the energy use cannot be returned
         */
        int showEnergyUse(
                        String authToken,
                        String houseName,
                        String roomName,
                        String applianceName) throws HousemateModelServiceException;

        /**
         * Finds appliances of a given type in a room.
         *
         * @param authToken     the authentication token
         * @param houseName     the house name
         * @param roomName      the room name
         * @param applianceType the appliance type to find
         * @return the matching appliance identifiers
         * @throws HousemateModelServiceException if the house or room is not found
         */
        List<String> findApplianceIdsByType(
                        String authToken,
                        String houseName,
                        String roomName,
                        String applianceType) throws HousemateModelServiceException;

        /**
         * Gets the locations recorded for an occupant in the Knowledge Graph.
         *
         * @param authToken    the authentication token
         * @param occupantName the occupant to find
         * @return the occupant's recorded locations
         * @throws HousemateModelServiceException if the lookup cannot be completed
         */
        Set<String> getOccupantLocations(
                        String authToken,
                        String occupantName) throws HousemateModelServiceException;

        /** Records an occupant's current room. */
        void setOccupantLocation(
                        String authToken,
                        String occupantName,
                        String houseName,
                        String roomName) throws HousemateModelServiceException;

        /** Removes an occupant from a recorded room. */
        void removeOccupantLocation(
                        String authToken,
                        String occupantName,
                        String houseName,
                        String roomName) throws HousemateModelServiceException;

        /** Returns the number of occupants in a room. */
        int countOccupantsInRoom(
                        String authToken,
                        String houseName,
                        String roomName) throws HousemateModelServiceException;

        /** Updates an occupant's activity status. */
        void setOccupantStatus(
                        String authToken,
                        String occupantName,
                        String status) throws HousemateModelServiceException;

        /** Returns the names of the rooms in a house. */
        List<String> getRoomNames(String authToken, String houseName)
                        throws HousemateModelServiceException;

        /** Returns the floor number of a room. */
        int getRoomFloor(String authToken, String houseName, String roomName)
                        throws HousemateModelServiceException;

        /** Returns the number of windows in a room. */
        int getRoomWindows(String authToken, String houseName, String roomName)
                        throws HousemateModelServiceException;

}