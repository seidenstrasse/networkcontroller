package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.TimerTask;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Message;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.route.Message.MessageType;

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
		// FIXME I put start and end into bytes 1 and 3, is this correct?
		this.t.getNetwork().send(new Message(MessageType.STARTED, this.t.getStart().getId(), 0, this.t.getEnde().getId()));

		final TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				System.out.println("NETWORK: Arived by Timeout");
				PullState.this.arrived();
			};
		};
		int duration =  t.getStart().getTransferDuration();
		for(NetworkComponent inc : t.getUp()) {
			duration += inc.getTransferDuration();
		}
		duration -= t.getLastUp().getTransferDuration();
		System.out.println("Computed transfer duration is " + duration);
		PullState.this.timer.schedule(tt, duration * 1000);
	}
}
