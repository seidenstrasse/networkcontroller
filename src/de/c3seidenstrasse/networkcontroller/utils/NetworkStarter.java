package de.c3seidenstrasse.networkcontroller.utils;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;

public class NetworkStarter {

	public static void main(final String[] args)
			throws NoAttachmentException, RouteNotFoundException, InterruptedException {
		final Network n = Network.create();
		n.getRoot().create33c3();
		final NetworkComponent centralNode = n.getRoot().getChild();
		final NetworkComponent gateToGo = centralNode.getChildAt(2);
		final NetworkComponent pilz = centralNode.getChildAt(1);

		final Transport t1 = new Transport(gateToGo, pilz);
		
		System.out.println(t1);

		n.addTransport(t1);

//		Thread.sleep((long) (Network.ROUTERMAXWAIT + (Math.random() * Network.DETECTIONMAXWAIT)));
//		t1.getHighestPoint().capsulePassed();
//		Thread.sleep((long) (Network.ROUTERMAXWAIT + (Math.random() * Network.DETECTIONMAXWAIT)));
//		t1.getEnde().capsulePassed();
		Thread.sleep((long) 30000);
		t1.getEnde().capsulePassed();
	}

}
