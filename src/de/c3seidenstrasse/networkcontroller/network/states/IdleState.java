package de.c3seidenstrasse.networkcontroller.network.states;

import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.NoCurrentTransportException;

public class IdleState extends NetworkState {
	private static IdleState instance;

	private IdleState() {
	}

	public static IdleState getInstance() {
		if (IdleState.instance == null) {
			IdleState.instance = new IdleState();
		}
		return IdleState.instance;
	}

	@Override
	public void accept(final NetworkStateVisitor nsv) {
		nsv.handle(this);
	}

	@Override
	public Transport getCurrentTransport() throws NoCurrentTransportException {
		throw new NoCurrentTransportException();
	}

	@Override
	public void doYourThing() {
		// just chill. nothing to do here!
	}

	@Override
	public void arrived() {
		// do nothing
	}

}
