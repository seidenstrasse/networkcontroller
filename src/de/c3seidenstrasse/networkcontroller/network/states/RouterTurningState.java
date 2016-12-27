package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.Iterator;
import java.util.LinkedList;

import de.c3seidenstrasse.networkcontroller.network.IndexedNetworkComponent;
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
		final Iterator<IndexedNetworkComponent> i = new LinkedList<>(this.waiting).iterator();
		while (i.hasNext()) {
			final IndexedNetworkComponent current = i.next();
				current.getNc().register(this, ObserverEvent.POSITIONCHANGED);
				current.getNc().turnTo(current.getI()); // Es muss gedreht
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
		final Iterator<IndexedNetworkComponent> i = this.waiting.iterator();
		boolean isDeleted = false;
		while (i.hasNext() && !isDeleted) {
			final IndexedNetworkComponent current = i.next();
			if (current.getNc().equals(nc) && current.getI().equals(nc.getCurrentExit())) {
				current.getNc().deregister(this, ObserverEvent.POSITIONCHANGED);
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

}
