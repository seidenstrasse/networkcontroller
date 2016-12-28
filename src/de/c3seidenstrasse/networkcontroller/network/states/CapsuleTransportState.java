package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.Timer;

import de.c3seidenstrasse.networkcontroller.route.Transport;

public abstract class CapsuleTransportState extends NetworkState {

	private boolean finished = false;

	protected final Transport t;

	protected final Timer timer;

	protected CapsuleTransportState(final Transport t) {
		this.t = t;
		this.timer = new Timer();
	}

	public void arrived() {
		this.t.getNetwork().getAirsupplier().stop();
		this.finished = true;
		this.t.getNetwork().awake();
	}

	@Override
	final public boolean isFinished() {
		if (this.finished)
			this.timer.cancel();
		return this.finished;
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
