package de.c3seidenstrasse.networkcontroller.network.states;

import de.c3seidenstrasse.networkcontroller.route.Transport;

public class PullState extends CapsuleTransportState {

	public PullState(final Transport t) {
		super(t, t.getHighestPoint());
	}

	@Override
	public void accept(final NetworkStateVisitor nsv) {
		nsv.accept(this);
	}

	@Override
	public void doYourThing() {
		this.t.getNetwork().startPullAirflow();
	}

}
