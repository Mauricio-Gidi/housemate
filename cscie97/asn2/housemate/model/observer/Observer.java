// Observer.java

package cscie97.asn2.housemate.model.observer;

/**
 * Defines an observer that receives Model Service events.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public interface Observer {
    /**
     * Receives a device status-change event.
     *
     * @param event the reported status change
     */
    void update(Event event);
}
