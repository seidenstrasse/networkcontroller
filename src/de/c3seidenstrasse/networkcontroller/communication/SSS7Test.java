package de.c3seidenstrasse.networkcontroller.communication;

/** Simple example of native C POSIX library declaration and usage. */
public class SSS7Test {

	public static void main(final String[] args) {
		final SSS7 bus = SSS7.getInstance();

		bus.start("/dev/ttyUSB0");

		while (!bus.canSend())
			;
		bus.send("Hallo Java".getBytes());
		while (!bus.canSend())
			;

		while (!bus.hasReceived())
			;
		final byte[] data = bus.getReceived();
		final String str = new String(data);
		System.out.println(str);

		bus.stop();
	}
}
