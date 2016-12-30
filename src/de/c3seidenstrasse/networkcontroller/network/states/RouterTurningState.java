package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.Iterator;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.Router;
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
		for (Router r : this.directionForRouter.keySet()) {
			Integer direction = this.directionForRouter.get(r);
			r.register(this, ObserverEvent.POSITIONCHANGED);
			r.turnTo(direction);
		}
		if (this.directionForRouter.isEmpty())
			this.t.getNetwork().awake();
	}

	@Override
	public synchronized void update(final NetworkComponent nc) {
		this.removeFromWaitingList(nc);
	}

	private synchronized void removeFromWaitingList(final NetworkComponent nc) {
		Integer direction = directionForRouter.get(nc);
		if(direction != null) {
			nc.deregister(this, ObserverEvent.POSITIONCHANGED);
			directionForRouter.remove(nc);
		}
		if (this.directionForRouter.isEmpty()) {
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
