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
			final byte[] message = new byte[16];

			int bytesread = 0;
			while (bytesread < 16) {
				int read = input.read();
				String readString = String.valueOf(read);
				Byte readbyte = new Byte(readString);
				if (read != -1) {
					message[bytesread] = readbyte;
					System.out.println(readbyte);
					bytesread++;
				} else {
					synchronized (this) {
						try {
							this.wait(100);
						} catch (InterruptedException e) {
						}
					}
				}
			}

			new Thread(new SssMessageProcessor(message, this.n)).start();

			this.s.close();
		} catch (final IOException e) {
		}
	}
}
