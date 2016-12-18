package de.c3seidenstrasse.networkcontroller.webservice;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.states.NetworkState;

public abstract class GuiUpdater {
	public abstract void updateNode();

	public abstract void updateState();

	protected class NodeChanged {
		final String changed = "Node";
		final NetworkComponent nc;

		NodeChanged(final NetworkComponent nc) {
			this.nc = nc;
		}
	}

	protected class StateChanged {
		final String changed = "State";
		final NetworkState ns;

		StateChanged(final NetworkState ns) {
			this.ns = ns;
		}
	}
}
