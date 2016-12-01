package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.c3seidenstrasse.networkcontroller.network.IndexedNetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.NoCurrentTransportException;

public abstract class NetworkState {
	protected final Set<IndexedNetworkComponent> waiting;

	public abstract void accept(NetworkStateVisitor nsv);

	NetworkState() {
		this.waiting = new HashSet<>();
	}

	public boolean isFinished() {
		return this.waiting.isEmpty();
	}

	public void hasFinished(final NetworkComponent nc) {
		final Iterator<IndexedNetworkComponent> i = this.waiting.iterator();
		while (i.hasNext()) {
			if (i.next().equals(nc)) {
				i.remove();
				return;
			}
		}
	}

	public abstract Transport getCurrentTransport() throws NoCurrentTransportException;

	/**
	 * this method is invoked every time a network jumps into this NetworkState
	 */
	public abstract void doYourThing();

}
