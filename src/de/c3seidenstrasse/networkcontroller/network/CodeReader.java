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
		super(id, network);
		this.setCurrentExit(1);
	}

	@Override
	public NetworkComponent getParent() throws NoAttachmentException {
		throw new NoAttachmentException("CodeReader has no parent!");
	}

	@Override
	public Set<IndexedNetworkComponent> getIndexedChildren() {
		final Set<IndexedNetworkComponent> set = new HashSet<>();
		set.add(new IndexedNetworkComponent(this.getChild(), 1));
		return set;
	}

	@Override
	protected void addChildAt(final Integer position, final NetworkComponent nc)
			throws TreeIntegrityException, NoAttachmentException {
		if (!position.equals(1))
			throw new NoAttachmentException("There is no attachment at position " + position.toString());
		this.setChild(nc);
	}

	@Override
	public Exit createExitAt(final Integer position, final Integer id, final String name)
			throws NoAttachmentException, IdAlreadyExistsException {
		final Exit e = new Exit(id, name, this.getNetwork(), this);
		try {
			this.addChildAt(position, e);
		} catch (final TreeIntegrityException e1) {
			throw new Error(); // should not happen
		}
		return e;
	}

	@Override
	public Router createRouterAt(final Integer position, final Integer id, final String name)
			throws NoAttachmentException, IdAlreadyExistsException {
		final Router r = new Router(id, name, this.getNetwork(), this);
		try {
			this.addChildAt(position, r);
		} catch (final TreeIntegrityException e1) {
			throw new Error(); // should not happen
		}
		return r;
	}

	@Override
	public void fillRoute(final Transport t, final NetworkComponent target) {
		if (this.hasChild(target)) {
			t.addDown(new IndexedNetworkComponent(this, 1));
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
	public LinkedList<IndexedNetworkComponent> RouteTo(final NetworkComponent target) throws RouteNotFoundException {
		return this.getChild().RouteTo(target);
	}

	public void create33c3() {
		final CodeReader cr = this;
		try {
			final Router alice = cr.createRouterAt(1, 11, "Alice");
			alice.createExitAt(1, 1, "Seidenstrasse");
			alice.createExitAt(2, 2, "Spacestation");
			final Router betty = alice.createRouterAt(3, 22, "Betty");
			betty.createExitAt(1, 3, "Eingang");
			betty.createExitAt(2, 4, "Sendezentrum");
			final Router caty = betty.createRouterAt(3, 33, "Caty");
			caty.createExitAt(1, 5, "Wizzard");
			caty.createExitAt(2, 6, "Kidspace");
			caty.createExitAt(3, 7, "Coffeenerds");
			caty.createExitAt(4, 8, "Lounge");
			caty.createExitAt(5, 9, "FoodHacker");
			caty.createExitAt(6, 10, "Monolith");
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
