// House.java

package cscie97.asn2.housemate.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Models a house with its rooms and occupants.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class House {

    private final String globalId;
    private final String address;
    private final Map<String, Room> rooms;
    private final Map<String, Occupant> occupants;

    /**
     * Creates a house.
     *
     * @param globalId globally unique identifier of the house
     * @param address  address of the house
     */
    public House(String globalId, String address) {
        this.globalId = globalId;
        this.address = address;
        this.rooms = new HashMap<>();
        this.occupants = new HashMap<>();
    }

    /**
     * Adds a room to this house.
     *
     * @param room room to add
     */
    public void addRoom(Room room) {
        rooms.put(room.getName(), room);
    }

    /**
     * Returns a room by name.
     *
     * @param roomName name of the room
     * @return the room, or null if it is not found
     */
    public Room getRoom(String roomName) {
        return rooms.get(roomName);
    }

    /**
     * Returns the house ID.
     *
     * @return globally unique identifier of the house
     */
    public String getGlobalId() {
        return this.globalId;
    }

    /**
     * Adds an occupant to this house.
     *
     * @param occupant occupant to add
     */
    public void addOccupant(Occupant occupant) {
        occupants.put(occupant.getName(), occupant);
    }

    /**
     * Returns the house address.
     *
     * @return address of the house
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Returns the rooms in this house.
     *
     * @return map of rooms by name
     */
    public Map<String, Room> getRooms() {
        return this.rooms;
    }

    /**
     * Returns the occupants in this house.
     *
     * @return map of occupants by name
     */
    public Map<String, Occupant> getOccupants() {
        return this.occupants;
    }
}