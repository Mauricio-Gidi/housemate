// HouseMateVocabulary.java

package cscie97.asn2.housemate.model;

/**
 * Defines the common names used by HouseMate devices and commands.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public final class HouseMateVocabulary {
    /**
     * Prevents creating instances of this constants class.
     */
    private HouseMateVocabulary() {
    }

    // Device categories and types
    public static final String APPLIANCE = "appliance";
    public static final String AVA = "ava";
    public static final String DOOR = "door";
    public static final String LIGHT = "light";

    // Status names
    public static final String STATE = "state";
    public static final String POWER = "power";
    public static final String SPEECH = "speech";
    public static final String VOICECOMMAND = "voicecommand";

    // Status values
    public static final String OPEN = "open";
    public static final String CLOSED = "closed";
    public static final String ON = "on";
    public static final String OFF = "off";

    // Ava voice commands
    public static final String OPENDOOR = "open door";
    public static final String CLOSEDOOR = "close door";
    public static final String LIGHTSON = "lights on";
    public static final String LIGHTSOFF = "lights off";
    public static final String WHEREIS = "where is ";

    // Knowledge Graph predicate
    public static final String IS_LOCATED_IN = "is_located_in";

    // Camera device
    public static final String CAMERA = "camera";

    // Camera event names
    public static final String OCCUPANTDETECTED = "occupantdetected";
    public static final String OCCUPANTLEAVING = "occupantleaving";
    public static final String OCCUPANTINACTIVE = "occupantinactive";
    public static final String OCCUPANTACTIVE = "occupantactive";

    // Appliance types
    public static final String THERMOSTAT = "thermostat";

    // Device status names
    public static final String TEMPERATURE = "temperature";
    public static final String BRIGHTNESS = "brightness";

    // Occupant Knowledge Graph status
    public static final String OCCUPANTSTATUS = "status";
    public static final String RESTING = "resting";
    public static final String ACTIVE = "active";

}
