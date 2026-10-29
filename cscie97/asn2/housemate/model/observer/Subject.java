// Subject.java

package cscie97.asn2.housemate.model.observer;

import java.util.LinkedList;
import java.util.List;

/**
 * Defines how observers register for Model Service events.
 *
 * @author Mauricio Gidi
 * @version October 27, 2026
 */
public interface Subject {

    List<Observer> observers = new LinkedList<Observer>();

    /**
     * Registers an observer to receive events.
     *
     * @param observer the observer to register
     */
    public void registerObserver(Observer observer);

    /**
     * Removes a registered observer.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(Observer observer);

    /**
     * Sends an event to registered observers.
     *
     * @param event the event to report
     */
    public void notifyObservers(Event event);
}
