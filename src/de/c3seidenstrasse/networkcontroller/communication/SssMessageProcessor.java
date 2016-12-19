package de.c3seidenstrasse.networkcontroller.communication;

import de.c3seidenstrasse.networkcontroller.route.Network;

public class SssMessageProcessor implements Runnable {
	final byte[] message;
	final Network n;

	SssMessageProcessor(final byte[] message, final Network n) {
		this.message = message;
		this.n = n;
	}

	@Override
	public void run() {
		// hier wird dann die Nachricht verarbeitet!
	}

}
