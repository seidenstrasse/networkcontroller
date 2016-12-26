package de.c3seidenstrasse.networkcontroller.network;

import de.c3seidenstrasse.networkcontroller.route.Network;

public class AirSupplier {
	private final static byte VACCUUM_ID = (byte) 0xFE;
	private final static byte CHANGER_ID = (byte) 0xFF;
	private final Network n;
	private final AirState airstate;

	public AirSupplier(final Network n) {
		this.n = n;
		this.airstate = AirState.UNKNOWN;
	}

	public void pull() {
		throw new UnsupportedOperationException();
	}

	public void push() {
		throw new UnsupportedOperationException();
	}

	public void stop() {
		throw new UnsupportedOperationException();
	}

	public enum AirState {
		UNKNOWN, OFF, PULL, PUSH
	}
}
