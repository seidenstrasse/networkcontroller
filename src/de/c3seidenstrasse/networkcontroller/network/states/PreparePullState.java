package de.c3seidenstrasse.networkcontroller.network.states;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.Router;
import de.c3seidenstrasse.networkcontroller.route.Transport;

public final class PreparePullState extends RouterTurningState {

	@Override
	public void accept(final NetworkStateVisitor nsv) {
		nsv.handle(this);
	}

	public PreparePullState(final Transport t) {
		super(t);
		synchronized (this) {
			NetworkComponent prev = t.getStart();
			for(NetworkComponent nc : this.getCurrentTransport().getUp()) {
				this.directionForRouter.put((Router)nc, prev.getIndexInParent());
				prev = nc;
			}			
			for(NetworkComponent nc : this.getCurrentTransport().getAirflow()) {
				this.directionForRouter.put((Router)nc, prev.getIndexInParent());
				prev = nc;
			}
		}
	}
}
