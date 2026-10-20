package cscie97.asn2.housemate.model;

/**
 * Models a pet in the Housemate system.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class Animal extends Occupant {

    /**
     * Creates a pet.
     *
     * @param name name of the pet
     */
    public Animal(String name) {
        super(name);
    }

    /**
     * Returns the pet type.
     *
     * @return pet type
     */
    @Override
    public String getType() {
        return "PET";
    }
}