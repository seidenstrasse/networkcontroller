package de.c3seidenstrasse.networkcontroller.communication;

import de.c3seidenstrasse.networkcontroller.network.AirSupplier;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Message;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.route.Message.MessageType;
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
		Message messageObject = new Message(this.message, true);
		Platform.runLater(() -> {
			System.out.println(messageObject);
			this.n.saveBusToList(messageObject);
		});
		if (this.message[1] == AirSupplier.CHANGER_ID) {
			// delegate message to AirSupplier
			this.n.getAirsupplier().messageRecieved(this.message[0], this.message[1], this.message[3]);
			return;
		}

		switch (messageObject.getType()) {
		case DETECT:
			final NetworkComponent nc = this.n.getNodeById((int) this.message[1]);
			nc.capsulePassed();
			break;
		case CONNECT:
			// verwerfen
			break;
		case CONNECTED:
			// 
			final NetworkComponent nc1 = this.n.getNodeById((int) this.message[1]);
			nc1.setCurrentExit((int) this.message[3]);
			break;
		case REQUEST:
			// 
			final NetworkComponent src = this.n.getNodeById((int) this.message[1]);
			final NetworkComponent dst = this.n.getNodeById((int) this.message[3]);
			try {
				final Transport t = new Transport(src, dst);
				this.n.addTransport(t);
				// FIXME not sure if the start should go into byte 1 or 2, so I put it into both bytes.
				this.n.send(new Message(MessageType.ACK_ROUTE, this.message[1], this.message[1], this.message[3]));

			} catch (final RouteNotFoundException e) {
				// forget it
				e.printStackTrace();
			}
			break;
		case DUMMY_4:
			// TODO implement barcode transfer
			break;
		case ACK_ROUTE:
			// verwerfen
			break;
		case STARTED:
			// verwerfen
			break;
		case BARCODE:
			// TODO implement barcode transfer
			break;
		}

	}

}
