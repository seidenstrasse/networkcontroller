package de.c3seidenstrasse.networkcontroller.network;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.google.gson.annotations.Expose;

import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.IdAlreadyExistsException;
import de.c3seidenstrasse.networkcontroller.utils.NoAttachmentException;
import de.c3seidenstrasse.networkcontroller.utils.NotFoundException;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import javafx.scene.control.TreeItem;

public class Exit extends NetworkComponent {

	private static final String AN_EXIT_HAS_NO_ATTACHMENT = "An exit has no attachment.";
	private final NetworkComponent parent;
	@Expose
	private final String name;

	Exit(final Integer id, final String name, final Network network, final NetworkComponent parent, int i, int duration)
			throws IdAlreadyExistsException {
		super(id, network, i, duration);
		this.parent = parent;
		this.name = name;
		this.setCurrentExit(1);
	}

	@Override
	public NetworkComponent getParent() {
		return this.parent;
	}

	@Override
	public Set<NetworkComponent> getIndexedChildren() {
		return new HashSet<>();
	}

	@Override
	protected void addChildAt(final Integer position, final NetworkComponent nc, final int transferDuration)
			throws NoAttachmentException {
		throw new NoAttachmentException(Exit.AN_EXIT_HAS_NO_ATTACHMENT);
	}

	@Override
	public Exit createExitAt(final Integer position, final Integer id, final String name, final int transferDuration)
			throws NoAttachmentException {
		throw new NoAttachmentException(Exit.AN_EXIT_HAS_NO_ATTACHMENT);
	}

	@Override
	public Router createRouterAt(final Integer position, final Integer id, final String name,
			final int transferDuration) throws NoAttachmentException {
		throw new NoAttachmentException(Exit.AN_EXIT_HAS_NO_ATTACHMENT);
	}

	@Override
	public void fillRoute(final Transport t, final NetworkComponent target) {
		if (!this.hasChild(target)) {
			t.addUp(this);
			this.parent.fillRoute(t, target);
		} else {
			t.addDown(this);
		}
	}

	@Override
	public Integer getPositionOf(final NetworkComponent child) throws NoAttachmentException {
		throw new NoAttachmentException(Exit.AN_EXIT_HAS_NO_ATTACHMENT);
	}

	@Override
	public LinkedList<NetworkComponent> RouteTo(final NetworkComponent target) throws RouteNotFoundException {
		if (this.equals(target)) {
			final LinkedList<NetworkComponent> route = new LinkedList<>();
			// route.addFirst(new NetworkComponent(this, 1));
			return route;
		}
		throw new RouteNotFoundException();
	}

	@Override
	public NetworkComponent getChildAt(final Integer position) throws NoAttachmentException {
		throw new NoAttachmentException(Exit.AN_EXIT_HAS_NO_ATTACHMENT);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void turnTo(final Integer Exit) {
		throw new UnsupportedOperationException("A CodeReader can nor turn!");
	}

	@Override
	public TreeItem<NetworkComponent> getTreeItem() {
		return new TreeItem<>(this);
	}
}
