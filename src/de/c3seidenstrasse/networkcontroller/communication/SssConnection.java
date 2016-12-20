package de.c3seidenstrasse.networkcontroller.communication;

import java.util.LinkedList;
import java.util.Queue;

import de.c3seidenstrasse.networkcontroller.route.Network;

public class SssConnection {
	private final Network n;
	private final SssSender sender;
	private final SssReciever reciever;

	public SssConnection(final Network n, final String connection) {
		this.n = n;
		SSS7.getInstance().start(connection);

		this.reciever = new SssReciever(this.n);
		new Thread(this.reciever, "SssReciever").start();
		this.sender = new SssSender();
		new Thread(this.sender, "SssSender").start();
	}

	private class SssReciever implements Runnable {
		private final Network n;

		private SssReciever(final Network n) {
			this.n = n;
		}

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				while (!SSS7.getInstance().hasReceived()) {
					try {
						synchronized (this) {
							this.wait(100);
						}
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
				new Thread(new SssMessageProcessor(SSS7.getInstance().getReceived(), this.n)).start();
			}
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
				while (!this.isEmpty()) {
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
}
