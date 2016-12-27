package de.c3seidenstrasse.networkcontroller.communication;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import de.c3seidenstrasse.networkcontroller.route.Network;

public class CodeReaderMessageProcessor implements Runnable {
	private final Network n;
	private final Socket s;

	public CodeReaderMessageProcessor(final Network n, final Socket s) {
		this.n = n;
		this.s = s;

		new Thread(this, "CodeReaderMessageProcessor").start();
	}

	@Override
	public void run() {
		try {
			final InputStream input = this.s.getInputStream();
			final byte[] message = {};

			int bytesread = 0;
			while (input.available() > 1 && bytesread < 16) {
				bytesread = bytesread + input.read(message);
			}

			if (message.length == 16) {
				new SssMessageProcessor(message, this.n);
			}

			this.s.close();
		} catch (final IOException e) {
		}
	}
}
