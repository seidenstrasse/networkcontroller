package de.c3seidenstrasse.networkcontroller.network.states;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.Router;
import de.c3seidenstrasse.networkcontroller.route.Transport;

public class PreparePushState extends RouterTurningState {

	@Override
	public void accept(final NetworkStateVisitor nsv) {
		nsv.accept(this);
	}

	public PreparePushState(final Transport t) {
		super(t);
		synchronized (this) {

			Router prev = null;
			for(NetworkComponent nc : this.getCurrentTransport().getDown()) {
				if(prev == null) {
					prev = (Router)nc;
				} else {
					this.directionForRouter.put(prev, nc.getIndexInParent());
					prev = (Router)nc;
				}
			}
			this.directionForRouter.put(prev, t.getEnde().getIndexInParent());
		}
	}
}
