package de.c3seidenstrasse.networkcontroller.network.states;

import de.c3seidenstrasse.networkcontroller.route.Transport;

public final class PreparePullState extends RouterTurningState {

	@Override
	public void accept(final NetworkStateVisitor nsv) {
		nsv.handle(this);
	}

	public PreparePullState(final Transport t) {
		super(t);
		this.t.getNetwork().getAirsupplier().turnForPull();
		synchronized (this) {
			this.waiting.addAll(this.getCurrentTransport().getAirflow());
			this.waiting.addAll(this.getCurrentTransport().getUp());
		}
	}
}
