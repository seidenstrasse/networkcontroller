package de.c3seidenstrasse.networkcontroller.network;

import de.c3seidenstrasse.networkcontroller.route.Message;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Message.MessageType;

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
		this.n.send(new Message(MessageType.AIRFLOW, 0, CHANGER_ID, 1));
	}

	synchronized private void turnForPush() {
		this.n.send(new Message(MessageType.AIRFLOW, 0, CHANGER_ID, 2));
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
		this.n.send(new Message(MessageType.AIRFLOW, 0, CHANGER_ID, 0));
		System.out.println("AIRSUPPLY: Stopped");
	}

	public ChangerState getAirstate() {
		return this.changerState;
	}

	public enum ChangerState {
		UNKNOWN, OFF, PULL, PUSH, PREPAREPULL, PREPAREPUSH
	}
}
