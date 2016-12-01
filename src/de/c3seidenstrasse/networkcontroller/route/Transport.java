package de.c3seidenstrasse.networkcontroller.route;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import de.c3seidenstrasse.networkcontroller.manager.Network;
import de.c3seidenstrasse.networkcontroller.network.IndexedNetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.utils.NotFoundException;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;

public class Transport {
	private static int LASTTRANSPORTID = 0;
	private final int transportid;
	private final Network network;
	private final NetworkComponent start;
	private final NetworkComponent ende;
	private final NetworkComponent highestPoint;
	private final LinkedList<IndexedNetworkComponent> up;
	private final LinkedList<IndexedNetworkComponent> down;
	private final HashSet<IndexedNetworkComponent> airflow;

	public Transport(final NetworkComponent start, final NetworkComponent ende) throws RouteNotFoundException {
		this.transportid = Transport.LASTTRANSPORTID++;

		this.up = new LinkedList<>();
		this.down = new LinkedList<>();
		this.airflow = new HashSet<>();

		if (!start.getNetwork().equals(ende.getNetwork())) {
			throw new IllegalArgumentException();
		}

		this.network = start.getNetwork();
		this.start = start;
		this.ende = ende;

		try {
			this.highestPoint = this.getNetwork().getRoot().getIntersectionOf(start, ende);
		} catch (final NotFoundException e1) {
			throw new RouteNotFoundException(e1);
		}

		this.up.addAll(this.getHighestPoint().RouteTo(start));
		Collections.reverse(this.up);
		this.down.addAll(this.getHighestPoint().RouteTo(ende));
		this.getAirflow().addAll(this.getNetwork().getRoot().RouteTo(this.getHighestPoint()));
	}

	@Override
	public String toString() {
		return "Transport #" + this.transportid;
	}

	public String visualRoute() {
		return "Transport [start=" + this.start + ", ende=" + this.ende + ", highestPoint=" + this.highestPoint
				+ ",\nup=" + this.up + ",\ndown=" + this.down + ",\nairflow=" + this.airflow + "]";
	}

	public void addUp(final IndexedNetworkComponent inc) {
		this.getUp().add(inc);
	}

	public void addDown(final IndexedNetworkComponent inc) {
		this.getDown().add(inc);
	}

	public LinkedList<IndexedNetworkComponent> getTransportWaypoints() {
		final LinkedList<IndexedNetworkComponent> l = new LinkedList<>(this.getUp());
		l.addAll(this.getDown());
		return l;
	}

	public NetworkComponent getLastUp() {
		return this.getUp().getLast().getNc();
	}

	public LinkedList<IndexedNetworkComponent> getUp() {
		return this.up;
	}

	public LinkedList<IndexedNetworkComponent> getDown() {
		return this.down;
	}

	public NetworkComponent getStart() {
		return this.start;
	}

	public NetworkComponent getEnde() {
		return this.ende;
	}

	public NetworkComponent getHighestPoint() {
		return this.highestPoint;
	}

	public HashSet<IndexedNetworkComponent> getAirflow() {
		return this.airflow;
	}

	public Network getNetwork() {
		return this.network;
	}

	public int getId() {
		return this.transportid;
	}
}
