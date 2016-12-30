package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.Iterator;
import java.util.LinkedList;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.Observer;
import de.c3seidenstrasse.networkcontroller.utils.ObserverEvent;

public abstract class RouterTurningState extends NetworkState implements Observer {

	protected final Transport t;

	public RouterTurningState(final Transport t) {
		this.t = t;
	}

	@Override
	public abstract void accept(NetworkStateVisitor nsv);

	@Override
	public Transport getCurrentTransport() {
		return this.t;
	};

	public void enforceRouterPositions() {
		final Iterator<NetworkComponent> i = new LinkedList<>(this.waiting).iterator();
		while (i.hasNext()) {
			final NetworkComponent current = i.next();
			current.register(this, ObserverEvent.POSITIONCHANGED);
			current.turnTo(current.getI()); // Es muss gedreht
													// werden
		}
		if (this.waiting.isEmpty())
			this.t.getNetwork().awake();
	}

	@Override
	public synchronized void update(final NetworkComponent nc) {
		this.removeFromWaitingList(nc);
	}

	private synchronized void removeFromWaitingList(final NetworkComponent nc) {
		final Iterator<NetworkComponent> i = this.waiting.iterator();
		boolean isDeleted = false;
		while (i.hasNext() && !isDeleted) {
			final NetworkComponent current = i.next();
			if (current.equals(nc) && current.getI().equals(nc.getCurrentExit())) {
				current.deregister(this, ObserverEvent.POSITIONCHANGED);
				i.remove();
				isDeleted = true;
			}
		}
		if (this.waiting.isEmpty()) {
			this.getCurrentTransport().getNetwork().awake();
		}
	}

	@Override
	public void doYourThing() {
		this.enforceRouterPositions();
	}

	@Override
	public void arrived() {
		// do nothing
	}
}
