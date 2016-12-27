package de.c3seidenstrasse.networkcontroller.network.states;

import java.util.Iterator;
import java.util.TimerTask;

import de.c3seidenstrasse.networkcontroller.network.IndexedNetworkComponent;
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
		this.t.getNetwork().getAirsupplier().pull();

		final Iterator<IndexedNetworkComponent> i = this.t.getUp().iterator();
		int seconds = 0;
		while (i.hasNext()) {
			final IndexedNetworkComponent current = i.next();
			seconds = seconds + current.getTransferDuration();

			final TimerTask tt = new TimerTask() {
				@Override
				public void run() {
					current.getNc().capsulePassed();
				}
			};
			PullState.this.timer.schedule(tt, seconds * 1000);
		}
	}

}
