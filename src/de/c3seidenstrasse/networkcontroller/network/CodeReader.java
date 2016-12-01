package de.c3seidenstrasse.networkcontroller.network;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.NoAttachmentException;
import de.c3seidenstrasse.networkcontroller.utils.NotFoundException;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import de.c3seidenstrasse.networkcontroller.utils.TreeIntegrityException;
import javafx.scene.control.TreeItem;

public class CodeReader extends NetworkComponent {

	@Override
	public String toString() {
		return "CodeReader";
	}

	private NetworkComponent child;

	public CodeReader(final Network network) {
		super(network);
		this.setCurrentExit(1);
	}

	@Override
	public NetworkComponent getParent() throws NoAttachmentException {
		throw new NoAttachmentException("CodeReader has no parent!");
	}

	@Override
	public Set<IndexedNetworkComponent> getChildren() {
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
	public Exit createExitAt(final Integer position, final String name) throws NoAttachmentException {
		final Exit e = new Exit(name, this.getNetwork(), this);
		try {
			this.addChildAt(position, e);
		} catch (final TreeIntegrityException e1) {
			throw new Error(); // should not happen
		}
		return e;
	}

	@Override
	public Router createRouterAt(final Integer position, final String name) throws NoAttachmentException {
		final Router r = new Router(name, this.getNetwork(), this);
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
			final Router cn = cr.createRouterAt(1, "Central Node");
			cn.createExitAt(1, "Minecraft");
			cn.createExitAt(2, "Pilz");
			final Router gf1 = cn.createRouterAt(3, "GF1");
			gf1.createExitAt(1, "Sendezentrum");
			gf1.createExitAt(2, "GF2");
			gf1.createExitAt(3, "POC/Heaven");
			final Router f1 = gf1.createRouterAt(4, "F1");
			f1.createExitAt(1, "F1_E0");
			f1.createExitAt(2, "F1_E1");
			f1.createExitAt(3, "F1_E2");
			final Router f2 = f1.createRouterAt(4, "F2");
			f2.createExitAt(1, "F2_E0");
			f2.createExitAt(2, "F2_E1");
			f2.createExitAt(3, "F2_E2");
		} catch (final NoAttachmentException e) {
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
