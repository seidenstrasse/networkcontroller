package de.c3seidenstrasse.networkcontroller.network;

import de.c3seidenstrasse.networkcontroller.route.Network;

public class AirSupplier {
	public final static byte CHANGER_ID = (byte) 0xFF;
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
		final byte[] message = { 0x08, 0x00, CHANGER_ID, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00 };
		this.n.send(message);
	}

	synchronized private void turnForPush() {
		final byte[] message = { 0x08, 0x00, CHANGER_ID, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00 };
		this.n.send(message);
	}

	synchronized public void pull() {
		this.turnForPull();
		System.out.println("AIRSUPPLY: is Pulling");
	}

	synchronized public void push() {
		this.turnForPush();
		System.out.println("AIRSUPPLY: is Pushing");
	}

	synchronized public void stop() {
		final byte[] message = { 0x08, 0x00, CHANGER_ID, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00 };
		this.n.send(message);
		System.out.println("AIRSUPPLY: Stopped");
	}

	public ChangerState getAirstate() {
		return this.changerState;
	}

	public enum ChangerState {
		UNKNOWN, OFF, PULL, PUSH, PREPAREPULL, PREPAREPUSH
	}
}
