package de.c3seidenstrasse.networkcontroller.route;

import java.util.Iterator;
import java.util.LinkedList;

import de.c3seidenstrasse.networkcontroller.network.Exit;
import de.c3seidenstrasse.networkcontroller.network.IndexedNetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.utils.NoCurrentTransportException;
import de.c3seidenstrasse.networkcontroller.utils.Observer;
import de.c3seidenstrasse.networkcontroller.utils.ObserverEvent;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import javafx.collections.ObservableList;

public class Interconnect implements Observer {
	private final NetworkComponent from;
	private final NetworkComponent to;
	private final LinkedList<IndexedNetworkComponent> route;

	private final LinkedList<IndexedNetworkComponent> unknownStatus;

	private final ObservableList<Interconnect> myList;

	public Interconnect(final NetworkComponent from, final NetworkComponent to,
			final ObservableList<Interconnect> myList) throws RouteNotFoundException, NoCurrentTransportException {
		if (!(from instanceof Exit) && !(to instanceof Exit))
			throw new NoCurrentTransportException("A route must go from an exit to an router or vice versa!");

		this.from = from;
		this.to = to;

		this.route = this.from.RouteTo(this.to);
		this.unknownStatus = new LinkedList<>(this.route);

		this.myList = myList;
	}

	synchronized public void setUpRoute() {
		this.from.getNetwork().getAirsupplier().stop();
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

		this.route.forEach((inc) -> {
			inc.getNc().register(this, ObserverEvent.POSITIONCHANGED);
			inc.getNc().turnTo(inc.getI());
		});
	}

	synchronized public void check() {
		if (this.unknownStatus.size() == 0) {
			this.myList.remove(this);
		}

		this.route.forEach((inc) -> {
			inc.getNc().deregister(this, ObserverEvent.POSITIONCHANGED);
		});
	}

	@Override
	synchronized public void update(final NetworkComponent nc) {
		final Iterator<IndexedNetworkComponent> i = this.unknownStatus.iterator();
		while (i.hasNext()) {
			final IndexedNetworkComponent current = i.next();
			if (current.equals(nc)) {
				if (nc.getCurrentExit() == current.getI()) {
					// hat alles geklappt
					i.remove();
				}
				return;
			}
		}
	}
}
