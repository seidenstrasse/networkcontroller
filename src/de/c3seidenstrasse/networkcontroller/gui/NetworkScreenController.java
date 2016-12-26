package de.c3seidenstrasse.networkcontroller.gui;

import de.c3seidenstrasse.networkcontroller.network.IndexedNetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Network;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.ComboBox;

public class NetworkScreenController {
	private Network n;
	@FXML
	public TreeView<NetworkComponent> ncTree;
	public NetworkComponent selected;
	@FXML
	ComboBox<IndexedNetworkComponent> childCombobox;

	public Network init() {
		this.n = Network.create();

		// Netzwerkliste
		this.selected = this.n.getRoot();
		final TreeItem<NetworkComponent> ti = this.n.getRoot().getTreeItem();
		this.ncTree.setRoot(ti);
		this.ncTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			this.selected(newValue.getValue());
		});

		return this.n;
	}

	private void selected(final NetworkComponent nc) {
		this.selected = nc;
		this.childCombobox.getItems().clear();
		this.childCombobox.getItems().addAll(this.selected.getIndexedChildren());
		this.childCombobox.getItems().sort((arg0, arg1) -> {
			if (arg0.getI() < arg1.getI())
				return -1;
			else if (arg0.getI() > arg1.getI())
				return 1;
			return 0;
		});
		this.childCombobox.setVisibleRowCount(this.childCombobox.getItems().size());
	}

	public void turnToAction() {
		final IndexedNetworkComponent inc = this.childCombobox.getValue();
		this.selected.turnTo(inc.getI());
	}
}
