package de.c3seidenstrasse.networkcontroller.network;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import de.c3seidenstrasse.networkcontroller.manager.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.NoAttachmentException;
import de.c3seidenstrasse.networkcontroller.utils.NotFoundException;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import de.c3seidenstrasse.networkcontroller.utils.SpaceOccupiedException;
import de.c3seidenstrasse.networkcontroller.utils.TreeIntegrityException;
import javafx.scene.control.TreeItem;

/**
 * This class represents all Silkroad Routers.
 *
 * @author clarity
 */
public class Router extends NetworkComponent {

	private final NetworkComponent parent;
	private final Set<IndexedNetworkComponent> childs;

	private final String name;

	Router(final String name, final Network network, final NetworkComponent parent) {
		super(network);
		this.parent = parent;
		this.childs = new HashSet<>();
		this.name = name;
	}

	@Override
	public NetworkComponent getParent() throws NoAttachmentException {
		if (this.parent == null)
			throw new NoAttachmentException();
		return this.parent;
	}

	@Override
	public Set<IndexedNetworkComponent> getChildren() {
		return this.childs;
	}

	@Override
	public void addChildAt(final Integer position, final NetworkComponent nc)
			throws TreeIntegrityException, SpaceOccupiedException {
		this.addChild(new IndexedNetworkComponent(nc, position));
	}

	// pull up?
	@Override
	public Integer getPositionOf(final NetworkComponent child) throws NotFoundException {
		final Iterator<IndexedNetworkComponent> i = this.childs.iterator();
		while (i.hasNext()) {
			final IndexedNetworkComponent current = i.next();
			if (current.getNc().equals(child))
				return current.getI();
		}
		throw new NotFoundException("No Position found for " + child.toString());
	}

	@Override
	public NetworkComponent getChildAt(final Integer position) throws NoAttachmentException {
		final Iterator<IndexedNetworkComponent> i = this.childs.iterator();
		while (i.hasNext()) {
			final IndexedNetworkComponent current = i.next();
			if (current.getI().equals(position))
				return current.getNc();
		}
		throw new NoAttachmentException("No Attachment at position " + position);
	}

	private void addChild(final IndexedNetworkComponent inc) throws TreeIntegrityException, SpaceOccupiedException {
		if (inc.getNc().hasChild(this))
			throw new TreeIntegrityException(this.toString() + "is already a child of " + inc.getNc().toString());
		try {
			if (!inc.getNc().getParent().equals(this))
				throw new TreeIntegrityException("Parent on " + inc.getNc().toString() + "is not set properly!");
		} catch (final NoAttachmentException e1) {
			throw new TreeIntegrityException("Parent on " + inc.getNc().toString() + "is not set!");
		}

		try {
			this.getChildAt(inc.getI());
		} catch (final NoAttachmentException e) {
			// no child on this position
			this.childs.add(inc);
			return;
		}
		throw new SpaceOccupiedException(this.toString() + " has already a child at " + inc.getI());
	}

	@Override
	public Exit createExitAt(final Integer position, final String name) {
		final Exit e = new Exit(name, this.getNetwork(), this);
		try {
			this.addChildAt(position, e);
		} catch (SpaceOccupiedException | TreeIntegrityException e1) {
			throw new Error(); // should not happen
		}
		return e;
	}

	@Override
	public Router createRouterAt(final Integer position, final String name) {
		final Router r = new Router(name, this.getNetwork(), this);
		try {
			this.addChildAt(position, r);
		} catch (final TreeIntegrityException | SpaceOccupiedException e) {
			throw new Error(); // Should not happen
		}
		return r;
	}

	/**
	 * return INC with the next hop and node
	 */
	private IndexedNetworkComponent getNextExitFor(final NetworkComponent nc) throws NotFoundException {
		final Iterator<IndexedNetworkComponent> i = this.childs.iterator();
		while (i.hasNext()) {
			final IndexedNetworkComponent inc = i.next();
			if (inc.getNc().hasChild(nc))
				return inc;
		}
		throw new NotFoundException();
	}

	@Override
	public void fillRoute(final Transport t, final NetworkComponent target) {
		if (this.hasChild(target)) {
			// ziel ist unter mir, down list
			Integer nextExitFor;
			try {
				nextExitFor = this.getNextExitFor(target).getI();
				t.addDown(new IndexedNetworkComponent(this, nextExitFor));
				this.getChildAt(nextExitFor).fillRoute(t, target);
			} catch (final NoAttachmentException | NotFoundException e) {
				throw new Error(); // should not happen, structure is not
									// correct
			}
		} else {
			// ziel ist über mir, up list
			try {
				t.addUp(new IndexedNetworkComponent(this, this.getPositionOf(t.getLastUp())));
			} catch (final NotFoundException e) {
				throw new Error(); // should not happen, structure is not
									// correct
			}
			this.parent.fillRoute(t, target);
		}
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public LinkedList<IndexedNetworkComponent> RouteTo(final NetworkComponent target) throws RouteNotFoundException {
		LinkedList<IndexedNetworkComponent> route;
		if (this.equals(target)) {
			route = new LinkedList<>();
		} else {
			IndexedNetworkComponent via;
			try {
				via = this.getNextExitFor(target);
			} catch (final NotFoundException e) {
				throw new RouteNotFoundException(e);
			}
			route = via.getNc().RouteTo(target);
			route.addFirst(new IndexedNetworkComponent(this, via.getI()));
		}
		return route;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * sends a command to a router to change his direction. this method is
	 * asynchronous
	 */
	@Override
	public void turnTo(final Integer Exit) {
		final Thread t = new Thread(() -> {
			try {
				synchronized (this) {
					this.wait((long) ((Math.random() * Network.ROUTERMAXWAIT) + 1));
				}
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(
					"SIMULATOR: Router " + this.toString() + " hat eine Endlage bei " + Exit + " eingenommen!");
			Router.this.setCurrentExit(Exit);
		});
		t.start();
	}

	@Override
	public TreeItem<NetworkComponent> getTreeItem() {
		final TreeItem<NetworkComponent> root = new TreeItem<>(this);
		root.setExpanded(true);
		final Iterator<IndexedNetworkComponent> i = this.childs.iterator();
		while (i.hasNext())
			root.getChildren().add(i.next().getNc().getTreeItem());
		return root;
	}

}
