package de.c3seidenstrasse.networkcontroller.utils;

import de.c3seidenstrasse.networkcontroller.route.Network;

public class NetworkStarter {

	public static void main(final String[] args)
			throws NoAttachmentException, RouteNotFoundException, InterruptedException {
		final Network n = Network.create();
		n.getRoot().create33c3();
	}

}
