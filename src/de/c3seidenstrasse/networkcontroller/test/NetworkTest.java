package de.c3seidenstrasse.networkcontroller.test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.c3seidenstrasse.networkcontroller.network.CodeReader;
import de.c3seidenstrasse.networkcontroller.network.Exit;
import de.c3seidenstrasse.networkcontroller.network.IndexedNetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.Router;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.NoAttachmentException;
import de.c3seidenstrasse.networkcontroller.utils.NotFoundException;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import de.c3seidenstrasse.networkcontroller.utils.TreeIntegrityException;

@SuppressWarnings("unused")
public class NetworkTest {

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testRouteTo() throws TreeIntegrityException, NoAttachmentException {
		Network n = new Network();
		final CodeReader cr1 = n.getRoot();
		final Router r = cr1.createRouterAt(1, "r");
		final Router eins = r.createRouterAt(1, "eins");
		final Router zwei = r.createRouterAt(2, "zwei");
		final Exit e1 = eins.createExitAt(1, "e1");
		final Exit e2 = eins.createExitAt(2, "e2");
		zwei.createExitAt(1, "e3");
		final Exit e4 = zwei.createExitAt(2, "e4");

		try {
			assertEquals(r, eins.getIntersectionOf(e1, e4));
			assertEquals(r, eins.getIntersectionOf(eins, zwei));
			assertEquals(r, eins.getIntersectionOf(e2, e4));
			assertEquals(eins, eins.getIntersectionOf(e1, e2));
			assertEquals(eins, zwei.getIntersectionOf(e1, e2));
		} catch (NotFoundException e) {
			e.printStackTrace();
			fail();
		}

		// Check Exception
		final CodeReader cr2 = new Network().getRoot();
		try {
			eins.getIntersectionOf(e1, cr2);
			fail();
		} catch (NotFoundException e) {
			// should throw an error because cr2 is not part of the network
		}
	}

	@Test
	public void testTransport() throws TreeIntegrityException, NoAttachmentException {
		final CodeReader cr1 = new Network().getRoot();
		final Router r = cr1.createRouterAt(1, "r");
		final Router eins = r.createRouterAt(1, "eins");
		final Router zwei = r.createRouterAt(2, "zwei");
		final Exit e1 = eins.createExitAt(1, "e1");
		eins.createExitAt(2, "e2");
		zwei.createExitAt(1, "e3");
		final Exit e4 = zwei.createExitAt(2, "e4");

		Transport t1;
		try {t1 = new Transport(e1, e4);}
		catch (RouteNotFoundException e) {
			e.printStackTrace();
			fail();
			return;
		}

		assertEquals(e1, t1.getStart());
		assertEquals(e4, t1.getEnde());

		final LinkedList<IndexedNetworkComponent> waypoints = new LinkedList<>(t1.getTransportWaypoints());
		final IndexedNetworkComponent first = waypoints.poll();
		final IndexedNetworkComponent second = waypoints.poll();
		final IndexedNetworkComponent third = waypoints.poll();
		final IndexedNetworkComponent fourth = waypoints.poll();
		assertEquals(0, waypoints.size());

		assertEquals(eins, first.getNc());
		assertEquals(1, (int) first.getI());
		assertEquals(r, second.getNc());
		assertEquals(1, (int) second.getI());
		assertEquals(r, third.getNc());
		assertEquals(2, (int) third.getI());
		assertEquals(zwei, fourth.getNc());
		assertEquals(2, (int) fourth.getI());

		assertEquals(0, t1.getAirflow().size());
	}
	
	@Test
	public void testTransportManagement() throws NotFoundException, RouteNotFoundException, InterruptedException, NoAttachmentException {
		Network n = new Network();
		n.getRoot().create33c3();
		
		NetworkComponent centralNode = n.getRoot().getChild();
		NetworkComponent poc = centralNode.getChildAt(3).getChildAt(3);
		NetworkComponent F2_E2 = centralNode.getChildAt(3).getChildAt(4).getChildAt(4).getChildAt(3);
		
		Transport t1 = new Transport(poc, F2_E2);
		System.out.println(t1);
		
		n.addTransport(t1);
		
		while (!n.getState().isFinished()) {
			synchronized (this) {
				this.wait(1000);
			}
		}
		
		assertEquals(new Integer(3), t1.getHighestPoint().getCurrentExit());
		assertEquals(new Integer(3), centralNode.getCurrentExit());
	}

	@Test
	public void testCongressNetwork() throws NotFoundException, RouteNotFoundException, NoAttachmentException {
		Network n = new Network();
		n.getRoot().create33c3();

		NetworkComponent centralNode = n.getRoot().getChild();
		NetworkComponent poc = centralNode.getChildAt(3).getChildAt(3);
		assertEquals("POC/Heaven", poc.getName());
		NetworkComponent F2_E2 = centralNode.getChildAt(3).getChildAt(4).getChildAt(4).getChildAt(3);
		NetworkComponent F1_E0 = centralNode.getChildAt(3).getChildAt(4).getChildAt(1);
		assertEquals("F1_E0", F1_E0.getName());
		assertEquals("F2_E2", F2_E2.getName());

		Transport t1 = new Transport(poc, F2_E2);

		// check static fields
		assertEquals(poc, t1.getStart());
		assertEquals(F2_E2, t1.getEnde());
		assertEquals(centralNode.getChildAt(3), t1.getHighestPoint());

		// check up route
		assertEquals(1, t1.getUp().size());
		Iterator<IndexedNetworkComponent> iu1 = t1.getUp().iterator();
		assertEquals(centralNode.getChildAt(3), iu1.next().getNc());

		// check down route
		assertEquals(3, t1.getDown().size());
		Iterator<IndexedNetworkComponent> id1 = t1.getDown().iterator();
		assertEquals(centralNode.getChildAt(3), id1.next().getNc());
		assertEquals(centralNode.getChildAt(3).getChildAt(4), id1.next().getNc());
		assertEquals(centralNode.getChildAt(3).getChildAt(4).getChildAt(4), id1.next().getNc());

		// check airflow route
		assertEquals(1, t1.getAirflow().size());
		Iterator<IndexedNetworkComponent> ia1 = t1.getAirflow().iterator();
		assertEquals(centralNode, ia1.next().getNc());

		Transport t2 = new Transport(F1_E0, F2_E2);
		
		// check static fields
		assertEquals(F1_E0, t2.getStart());
		assertEquals(F2_E2, t2.getEnde());
		assertEquals(centralNode.getChildAt(3).getChildAt(4), t2.getHighestPoint());

		// check up route
		assertEquals(1, t2.getUp().size());
		Iterator<IndexedNetworkComponent> iu2 = t2.getUp().iterator();
		assertEquals(centralNode.getChildAt(3).getChildAt(4), iu2.next().getNc());

		// check down route
		assertEquals(2, t2.getDown().size());
		Iterator<IndexedNetworkComponent> id2 = t2.getDown().iterator();
		assertEquals(centralNode.getChildAt(3).getChildAt(4), id2.next().getNc());
		assertEquals(centralNode.getChildAt(3).getChildAt(4).getChildAt(4), id2.next().getNc());

		// check airflow route
		assertEquals(2, t2.getAirflow().size());
		Iterator<IndexedNetworkComponent> ia2 = t2.getAirflow().iterator();
		//assertEquals(centralNode, ia2.next().getNc());
		//assertEquals(centralNode.getChildAt(3), ia2.next().getNc());
		// TODO iterator is random
	}
}
