package de.c3seidenstrasse.networkcontroller.network.states;

import de.c3seidenstrasse.networkcontroller.route.Transport;

public class PreparePushState extends RouterTurningState {

	@Override
	public void accept(final NetworkStateVisitor nsv) {
		nsv.accept(this);
	}

	public PreparePushState(final Transport t) {
		super(t);
		synchronized (this) {
			this.waiting.addAll(this.getCurrentTransport().getDown());
		}
	}
}
