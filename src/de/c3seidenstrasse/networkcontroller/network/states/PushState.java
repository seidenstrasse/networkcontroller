package de.c3seidenstrasse.networkcontroller.network.states;

import de.c3seidenstrasse.networkcontroller.route.Transport;

public class PushState extends CapsuleTransportState {

	public PushState(final Transport t) {
		super(t, t.getEnde());
	}

	@Override
	public void accept(final NetworkStateVisitor nsv) {
		nsv.accept(this);
	}

	@Override
	public void doYourThing() {
		this.t.getNetwork().startPushAirflow();
	}

}
