package de.c3seidenstrasse.networkcontroller.route;

public class Message {
	public enum MessageType {
		/** Kapsel erkannt */ DETECT, 
		/** Verbindung herstellen */ CONNECT, 
		/** Verbindung wurde hergestellt */ CONNECTED, 
		/** Anforderung direkter Transfer */ REQUEST, 
		/** Anforderung Barcode Transfer */ DUMMY_4, 
		/** Best�tigung Transfer */ ACK_ROUTE, 
		/** Transfer Start */ STARTED, 
		/** Barcode gelesen */ BARCODE, 
		/** */ AIRFLOW;
	}
	
	private final byte[] payload;
	public byte[] getPayload() {
		return payload;
	}

	public boolean isIncoming() {
		return incoming;
	}

	private final boolean incoming;

	public Message(final byte[] message) {
		this(message, false);
	}
	
	static byte[] constructPayload(MessageType type, int ... furtherPayload) {
		byte[] payload = new byte[16];
		payload[0] = (byte)type.ordinal();
		for(int i = 0; i < furtherPayload.length; i++)
			payload[i+1] = (byte)furtherPayload[i];
		return payload;
	}
	
	public Message(MessageType type, int ... furtherPayload) {
		this(constructPayload(type, furtherPayload), false);
	}
	
	public Message(final byte[] message, final boolean incoming) {
		if (message.length != 16)
			throw new RuntimeException("Message length must be 16, is " + message.length);
		this.payload = message;
		this.incoming = incoming;
	}
	
	public MessageType getType() {
		int typeId = this.payload[0];
		return MessageType.values()[typeId];
	}

	@Override
	public String toString() {
		String s = "";
		if (this.incoming) {
			s = s + "INC: ";
		} else {
			s = s + "OUT: ";
		}
		switch (getType()) {
		case DETECT:
			s = s + "Detect at " + this.payload[1];
			break;
		case CONNECT:
			s = s + "Connect " + this.payload[2] + " to " + this.payload[3];
			break;
		case CONNECTED:
			s = s + "Connected " + this.payload[1] + " to " + this.payload[3];
			break;
		case REQUEST:
			s = s + "Request Route " + this.payload[1] + " to " + this.payload[3];
			break;
		case DUMMY_4:
			break;
		case ACK_ROUTE:
			s = s + "ACK Route " + this.payload[1] + " to " + this.payload[3];
			break;
		case STARTED:
			s = s + "Route " + this.payload[1] + " to " + this.payload[3] + "started";
			break;
		case BARCODE:
			s = s + "Barcode " + this.payload[3] + " read";
			break;
		case AIRFLOW:
			switch (this.payload[3]) {
			case 0:
				s = s + "Airflow stopped";
				break;
			case 1:
				s = s + "Airflow Pull started";
				break;
			case 2:
				s = s + "Airflow Push started";
				break;
			}
			break;
		}
		return s;
	}
}