package de.c3seidenstrasse.networkcontroller.gui;

import java.util.Iterator;
import java.util.Map.Entry;

import de.c3seidenstrasse.networkcontroller.network.Exit;
import de.c3seidenstrasse.networkcontroller.network.IndexedNetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;

public class NetworkScreenController {
	private Network n;
	@FXML
	public TreeView<NetworkComponent> ncTree;
	public NetworkComponent selected;
	@FXML
	ComboBox<IndexedNetworkComponent> childCombobox;
	@FXML
	ComboBox<Exit> fromDropdown;
	@FXML
	ComboBox<Exit> toDropdown;
	@FXML
	Button addTransportButton;

	public Network init() {
		this.n = Network.create(false);

		// Netzwerkliste
		this.selected = this.n.getRoot();
		final TreeItem<NetworkComponent> ti = this.n.getRoot().getTreeItem();
		this.ncTree.setRoot(ti);
		this.ncTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			this.selected(newValue.getValue());
		});

		// fill dropdowns
		final ObservableList<Exit> exits = FXCollections.observableArrayList();
		final Iterator<Entry<Integer, NetworkComponent>> i = this.n.getIdMap().entrySet().iterator();
		while (i.hasNext()) {
			final NetworkComponent current = i.next().getValue();
			if (current instanceof Exit)
				exits.add((Exit) current);
		}
		this.fromDropdown.setItems(exits);
		this.toDropdown.setItems(exits);

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

	@FXML
	public void addRouteAction() {
		final Exit start = this.fromDropdown.getValue();
		final Exit ende = this.toDropdown.getValue();
		if (start == null || ende == null)
			return;
		try {
			final Transport t = new Transport(this.fromDropdown.getValue(), this.toDropdown.getValue());
			this.n.addTransport(t);
			this.addTransportButton.setStyle("-fx-base: #b6e7c9;");
			Platform.runLater(() -> {
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
				}
				NetworkScreenController.this.addTransportButton.setStyle("");
			});
		} catch (final RouteNotFoundException e) {
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Route not found");
			alert.setHeaderText("works not for you");
			alert.setContentText("I was not able to find a route between your selected targets!");
			alert.show();
		}
	}
}
