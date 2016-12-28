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
		this.t.getNetwork().getAirsupplier().pull();
		final byte[] message = { 0x06, 0x00, (byte) (int) this.t.getStart().getId(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		this.t.getNetwork().send(message);

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
