package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.Iterator;
import java.util.TimerTask;

import de.c3seidenstrasse.networkcontroller.network.IndexedNetworkComponent;
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
		this.t.getNetwork().getAirsupplier().push();

		this.getCurrentTransport().getEnde().capsulePassed();
			final TimerTask tt = new TimerTask() {
				@Override
				public void run() {
					PushState.this.getCurrentTransport().getEnde().capsulePassed();
				};
			};
			PushState.this.timer.schedule(tt, 2000);
		}
	}

