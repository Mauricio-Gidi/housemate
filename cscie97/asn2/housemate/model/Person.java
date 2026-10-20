package cscie97.asn2.housemate.model;

/**
 * Models a person in the Housemate system.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class Person extends Occupant {

    private final Maturity maturity;

    /**
     * Creates a person.
     */
    public Person(String name, Maturity maturity) {
        super(name);
        this.maturity = maturity;
    }

    /**
     * Returns the person type.
     */
    @Override
    public String getType() {
        return maturity.toString();
    }
}