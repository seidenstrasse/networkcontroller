package de.c3seidenstrasse.networkcontroller.route;

import java.util.Iterator;
import java.util.LinkedList;

import de.c3seidenstrasse.networkcontroller.network.Exit;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.utils.NoCurrentTransportException;
import de.c3seidenstrasse.networkcontroller.utils.Observer;
import de.c3seidenstrasse.networkcontroller.utils.ObserverEvent;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import javafx.collections.ObservableList;

public class Interconnect implements Observer {
	private final NetworkComponent from;
	private final NetworkComponent to;
	private final LinkedList<NetworkComponent> route;

	private final LinkedList<NetworkComponent> unknownStatus;

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

	@Override
	public String toString() {
		return "Interconnect [from=" + this.from + ", to=" + this.to + "]";
	}

	synchronized public void setUpRoute() {
		this.from.getNetwork().getAirsupplier().stop();
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

		this.route.forEach((inc) -> {
			inc.register(this, ObserverEvent.POSITIONCHANGED);
			inc.turnTo(inc.getIndexInParent());
		});
	}

	synchronized public void check() {
		if (this.unknownStatus.size() == 0) {
			this.myList.remove(this);
		}

		this.route.forEach((inc) -> {
			inc.deregister(this, ObserverEvent.POSITIONCHANGED);
		});
	}

	@Override
	synchronized public void update(final NetworkComponent nc) {
		final Iterator<NetworkComponent> i = this.unknownStatus.iterator();
		while (i.hasNext()) {
			final NetworkComponent current = i.next();
			if (current.equals(nc)) {
				if (nc.getCurrentExit() == current.getIndexInParent()) {
					// hat alles geklappt
					i.remove();
				}
				return;
			}
		}
	}
}
