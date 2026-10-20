package cscie97.asn2.housemate.model;

/**
 * Base class for people and pets in the Housemate system.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public abstract class Occupant {

    private final String name;

    private Status status;

    private boolean known;

    /**
     * Creates an occupant.
     *
     * @param name name of the occupant
     */
    protected Occupant(String name) {
        this.name = name;
        this.status = Status.ACTIVE;
        this.known = true;
    }

    /**
     * Returns the occupant name.
     *
     * @return name of the occupant
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the occupant type.
     *
     * @return type of occupant
     */
    public abstract String getType();
}