package de.c3seidenstrasse.networkcontroller.network;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.annotations.Expose;

import de.c3seidenstrasse.networkcontroller.route.Message;
import de.c3seidenstrasse.networkcontroller.route.Message.MessageType;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.IdAlreadyExistsException;
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
	@Expose
	private final TreeMap<Integer, NetworkComponent> childrenByIndex;

	@Expose
	private final String name;

	Router(final Integer id, final String name, final Network network, final NetworkComponent parent, int i, int duration)
			throws IdAlreadyExistsException, NoAttachmentException, TreeIntegrityException, SpaceOccupiedException {
		super(id, network, parent, i, duration);
		this.parent = parent;
		this.childrenByIndex = new TreeMap<>();
		this.name = name;
	}

	@Override
	public Map<Integer,NetworkComponent> getIndexedChildren() {
		return this.childrenByIndex;
	}

	@Override
	public void addChildAt(final Integer position, final NetworkComponent nc)
			throws TreeIntegrityException, SpaceOccupiedException {
		assert(position == nc.getIndexInParent());
		this.addChild(nc);
	}

	public NetworkComponent getIncOf(final NetworkComponent child) throws NotFoundException {
		final Iterator<NetworkComponent> i = this.childrenByIndex.values().iterator();
		while (i.hasNext()) {
			final NetworkComponent current = i.next();
			if (current.equals(child))
				return current;
		}
		throw new NotFoundException("No Position found for " + child.toString());
	}

	@Override
	public NetworkComponent getChildAt(final Integer position) throws NoAttachmentException {
		final Iterator<NetworkComponent> i = this.childrenByIndex.values().iterator();
		while (i.hasNext()) {
			final NetworkComponent current = i.next();
			if (current.getIndexInParent().equals(position))
				return current;
		}
		throw new NoAttachmentException("No Attachment at position " + position);
	}

	private void addChild(final NetworkComponent inc) throws TreeIntegrityException, SpaceOccupiedException {
		if (inc.hasChild(this))
			throw new TreeIntegrityException(this.toString() + "is already a child of " + inc.toString());
		try {
			if (!inc.getParent().equals(this))
				throw new TreeIntegrityException("Parent on " + inc.toString() + "is not set properly!");
		} catch (final NoAttachmentException e1) {
			throw new TreeIntegrityException("Parent on " + inc.toString() + "is not set!");
		}

		try {
			this.getChildAt(inc.getIndexInParent());
		} catch (final NoAttachmentException e) {
			// no child on this position
			this.childrenByIndex.put(inc.getIndexInParent(),inc);
			return;
		}
		throw new SpaceOccupiedException(this.toString() + " has already a child at " + inc.getIndexInParent());
	}

	@Override
	public Router createRouterAt(final Integer position, final Integer id, final String name,
			final int transferDuration) throws IdAlreadyExistsException, NoAttachmentException, TreeIntegrityException, SpaceOccupiedException {
		final Router r = new Router(id, name, this.getNetwork(), this, position, transferDuration);
		return r;
	}

	/**
	 * return INC with the next hop and node
	 */
	private NetworkComponent getNextExitFor(final NetworkComponent nc) throws NotFoundException {
		final Iterator<NetworkComponent> i = this.childrenByIndex.values().iterator();
		while (i.hasNext()) {
			final NetworkComponent inc = i.next();
			if (inc.hasChild(nc))
				return inc;
		}
		throw new NotFoundException();
	}

	@Override
	public void fillRoute(final Transport t, final NetworkComponent target) {
		if (this.hasChild(target)) {
			// ziel ist unter mir, down list
			final Integer nextExitFor;
			try {
				final NetworkComponent inc = this.getNextExitFor(target);
				t.addDown(this);
				this.getChildAt(inc.getIndexInParent()).fillRoute(t, target); // TODO
																	// replace
																	// by
																	// inc
			} catch (final NoAttachmentException | NotFoundException e) {
				throw new Error(); // should not happen, structure is not
									// correct
			}
		} else {
			// ziel ist �ber mir, up list
			try {
				final NetworkComponent deruntermir = this.getIncOf(t.getLastUp());
				t.addUp(this);
			} catch (final NotFoundException e) {
				throw new Error(); // should not happen, structure is not
									// correct
			}
			this.parent.fillRoute(t, target);
		}
	}

	@Override
	public LinkedList<NetworkComponent> RouteTo(final NetworkComponent target) throws RouteNotFoundException {
		LinkedList<NetworkComponent> route;
		if (this.equals(target)) {
			route = new LinkedList<>();
		} else {
			NetworkComponent via;
			try {
				via = this.getNextExitFor(target);
			} catch (final NotFoundException e) {
				throw new RouteNotFoundException(e);
			}
			route = via.RouteTo(target);
			route.addFirst(this);
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
	public void turnTo(final Integer exit) {
		this.getNetwork().send(new Message(MessageType.CONNECT, 0, getId(), exit));
	}

	@Override
	public TreeItem<NetworkComponent> getTreeItem() {
		final TreeItem<NetworkComponent> root = new TreeItem<>(this);
		root.setExpanded(true);
		final Iterator<NetworkComponent> i = this.childrenByIndex.values().iterator();
		while (i.hasNext())
			root.getChildren().add(i.next().getTreeItem());
		return root;
	}

}
