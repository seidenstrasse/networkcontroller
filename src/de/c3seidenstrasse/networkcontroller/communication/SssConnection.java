package de.c3seidenstrasse.networkcontroller.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import de.c3seidenstrasse.networkcontroller.route.Network;

public class SssConnection {
	private final Network n;
	private final SssSender sender;
	private final Thread senderThread;
	private final SssBusReciever busReciever;
	private final Thread recieverThread;
	// private final SssNetworkReciever networkReciever;

	public SssConnection(final Network n, final String connection) {
		this.n = n;
		SSS7.getInstance().start(connection);

		this.busReciever = new SssBusReciever(this.n);
		this.senderThread = new Thread(this.busReciever, "SssBusReciever");
		this.senderThread.start();
		// this.networkReciever = new SssNetworkReciever(this.n);
		// new Thread(this.networkReciever, "SssNetworkReciever").start();
		this.sender = new SssSender();
		this.recieverThread = new Thread(this.sender, "SssSender");
		this.recieverThread.start();
	}

	private class SssNetworkReciever implements Runnable {
		private final ServerSocket socket;
		private final Network n;

		private SssNetworkReciever(final Network n) {
			this.n = n;
			try {
				this.socket = new ServerSocket();
				this.socket.bind(new InetSocketAddress(InetAddress.getByName("192.168.0.1"), 6789));
			} catch (final IOException e) {
				throw new Error(e);
			}
		}

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				try {
					final Socket s = this.socket.accept();
					System.out.println("Connected!");
					new CodeReaderMessageProcessor(this.n, s);
				} catch (final IOException e) {
					throw new Error(e);
				}
			}
			try {
				this.socket.close();
			} catch (final IOException e) {
			}
		}
	}

	private class SssBusReciever implements Runnable {
		private final Network n;

		private SssBusReciever(final Network n) {
			this.n = n;
		}

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				while (!SSS7.getInstance().hasReceived() && !Thread.interrupted()) {
					try {
						synchronized (this) {
							this.wait(100);
						}
					} catch (final InterruptedException e) {
					}
				}
				if (SSS7.getInstance().hasReceived())
					new Thread(new SssMessageProcessor(SSS7.getInstance().getReceived(), this.n)).start();
			}
			SSS7.getInstance().stop();
		}
	}

	private class SssSender implements Runnable {
		private boolean isNotified;

		private final Queue<byte[]> queue;

		private SssSender() {
			this.isNotified = false;
			this.queue = new LinkedList<>();
		}

		synchronized public void add(final byte[] message) {
			this.queue.add(message);
			this.isNotified = true;
			this.notify();
		}

		synchronized private byte[] get() {
			return this.queue.poll();
		}

		synchronized private boolean isEmpty() {
			return this.queue.isEmpty();
		}

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				while (!this.isEmpty() && !Thread.interrupted()) {
					final byte[] message = this.get();
					while (!SSS7.getInstance().canSend()) {
						try {
							synchronized (this) {
								this.wait(100);
							}
						} catch (final InterruptedException e) {
						}
					}
					SSS7.getInstance().send(message);
					this.isNotified = false;
				}

				synchronized (this) {
					if (!this.isNotified) {
						try {
							this.wait();
						} catch (final InterruptedException e) {
						}
					}
				}
			}
		}

	}

	public void send(final byte[] message) {
		this.sender.add(message);
	}

	public void interrupt() {
		this.recieverThread.interrupt();
		this.senderThread.interrupt();
	}
}
