
package de.c3seidenstrasse.networkcontroller.utils;

import java.util.HashSet;
import java.util.Set;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;

abstract public class NetworkComponentObservee {

	private final Set<Observer> positionObservers;
	private final Set<Observer> passedObservers;

	protected NetworkComponentObservee() {
		this.positionObservers = new HashSet<>();
		this.passedObservers = new HashSet<>();
	}

	/**
	 * Attaches an observer to this observee. The same observer can only be
	 * registered once.
	 * 
	 * @param observer
	 *            receives update notifications until deregister is called for
	 *            this observer.
	 */
	public void register(final Observer observer, final ObserverEvent oe) {
		switch (oe) {
		case CAPSULEPASS:
			this.passedObservers.add(observer);
			break;
		case POSITIONCHANGED:
			this.positionObservers.add(observer);
			break;
		}
	}

	/**
	 * Detaches an observer from this observee.
	 * 
	 * @param observer
	 *            does no longer get update notifications until deregister is
	 *            called for this observer.
	 */
	public void deregister(final Observer observer, final ObserverEvent oe) {
		switch (oe) {
		case CAPSULEPASS:
			this.passedObservers.remove(observer);
			break;
		case POSITIONCHANGED:
			this.positionObservers.remove(observer);
			break;
		}
	}

	/**
	 * Sends update notifications to all registered observers
	 */
	protected void notifyObservers(NetworkComponent nc, final ObserverEvent oe) {
		switch (oe) {
		case CAPSULEPASS:
			this.passedObservers.forEach(o -> o.update(nc));
			break;
		case POSITIONCHANGED:
			this.positionObservers.forEach(o -> o.update(nc));
			break;
		}
	}
}
