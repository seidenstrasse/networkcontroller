package de.c3seidenstrasse.networkcontroller.utils;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;

public class NetworkStarter {

	public static void main(final String[] args)
			throws NoAttachmentException, RouteNotFoundException, InterruptedException {
		final Network n = new Network();

		n.getRoot().create33c3();
		final NetworkComponent centralNode = n.getRoot().getChild();
		final NetworkComponent poc = centralNode.getChildAt(3).getChildAt(3);
		final NetworkComponent F2_E2 = centralNode.getChildAt(3).getChildAt(4).getChildAt(4).getChildAt(3);

		final Transport t1 = new Transport(poc, F2_E2);

		if (System.console() != null) {
			System.out.print("Enter Start Node: ");
			System.console().readLine();
			System.out.print("Enter Target Node: ");
			System.console().readLine();
		}

		n.addTransport(t1);

		Thread.sleep((long) (Network.ROUTERMAXWAIT + (Math.random() * Network.DETECTIONMAXWAIT)));
		t1.getHighestPoint().capsulePassed();
		Thread.sleep((long) (Network.ROUTERMAXWAIT + (Math.random() * Network.DETECTIONMAXWAIT)));
		t1.getEnde().capsulePassed();
	}

}
