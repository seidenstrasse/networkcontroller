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

		final Iterator<IndexedNetworkComponent> i = this.t.getDown().iterator();
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
			PushState.this.timer.schedule(tt, seconds * 1000);
		}
	}

}
