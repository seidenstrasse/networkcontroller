package de.c3seidenstrasse.networkcontroller.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.NoAttachmentException;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;

public class NetworkStateTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws NoAttachmentException, RouteNotFoundException {
		Network n = new Network();
		n.getRoot().create33c3();
		
		NetworkComponent centralNode = n.getRoot().getChild();
		NetworkComponent poc = centralNode.getChildAt(3).getChildAt(3);
		NetworkComponent F2_E2 = centralNode.getChildAt(3).getChildAt(4).getChildAt(4).getChildAt(3);
		
		Transport t1 = new Transport(poc, F2_E2);
		
		n.addTransport(t1);
		
		assertTrue(true);
	}

}
