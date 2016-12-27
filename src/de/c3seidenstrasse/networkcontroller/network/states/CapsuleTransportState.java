package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.Timer;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.Observer;
import de.c3seidenstrasse.networkcontroller.utils.ObserverEvent;

public abstract class CapsuleTransportState extends NetworkState implements Observer {

	private boolean finished = false;

	private final NetworkComponent target;
	protected final Transport t;

	protected final Timer timer;

	protected CapsuleTransportState(final Transport t, final NetworkComponent target) {
		this.t = t;
		this.target = target;
		this.target.register(this, ObserverEvent.CAPSULEPASS);
		this.timer = new Timer();
	}

	@Override
	final public boolean isFinished() {
		if (this.finished)
			this.timer.cancel();
		return this.finished;
	}

	@Override
	public void update(final NetworkComponent nc) {
		this.t.getHighestPoint().deregister(this, ObserverEvent.CAPSULEPASS);
		this.t.getNetwork().getAirsupplier().stop();
		this.finished = true;
		this.t.getNetwork().awake();
	}

	@Override
	public Transport getCurrentTransport() {
		return this.t;
	}

	@Override
	public abstract void doYourThing();

	@Override
	public abstract void accept(NetworkStateVisitor nsv);

}
