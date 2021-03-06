package de.c3seidenstrasse.networkcontroller.communication;

import de.c3seidenstrasse.networkcontroller.network.AirSupplier;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import javafx.application.Platform;

public class SssMessageProcessor implements Runnable {
	final byte[] message;
	final Network n;

	SssMessageProcessor(final byte[] message, final Network n) {
		this.message = message;
		this.n = n;
	}

	@Override
	public void run() {
		System.out.println("MSG: " + this.message[0]);
		System.out.println("SRC: " + this.message[1]);
		System.out.println("DST: " + this.message[2]);
		System.out.println("PL1: " + this.message[3]);
		Platform.runLater(() -> {
			this.n.saveBusToList(this.message, true);
		});
		if (this.message[1] == AirSupplier.CHANGER_ID) {
			// delegate message to AirSupplier
			this.n.getAirsupplier().messageRecieved(this.message[0], this.message[1], this.message[3]);
			return;
		}
		switch (this.message[0]) {
		case 0x00:
			// Kapsel erkannt
			final NetworkComponent nc = this.n.getNodeById((int) this.message[1]);
			nc.capsulePassed();
			break;
		case 0x01:
			// Verbindung herstellen
			// verwerfen
			break;
		case 0x02:
			// Verbindung wurde hergestellt
			final NetworkComponent nc1 = this.n.getNodeById((int) this.message[1]);
			nc1.setCurrentExit((int) this.message[3]);
			break;
		case 0x03:
			// Anforderung direkter Transfer
			final NetworkComponent src = this.n.getNodeById((int) this.message[1]);
			final NetworkComponent dst = this.n.getNodeById((int) this.message[3]);
			try {
				final Transport t = new Transport(src, dst);
				this.n.addTransport(t);
				final byte[] message = { 0x05, 0x00, this.message[1], this.message[3], 0x00, 0x00, 0x00, 0x00, 0x00,
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
				this.n.send(message);
			} catch (final RouteNotFoundException e) {
				// forget it
				e.printStackTrace();
			}
			break;
		case 0x04:
			// Anforderung Barcode Transfer
			// TODO implement barcode transfer
			break;
		case 0x05:
			// Best�tigung Transfer
			// verwerfen
			break;
		case 0x06:
			// Transfer Start
			// verwerfen
			break;
		case 0x07:
			// Barcode gelesen
			// TODO implement barcode transfer
			break;
		}

	}

}
