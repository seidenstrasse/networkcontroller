package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.Router;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.NoCurrentTransportException;

public abstract class NetworkState {
	protected final Map<Router, Integer> directionForRouter;

	public abstract void accept(NetworkStateVisitor nsv);

	NetworkState() {
		this.directionForRouter = new TreeMap<>();
	}

	public boolean isFinished() {
		return this.directionForRouter.isEmpty();
	}

	public abstract void arrived();

	public abstract Transport getCurrentTransport() throws NoCurrentTransportException;

	/**
	 * this method is invoked every time a network jumps into this NetworkState
	 */
	public abstract void doYourThing();

}
