// Appliance.java

package cscie97.asn2.housemate.model;

/**
 * Models an appliance with its configured energy consumption.
 *
 * @author Mauricio Gidi
 * @version September 29, 2026
 */
public class Appliance extends Device {

    private final int energyConsumption;

    /**
     * Creates an appliance.
     *
     * @param deviceId          unique identifier of the appliance
     * @param type              type of appliance
     * @param energyConsumption energy consumption in watts
     */
    public Appliance(
            String deviceId,
            String type,
            int energyConsumption) {
        super(deviceId, type);
        this.energyConsumption = energyConsumption;
    }

    /**
     * Returns the configured energy consumption.
     *
     * @return energy consumption in watts
     */
    public int getEnergyConsumption() {
        return this.energyConsumption;
    }
}