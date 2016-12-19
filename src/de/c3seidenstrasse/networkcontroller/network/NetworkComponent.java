package de.c3seidenstrasse.networkcontroller.network;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.NoAttachmentException;
import de.c3seidenstrasse.networkcontroller.utils.NotFoundException;
import de.c3seidenstrasse.networkcontroller.utils.ObserverEvent;
import de.c3seidenstrasse.networkcontroller.utils.IdAlreadyExistsException;
import de.c3seidenstrasse.networkcontroller.utils.NetworkComponentObservee;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import de.c3seidenstrasse.networkcontroller.utils.SpaceOccupiedException;
import de.c3seidenstrasse.networkcontroller.utils.TreeIntegrityException;
import javafx.scene.control.TreeItem;

/**
 * This class represents all NetworkComponents in the Silkroad-Network
 *
 * @author clarity
 */
public abstract class NetworkComponent extends NetworkComponentObservee {
	private Integer currentExit;
	private final Network network;
	private final Integer id;

	NetworkComponent(final Integer id, final Network network) throws IdAlreadyExistsException {
		this.network = network;
		this.id = id;
		network.addNodeToMap(id, this);
		this.setCurrentExit(0);
	}

	/**
	 * gives the parent of this NetworkComponent
	 *
	 * @return {@link NetworkComponent} The network component which is the
	 *         parent of this component
	 * @throws NoAttachmentException
	 *             if this component has no parent attached
	 */
	public abstract NetworkComponent getParent() throws NoAttachmentException;

	/**
	 * returns the position of a specified child element below this
	 * NetworkComponent
	 *
	 * @param child
	 *            The NetworkComponent you are looking for
	 * @return The attachment number where the child is connected
	 * @throws NotFoundException
	 *             if the specified {@code child} is not directly below this
	 *             NetworkComponent
	 * @throws NoAttachmentException
	 *             if the specified {@code child} has no attachments
	 */
	public abstract Integer getPositionOf(final NetworkComponent child) throws NotFoundException, NoAttachmentException;

	/**
	 * returns the {@linkplain NetworkComponent} which is at the position
	 * {@code position}
	 *
	 * @param position
	 *            the selected position
	 * @return the {@linkplain NetworkComponent} at position {@code position}
	 * @throws {@linkplain
	 *             NoAttachmentException} if there is no attachment at the given
	 *             position
	 */
	public abstract NetworkComponent getChildAt(final Integer position) throws NoAttachmentException;

	/**
	 * gives the name of this {@linkplain NetworkComponent}
	 *
	 * @return the name of this {@linkplain NetworkComponent}
	 */
	public abstract String getName();

	/**
	 * outputs the set of all children
	 *
	 * @return a set of all children
	 */
	public abstract Set<IndexedNetworkComponent> getChildren();

	/**
	 * adds a {@linkplain NetworkComponent} as a child of this NetworkComponent
	 * at a given position
	 *
	 * @param position
	 *            the position this
	 * @param nc
	 * @throws NoAttachmentException
	 *             if this NetworkComponent can not attach a child at the given
	 *             position
	 * @throws TreeIntegrityException
	 *             if this would create a circle
	 */
	protected abstract void addChildAt(Integer position, NetworkComponent nc)
			throws NoAttachmentException, TreeIntegrityException, SpaceOccupiedException;

	public abstract Exit createExitAt(Integer position, final Integer id, String name)
			throws NoAttachmentException, IdAlreadyExistsException;

	public abstract Router createRouterAt(Integer position, final Integer id, String Name)
			throws NoAttachmentException, IdAlreadyExistsException;

	public abstract void fillRoute(Transport t, NetworkComponent target);

	public abstract LinkedList<IndexedNetworkComponent> RouteTo(NetworkComponent target) throws RouteNotFoundException;

	public boolean hasChild(final NetworkComponent nc) {
		if (this.equals(nc))
			return true;
		final Iterator<IndexedNetworkComponent> i = this.getChildren().iterator();
		while (i.hasNext())
			if (i.next().getNc().hasChild(nc))
				return true;
		return false;
	}

	public boolean hasAllChilds(final Set<NetworkComponent> nc) {
		final Iterator<NetworkComponent> i = nc.iterator();
		while (i.hasNext()) {
			final NetworkComponent current = i.next();
			if (!this.hasChild(current)) {
				return false;
			}
		}
		return true;
	}

	public NetworkComponent getIntersectionOf(final NetworkComponent first, final NetworkComponent second)
			throws NotFoundException {
		final HashSet<NetworkComponent> nc = new HashSet<>();
		nc.add(first);
		nc.add(second);

		if (this.hasAllChilds(nc)) {
			final Iterator<IndexedNetworkComponent> iterator = this.getChildren().iterator();
			while (iterator.hasNext()) {
				final IndexedNetworkComponent current = iterator.next();
				if (current.getNc().hasAllChilds(nc)) {
					return current.getNc().getIntersectionOf(first, second);
				}
			}
			return this;
		} else {
			try {
				return this.getParent().getIntersectionOf(first, second);
			} catch (final NoAttachmentException e) {
				throw new NotFoundException(e);
			}
		}
	}

	public Integer getCurrentExit() {
		return this.currentExit;
	}

	public abstract void turnTo(final Integer Exit);

	public void setCurrentExit(final Integer currentExit) {
		this.currentExit = currentExit;
		this.notifyObservers(this, ObserverEvent.POSITIONCHANGED);
	}

	public Network getNetwork() {
		return this.network;
	}

	public void capsulePassed() {
		System.out.println("SIMULATOR: Kapsel erkannt bei " + this.getName());
		this.notifyObservers(this, ObserverEvent.CAPSULEPASS);
	}

	public abstract TreeItem<NetworkComponent> getTreeItem();
}
