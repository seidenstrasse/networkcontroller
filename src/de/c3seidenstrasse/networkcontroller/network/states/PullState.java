package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.TimerTask;

import de.c3seidenstrasse.networkcontroller.route.Transport;

public class PullState extends CapsuleTransportState {

	public PullState(final Transport t) {
		super(t);
	}

	@Override
	public void accept(final NetworkStateVisitor nsv) {
		nsv.accept(this);
	}

	@Override
	public void doYourThing() {
		this.t.getNetwork().getAirsupplier().push();

		final TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				System.out.println("NETWORK: Arived by Timeout");
				PullState.this.arrived();
			};
		};
		PullState.this.timer.schedule(tt, 120000);
	}
}
