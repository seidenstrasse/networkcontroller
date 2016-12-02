package de.c3seidenstrasse.networkcontroller.gui;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.utils.NoAttachmentException;

public class NetworkComponentView extends CenterView {

	public NetworkComponentView() {
	}

	protected void update(final NetworkComponent nc) {
		this.title.setText(nc.toString());
		this.left1.setText("Aktueller Ausgang: " + nc.getCurrentExit());
		try {
			this.left2.visibleProperty().set(true);
			this.left2.setText("Verbunden mit: " + nc.getChildAt(nc.getCurrentExit()));
		} catch (final NoAttachmentException e) {
			this.left2.visibleProperty().set(false);
		}
	}

}
