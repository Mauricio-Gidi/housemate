// HousemateModelService.java

package cscie97.asn2.housemate.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import cscie97.asn1.knowledge.engine.KnowledgeGraph;
import cscie97.asn1.knowledge.engine.Triple;
import cscie97.asn2.housemate.model.observer.Event;
import cscie97.asn2.housemate.model.observer.Observer;

/**
 * Implements the Housemate Model Service.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public class HousemateModelService implements ModelService {

        private static HousemateModelService instance;

        private final KnowledgeGraph knowledgeGraph;

        private final Map<String, House> houses;
        private final Map<String, Occupant> occupants;

        private static final String ADDRESS = "address";
        private static final String TYPE = "type";
        private static final String FLOOR = "floor";
        private static final String WINDOWS = "windows";

        private static final String HAS_ROOM = "has_room";
        private static final String HAS_OCCUPANT = "has_occupant";
        private static final String HAS_SENSOR = "has_sensor";
        private static final String HAS_APPLIANCE = "has_appliance";

        private static final String ENERGY_USE = "energy_use";

        private static final String STATUS_PREFIX = "status:";
        private static final String POWER = "power";

        private final List<Observer> observers = new LinkedList<Observer>();

        /**
         * Creates the Model Service and initializes its data.
         */
        private HousemateModelService() {
                knowledgeGraph = KnowledgeGraph.getInstance();
                houses = new HashMap<>();
                occupants = new HashMap<>();
        }

        /**
         * Returns the singleton model service.
         *
         * @return the model service
         */
        public static ModelService getInstance() {
                if (instance == null) {
                        instance = new HousemateModelService();
                }
                return instance;
        }

        /** {@inheritDoc} */

        public void defineHouse(
                        String authToken,
                        String houseName,
                        String address) throws DuplicateEntityException {

                if (houses.containsKey(houseName)) {
                        throw new DuplicateEntityException(
                                        "House",
                                        houseName);
                }

                houses.put(houseName, new House(houseName, address));

                knowledgeGraph.importTriple(
                                houseName,
                                ADDRESS,
                                address);
        }

        /** {@inheritDoc} */

        public void defineRoom(
                        String authToken,
                        String roomName,
                        int floor,
                        String roomType,
                        String houseName,
                        int windows) throws DuplicateEntityException, HousemateModelServiceException {

                House house = requireHouse(houseName);

                if (house.getRoom(roomName) != null) {
                        throw new DuplicateEntityException(
                                        "Room",
                                        houseName + ":" + roomName);
                }

                Room room = new Room(roomType, floor, roomName, windows);
                house.addRoom(room);

                String roomIdentifier = houseName + ":" + roomName;

                knowledgeGraph.importTriple(
                                houseName,
                                HAS_ROOM,
                                roomIdentifier);

                knowledgeGraph.importTriple(
                                houseName + ":" + roomName,
                                TYPE,
                                roomType);

                knowledgeGraph.importTriple(
                                houseName + ":" + roomName,
                                FLOOR,
                                Integer.toString(floor));

                knowledgeGraph.importTriple(
                                houseName + ":" + roomName,
                                WINDOWS,
                                Integer.toString(windows));
        }

        /** {@inheritDoc} */

        public void defineOccupant(
                        String authToken,
                        String occupantName,
                        String occupantType) throws DuplicateEntityException, HousemateModelServiceException {

                if (occupants.containsKey(occupantName)) {
                        throw new DuplicateEntityException(
                                        "Occupant",
                                        occupantName);
                }

                Occupant occupant;

                switch (occupantType.toLowerCase()) {

                        case "adult":
                                occupant = new Person(occupantName, Maturity.ADULT);
                                knowledgeGraph.importTriple(
                                                occupantName,
                                                TYPE,
                                                occupant.getType());
                                break;

                        case "child":
                                occupant = new Person(occupantName, Maturity.CHILD);
                                knowledgeGraph.importTriple(
                                                occupantName,
                                                TYPE,
                                                occupant.getType());
                                break;

                        case "pet":
                                occupant = new Animal(occupantName);
                                knowledgeGraph.importTriple(
                                                occupantName,
                                                TYPE,
                                                occupant.getType());
                                break;

                        default:
                                throw new HousemateModelServiceException(
                                                "Invalid occupant type: " + occupantType,
                                                new IllegalArgumentException(occupantType));
                }

                occupants.put(occupantName, occupant);
        }

        /** {@inheritDoc} */

        public void defineSensor(
                        String authToken,
                        String sensorName,
                        String sensorType,
                        String houseName,
                        String roomName) throws HousemateModelServiceException {

                House house = requireHouse(houseName);
                Room room = requireRoom(house, roomName);

                if (room.getDevice(sensorName) != null) {
                        throw new DuplicateEntityException(
                                        "Device",
                                        houseName
                                                        + ":"
                                                        + roomName
                                                        + ":"
                                                        + sensorName);
                }

                room.addSensor(
                                sensorName,
                                sensorType);

                knowledgeGraph.importTriple(
                                houseName + ":" + roomName,
                                HAS_SENSOR,
                                houseName + ":" + roomName + ":" + sensorName);

                knowledgeGraph.importTriple(
                                houseName + ":" + roomName + ":" + sensorName,
                                TYPE,
                                sensorType);
        }

        /** {@inheritDoc} */

        public void defineAppliance(
                        String authToken,
                        String applianceName,
                        String applianceType,
                        String houseName,
                        String roomName,
                        int energyUse) throws HousemateModelServiceException {

                House house = requireHouse(houseName);
                Room room = requireRoom(house, roomName);

                if (room.getDevice(applianceName) != null) {
                        throw new DuplicateEntityException(
                                        "Device",
                                        houseName
                                                        + ":"
                                                        + roomName
                                                        + ":"
                                                        + applianceName);
                }

                room.addAppliance(
                                applianceName,
                                applianceType,
                                energyUse);

                String applianceIdentifier = houseName + ":" + roomName + ":" + applianceName;

                knowledgeGraph.importTriple(
                                houseName + ":" + roomName,
                                HAS_APPLIANCE,
                                applianceIdentifier);

                knowledgeGraph.importTriple(
                                applianceIdentifier,
                                TYPE,
                                applianceType);

                knowledgeGraph.importTriple(
                                applianceIdentifier,
                                ENERGY_USE,
                                Integer.toString(energyUse));
        }

        /** {@inheritDoc} */

        public void addOccupantToHouse(
                        String authToken,
                        String occupantName,
                        String houseName) throws HousemateModelServiceException, EntityNotFoundException {

                House house = requireHouse(houseName);
                Occupant occupant = requireOccupant(occupantName);

                house.addOccupant(occupant);

                knowledgeGraph.importTriple(
                                houseName,
                                HAS_OCCUPANT,
                                occupantName);
        }

        /** {@inheritDoc} */

        public void setDeviceStatus(
                        String authToken,
                        String deviceType,
                        String houseName,
                        String roomName,
                        String deviceName,
                        String statusName,
                        String value) throws HousemateModelServiceException, EntityNotFoundException {

                statusName = statusName.strip().toLowerCase();
                value = value.strip().toLowerCase();

                House house = requireHouse(houseName);
                Room room = requireRoom(house, roomName);
                Device device = requireDevice(house, room, deviceName);

                requireDeviceType(device, deviceType);

                Map<String, String> prevValues = showDeviceStatus(authToken, deviceType, houseName, roomName,
                                deviceName);
                String prevValue = prevValues.get(statusName);

                if (Objects.equals(prevValue, value)) {
                        return;
                }

                knowledgeGraph.updateTriple(
                                house.getGlobalId()
                                                + ":"
                                                + room.getName()
                                                + ":"
                                                + device.getId(),
                                STATUS_PREFIX + statusName,
                                value);

                Event event = new Event(
                                house.getGlobalId(),
                                room.getName(),
                                device.getId(),
                                device.getType(),
                                statusName,
                                prevValue,
                                value);

                notifyObservers(event);

        }

        /** {@inheritDoc} */

        public Map<String, String> showDeviceStatus(
                        String authToken,
                        String deviceType,
                        String houseName,
                        String roomName,
                        String deviceName) throws HousemateModelServiceException, EntityNotFoundException {

                House house = requireHouse(houseName);
                Room room = requireRoom(house, roomName);
                Device device = requireDevice(house, room, deviceName);

                requireDeviceType(device, deviceType);

                Set<Triple> results = knowledgeGraph.executeQuery(
                                house.getGlobalId()
                                                + ":"
                                                + room.getName()
                                                + ":"
                                                + device.getId(),
                                "?",
                                "?");

                Map<String, String> status = new HashMap<>();

                for (Triple triple : results) {

                        String predicate = triple.getPredicate().getIdentifier();

                        if (predicate.startsWith(STATUS_PREFIX)) {

                                String statusName = predicate.substring(STATUS_PREFIX.length());

                                status.put(
                                                statusName,
                                                triple.getObject().getIdentifier());
                        }
                }

                return status;
        }

        /** {@inheritDoc} */

        public String showDeviceStatus(
                        String authToken,
                        String deviceType,
                        String houseName,
                        String roomName,
                        String deviceName,
                        String statusName) throws HousemateModelServiceException, EntityNotFoundException {

                House house = requireHouse(houseName);
                Room room = requireRoom(house, roomName);
                Device device = requireDevice(house, room, deviceName);

                requireDeviceType(device, deviceType);

                Set<Triple> results = knowledgeGraph.executeQuery(
                                house.getGlobalId()
                                                + ":"
                                                + room.getName()
                                                + ":"
                                                + device.getId(),
                                STATUS_PREFIX + statusName,
                                "?");

                if (!results.isEmpty()) {
                        Triple first = results.iterator().next();
                        return first.getObject().getIdentifier();
                }

                throw new InvalidStateException(
                                device.getId(),
                                statusName);
        }

        /** {@inheritDoc} */

        public String showConfiguration(
                        String authToken) throws HousemateModelServiceException, EntityNotFoundException {

                StringBuilder configuration = new StringBuilder();

                for (String houseName : houses.keySet()) {

                        if (configuration.length() > 0) {
                                configuration.append(System.lineSeparator());
                        }

                        configuration.append(
                                        showConfiguration(
                                                        authToken,
                                                        houseName));
                }

                return configuration.toString();
        }

        /** {@inheritDoc} */

        public String showConfiguration(
                        String authToken,
                        String houseName) throws HousemateModelServiceException, EntityNotFoundException {

                House house = requireHouse(houseName);

                StringBuilder configuration = new StringBuilder();

                configuration.append("House: ")
                                .append(house.getGlobalId())
                                .append(System.lineSeparator());

                configuration.append("Address: ")
                                .append(house.getAddress());

                Map<String, Occupant> houseOccupants = house.getOccupants();

                for (String occupantName : houseOccupants.keySet()) {

                        Occupant occupant = houseOccupants.get(occupantName);

                        configuration.append(System.lineSeparator())
                                        .append("Occupant: ")
                                        .append(occupant.getName())
                                        .append(" (")
                                        .append(occupant.getType())
                                        .append(")");
                }

                Map<String, Room> houseRooms = house.getRooms();

                for (String roomName : houseRooms.keySet()) {

                        configuration.append(System.lineSeparator())
                                        .append(
                                                        showConfiguration(
                                                                        authToken,
                                                                        houseName,
                                                                        roomName));
                }

                return configuration.toString();
        }

        /** {@inheritDoc} */

        public String showConfiguration(
                        String authToken,
                        String houseName,
                        String roomName) throws HousemateModelServiceException, EntityNotFoundException {

                House house = requireHouse(houseName);
                Room room = requireRoom(house, roomName);

                StringBuilder configuration = new StringBuilder();

                configuration.append("Room: ")
                                .append(room.getName())
                                .append(System.lineSeparator());

                configuration.append("Type: ")
                                .append(room.getType())
                                .append(System.lineSeparator());

                configuration.append("Floor: ")
                                .append(room.getFloor())
                                .append(System.lineSeparator());

                configuration.append("Windows: ")
                                .append(room.getWindows());

                Map<String, Device> roomDevices = room.getDevices();

                for (String deviceName : roomDevices.keySet()) {

                        configuration.append(System.lineSeparator())
                                        .append(
                                                        showConfiguration(
                                                                        authToken,
                                                                        houseName,
                                                                        roomName,
                                                                        deviceName));
                }

                return configuration.toString();
        }

        /** {@inheritDoc} */

        public String showConfiguration(
                        String authToken,
                        String houseName,
                        String roomName,
                        String deviceName) throws HousemateModelServiceException, EntityNotFoundException {

                House house = requireHouse(houseName);
                Room room = requireRoom(house, roomName);
                Device device = requireDevice(house, room, deviceName);

                StringBuilder configuration = new StringBuilder();

                configuration.append("Device: ")
                                .append(device.getId())
                                .append(System.lineSeparator());

                configuration.append("Type: ")
                                .append(device.getType());

                if (device instanceof Appliance appliance) {
                        configuration.append(System.lineSeparator())
                                        .append("Energy Use: ")
                                        .append(appliance.getEnergyConsumption());
                }

                return configuration.toString();
        }

        /** {@inheritDoc} */

        public int showEnergyUse(
                        String authToken) throws HousemateModelServiceException, EntityNotFoundException {

                int totalEnergyUse = 0;

                for (String houseName : houses.keySet()) {

                        totalEnergyUse += showEnergyUse(
                                        authToken,
                                        houseName);
                }

                return totalEnergyUse;
        }

        /** {@inheritDoc} */

        public int showEnergyUse(
                        String authToken,
                        String houseName) throws HousemateModelServiceException, EntityNotFoundException {

                House house = requireHouse(houseName);

                int totalEnergyUse = 0;

                for (String roomName : house.getRooms().keySet()) {

                        totalEnergyUse += showEnergyUse(
                                        authToken,
                                        houseName,
                                        roomName);
                }

                return totalEnergyUse;
        }

        /** {@inheritDoc} */

        public int showEnergyUse(
                        String authToken,
                        String houseName,
                        String roomName) throws HousemateModelServiceException, EntityNotFoundException {

                House house = requireHouse(houseName);
                Room room = requireRoom(house, roomName);

                int totalEnergyUse = 0;

                for (Device device : room.getDevices().values()) {

                        if (device instanceof Appliance) {

                                totalEnergyUse += showEnergyUse(
                                                authToken,
                                                houseName,
                                                roomName,
                                                device.getId());
                        }
                }

                return totalEnergyUse;
        }

        /** {@inheritDoc} */

        public int showEnergyUse(
                        String authToken,
                        String houseName,
                        String roomName,
                        String applianceName) throws HousemateModelServiceException, EntityNotFoundException {

                House house = requireHouse(houseName);
                Room room = requireRoom(house, roomName);
                Device device = requireDevice(house, room, applianceName);

                requireDeviceType(
                                device,
                                "appliance");

                Appliance appliance = (Appliance) device;

                Set<Triple> results = knowledgeGraph.executeQuery(
                                house.getGlobalId()
                                                + ":"
                                                + room.getName()
                                                + ":"
                                                + appliance.getId(),
                                STATUS_PREFIX + POWER,
                                "?");

                if (results.isEmpty()) {
                        return 0;
                }

                Triple powerStatus = results.iterator().next();

                if (powerStatus
                                .getObject()
                                .getIdentifier()
                                .equalsIgnoreCase("ON")) {

                        return appliance.getEnergyConsumption();
                }

                return 0;
        }

        /**
         * Finds a house by name.
         *
         * @param houseName the house to find
         * @return the matching house
         * @throws EntityNotFoundException if the house does not exist
         */
        private House requireHouse(
                        String houseName) throws EntityNotFoundException {

                House house = houses.get(houseName);

                if (house == null) {
                        throw new EntityNotFoundException(
                                        "House",
                                        houseName);
                }

                return house;
        }

        /**
         * Finds a room in a house.
         *
         * @param house    the house to search
         * @param roomName the room to find
         * @return the matching room
         * @throws EntityNotFoundException if the room does not exist
         */
        private Room requireRoom(
                        House house,
                        String roomName) throws EntityNotFoundException {

                Room room = house.getRoom(roomName);

                if (room == null) {
                        throw new EntityNotFoundException(
                                        "Room",
                                        house.getGlobalId()
                                                        + ":"
                                                        + roomName);
                }

                return room;
        }

        /**
         * Finds an occupant by name.
         *
         * @param occupantName the occupant to find
         * @return the matching occupant
         * @throws EntityNotFoundException if the occupant does not exist
         */
        private Occupant requireOccupant(
                        String occupantName) throws EntityNotFoundException {

                Occupant occupant = occupants.get(occupantName);

                if (occupant == null) {
                        throw new EntityNotFoundException(
                                        "Occupant",
                                        occupantName);
                }

                return occupant;
        }

        /**
         * Finds a device in a room.
         *
         * @param house      the house containing the room
         * @param room       the room to search
         * @param deviceName the device to find
         * @return the matching device
         * @throws EntityNotFoundException if the device does not exist
         */
        private Device requireDevice(
                        House house,
                        Room room,
                        String deviceName) throws EntityNotFoundException {

                Device device = room.getDevice(deviceName);

                if (device == null) {
                        throw new EntityNotFoundException(
                                        "Device",
                                        house.getGlobalId()
                                                        + ":"
                                                        + room.getName()
                                                        + ":"
                                                        + deviceName);
                }

                return device;
        }

        /**
         * Checks that a device matches the requested category.
         *
         * @param device     the device to check
         * @param deviceType the requested category
         * @throws EntityNotFoundException if the device does not match
         */
        private void requireDeviceType(
                        Device device,
                        String deviceType) throws EntityNotFoundException {

                if (deviceType.equalsIgnoreCase("sensor")
                                && !(device instanceof Sensor)) {

                        throw new EntityNotFoundException(
                                        "Sensor",
                                        device.getId());
                }

                if (deviceType.equalsIgnoreCase("appliance")
                                && !(device instanceof Appliance)) {

                        throw new EntityNotFoundException(
                                        "Appliance",
                                        device.getId());
                }
        }

        /** {@inheritDoc} */
        public void registerObserver(Observer observer) {
                observers.add(observer);
        }

        /** {@inheritDoc} */
        public void removeObserver(Observer observer) {
                observers.remove(observer);
        }

        /** {@inheritDoc} */
        public void notifyObservers(Event event) {
                for (Observer observer : observers) {
                        observer.update(event);
                }
        }

        /** {@inheritDoc} */
        public synchronized List<String> findApplianceIdsByType(
                        String authToken,
                        String houseName,
                        String roomName,
                        String applianceType) throws HousemateModelServiceException {

                House house = requireHouse(houseName);
                Room room = requireRoom(house, roomName);

                List<String> ids = new ArrayList<>();

                for (Device device : room.getDevices().values()) {
                        if (device instanceof Appliance
                                        && device.getType().equals(applianceType)) {
                                ids.add(device.getId());
                        }
                }

                return ids;
        }

        /** Queries an occupant's recorded locations. */
        public synchronized Set<String> getOccupantLocations(
                        String authToken,
                        String occupantName) {
                Set<String> locations = new HashSet<>();

                Set<Triple> triples = knowledgeGraph.executeQuery(
                                occupantName,
                                HouseMateVocabulary.IS_LOCATED_IN,
                                "?");

                for (Triple triple : triples) {
                        locations.add(
                                        triple.getObject().getIdentifier());
                }

                return locations;
        }

        /** Sets an occupant's location in the knowledge graph. */
        public void setOccupantLocation(
                        String authToken,
                        String occupantName,
                        String houseName,
                        String roomName) throws HousemateModelServiceException {

                House house = requireHouse(houseName);
                requireRoom(house, roomName);
                requireOccupant(occupantName);

                if (!house.getOccupants().containsKey(occupantName)) {
                        throw new HousemateModelServiceException(
                                        "Occupant not in house: " + occupantName);
                }

                knowledgeGraph.updateTriple(
                                occupantName,
                                HouseMateVocabulary.IS_LOCATED_IN,
                                houseName + ":" + roomName);
        }

        /** Removes an occupant's location from the knowledge graph. */
        public void removeOccupantLocation(
                        String authToken,
                        String occupantName,
                        String houseName,
                        String roomName) throws HousemateModelServiceException {

                House house = requireHouse(houseName);
                requireRoom(house, roomName);
                requireOccupant(occupantName);

                if (!house.getOccupants().containsKey(occupantName)) {
                        throw new HousemateModelServiceException(
                                        "Occupant not in house: " + occupantName);
                }

                if (!getOccupantLocations(authToken, occupantName)
                                .contains(houseName + ":" + roomName)) {

                        throw new HousemateModelServiceException(
                                        "Occupant not located in "
                                                        + houseName + ":" + roomName);
                }

                knowledgeGraph.deleteTriple(
                                occupantName,
                                HouseMateVocabulary.IS_LOCATED_IN,
                                houseName + ":" + roomName);
        }

        /** Counts the number of occupants in a specific room. */
        public int countOccupantsInRoom(
                        String authToken,
                        String houseName,
                        String roomName) throws HousemateModelServiceException {

                House house = requireHouse(houseName);
                requireRoom(house, roomName);

                return knowledgeGraph.executeQuery(
                                "?",
                                HouseMateVocabulary.IS_LOCATED_IN,
                                houseName + ":" + roomName).size();
        }

        /** Sets the status of an occupant in the knowledge graph. */
        public void setOccupantStatus(
                        String authToken,
                        String occupantName,
                        String status) throws HousemateModelServiceException {

                requireOccupant(occupantName);

                knowledgeGraph.updateTriple(
                                occupantName,
                                HouseMateVocabulary.OCCUPANTSTATUS,
                                status);
        }

        /** {@inheritDoc} */
        public List<String> getRoomNames(String authToken, String houseName)
                        throws HousemateModelServiceException {
                return new ArrayList<>(requireHouse(houseName).getRooms().keySet());
        }

        /** {@inheritDoc} */
        public int getRoomFloor(String authToken, String houseName, String roomName)
                        throws HousemateModelServiceException {
                return requireRoom(requireHouse(houseName), roomName).getFloor();
        }

        /** {@inheritDoc} */
        public int getRoomWindows(String authToken, String houseName, String roomName)
                        throws HousemateModelServiceException {
                return requireRoom(requireHouse(houseName), roomName).getWindows();
        }

}