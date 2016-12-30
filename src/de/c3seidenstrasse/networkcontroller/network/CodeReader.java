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
import de.c3seidenstrasse.networkcontroller.utils.TreeIntegrityException;
import javafx.scene.control.TreeItem;

public class CodeReader extends NetworkComponent {
	@Expose
	final String name = "CodeReader";

	@Override
	public String toString() {
		return this.name;
	}

	private NetworkComponent child;

	public CodeReader(final Integer id, final Network network) throws IdAlreadyExistsException {
		super(id, network, 0, 0);
		this.setCurrentExit(1);
	}

	@Override
	public NetworkComponent getParent() throws NoAttachmentException {
		throw new NoAttachmentException("CodeReader has no parent!");
	}

	@Override
	public Set<NetworkComponent> getIndexedChildren() {
		final Set<NetworkComponent> set = new HashSet<>();
		set.add(child);
		return set;
	}

	@Override
	protected void addChildAt(final Integer position, final NetworkComponent nc, final int transferDuration)
			throws TreeIntegrityException, NoAttachmentException {
		if (!position.equals(1))
			throw new NoAttachmentException("There is no attachment at position " + position.toString());
		this.setChild(nc);
	}

	@Override
	public Exit createExitAt(final Integer position, final Integer id, final String name, final int transferDuration)
			throws NoAttachmentException, IdAlreadyExistsException {
		final Exit e = new Exit(id, name, this.getNetwork(), this, position, transferDuration);
		try {
			this.addChildAt(position, e, transferDuration);
		} catch (final TreeIntegrityException e1) {
			throw new Error(); // should not happen
		}
		return e;
	}

	@Override
	public Router createRouterAt(final Integer position, final Integer id, final String name,
			final int transferDuration) throws NoAttachmentException, IdAlreadyExistsException {
		final Router r = new Router(id, name, this.getNetwork(), this, position, transferDuration);
		try {
			this.addChildAt(position, r, transferDuration);
		} catch (final TreeIntegrityException e1) {
			throw new Error(); // should not happen
		}
		return r;
	}

	@Override
	public void fillRoute(final Transport t, final NetworkComponent target) {
		if (this.hasChild(target)) {
			t.addDown(this);
			this.getChild().fillRoute(t, target);
		} else
			throw new Error();
	}

	@Override
	public Integer getPositionOf(final NetworkComponent child) throws NotFoundException {
		if (this.getChild().equals(child))
			return 1;
		throw new NotFoundException();
	}

	@Override
	public LinkedList<NetworkComponent> RouteTo(final NetworkComponent target) throws RouteNotFoundException {
		return this.getChild().RouteTo(target);
	}

	public void create33c3() {
		final CodeReader cr = this;
		try {
			final Router alice = cr.createRouterAt(1, 11, "Alice", 5);
			alice.createExitAt(2, 1, "Seidenstrasse", 5);
			alice.createExitAt(0, 2, "Spacestation", 20);
			final Router betty = alice.createRouterAt(1, 22, "Betty", 30);
			betty.createExitAt(2, 3, "Eingang", 50);
			betty.createExitAt(1, 4, "Sendezentrum", 20);
			final Router caty = betty.createRouterAt(0, 33, "Caty", 40);
			caty.createExitAt(5, 5, "Wizzard", 100);
			caty.createExitAt(1, 6, "Kidspace", 110);
			caty.createExitAt(0, 7, "Coffeenerds", 120);
			caty.createExitAt(4, 8, "Lounge", 130);
			caty.createExitAt(3, 9, "FoodHacker", 140);
			caty.createExitAt(2, 10, "Monolith", 150);
		} catch (final NoAttachmentException | IdAlreadyExistsException e) {
			e.printStackTrace();
		}
	}

	public NetworkComponent getChild() {
		return this.child;
	}

	private void setChild(final NetworkComponent child) {
		this.child = child;
	}

	@Override
	public NetworkComponent getChildAt(final Integer position) throws NoAttachmentException {
		if (position.equals(1))
			return this.child;
		throw new NoAttachmentException();
	}

	@Override
	public String getName() {
		return this.toString();
	}

	@Override
	public void turnTo(final Integer Exit) {
		throw new UnsupportedOperationException("A CodeReader can nor turn!");
	}

	@Override
	public TreeItem<NetworkComponent> getTreeItem() {
		final TreeItem<NetworkComponent> root = new TreeItem<>(this);
		root.setExpanded(true);
		root.getChildren().add(this.child.getTreeItem());
		return root;
	}

}
