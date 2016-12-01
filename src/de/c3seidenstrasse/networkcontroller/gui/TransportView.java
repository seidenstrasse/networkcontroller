package de.c3seidenstrasse.networkcontroller.gui;

import de.c3seidenstrasse.networkcontroller.route.Transport;

public class TransportView extends CenterView {

	public TransportView() {
	}

	protected void update(final Transport t) {
		this.title.setText(t.toString());
		this.left1.setText("Start: " + t.getStart().getName());
		this.left2.setText("Ziel: " + t.getEnde().getName());
		this.right1.setText("Höchster Knoten: " + t.getHighestPoint().getName());
		this.right2.setText("Direkttransfer");
	}
}
