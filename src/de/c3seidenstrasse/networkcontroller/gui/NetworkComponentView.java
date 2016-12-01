package de.c3seidenstrasse.networkcontroller.gui;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;

public class NetworkComponentView extends CenterView {

	public NetworkComponentView() {
	}

	protected void update(final NetworkComponent nc) {
		this.title.setText(nc.toString());
	}

}
