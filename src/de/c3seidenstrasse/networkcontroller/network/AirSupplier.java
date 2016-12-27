package de.c3seidenstrasse.networkcontroller.network;

import de.c3seidenstrasse.networkcontroller.route.Network;

public class AirSupplier {
	public final static byte CHANGER_ID = (byte) 0xFF;
	private final static long AIRFLOW_START_MS = 1000;
	private final static long AIRFLOW_STOP_MS = 2000;
	private final Network n;
	private ChangerState changerState;

	public AirSupplier(final Network n) {
		this.n = n;
		this.changerState = ChangerState.UNKNOWN;
	}

	synchronized public void messageRecieved(final byte type, final byte src, final byte payload) {
		if (type == 0x02 && src == CHANGER_ID) {
			switch (payload) {
			case 0x01:
				this.preparePullFinished();
				break;
			case 0x02:
				this.preparePushFinished();
				break;
			default:
				this.changerState = ChangerState.UNKNOWN;
				break;
			}
		}
	}

	synchronized public void preparePullFinished() {
		this.changerState = ChangerState.PULL;
	}

	synchronized public void preparePushFinished() {
		this.changerState = ChangerState.PUSH;
	}

	synchronized private void turnForPull() {
		if (this.changerState != ChangerState.PULL && this.changerState != ChangerState.PREPAREPULL) {
			final byte[] message = { 0x08, 0x00, CHANGER_ID, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00, 0x00 };
			this.n.send(message);
			this.changerState = ChangerState.PREPAREPULL;
		}
	}

	synchronized private void turnForPush() {
		if (this.changerState != ChangerState.PUSH && this.changerState != ChangerState.PREPAREPUSH) {
			final byte[] message = { 0x08, 0x00, CHANGER_ID, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00, 0x00 };
			this.n.send(message);
			this.changerState = ChangerState.PREPAREPUSH;
		}
	}

	synchronized public void pull() {
		this.turnForPull();
		// TODO busy waiting
		while (this.changerState != ChangerState.PULL) {
			try {
				this.wait(100);
			} catch (final InterruptedException e) {
			}
		}
		this.changerState = ChangerState.PULL;
	}

	synchronized public void push() {
		this.turnForPush();
		// TODO busy waiting
		while (this.changerState != ChangerState.PUSH) {
			try {
				this.wait(100);
			} catch (final InterruptedException e) {
			}
		}
		this.changerState = ChangerState.PUSH;
	}

	synchronized public void stop() {
		final byte[] message = { 0x08, 0x00, CHANGER_ID, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00 };
		this.n.send(message);
		try {
			this.wait(AIRFLOW_STOP_MS);
		} catch (final InterruptedException e) {
		}
	}

	public ChangerState getAirstate() {
		return this.changerState;
	}

	public enum ChangerState {
		UNKNOWN, OFF, PULL, PUSH, PREPAREPULL, PREPAREPUSH
	}
}
