package de.c3seidenstrasse.networkcontroller.gui;

import java.util.Iterator;
import java.util.Map.Entry;

import de.c3seidenstrasse.networkcontroller.network.Exit;
import de.c3seidenstrasse.networkcontroller.network.IndexedNetworkComponent;
import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Interconnect;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.utils.NoCurrentTransportException;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

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
	@FXML
	ListView<Network.Message> messageLog;
	@FXML
	VBox rightVbox;
	@FXML
	ListView<Interconnect> connectLog;

	public Network init() {
		this.n = Network.create();

		// Message Queue
		this.messageLog.setItems(this.n.getBusProtocolHistory());
		this.messageLog.prefHeightProperty().bind(this.rightVbox.heightProperty().divide(2));
		this.n.getBusProtocolHistory().addListener((ListChangeListener<Network.Message>) c -> {
			this.messageLog.scrollTo(this.n.getBusProtocolHistory().size());
		});

		// Transport Queue
		this.connectLog.prefHeightProperty().bind(this.rightVbox.heightProperty().divide(2));

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
		try {
			final Interconnect ic = new Interconnect(start, ende, this.connectLog.getItems());
			ic.setUpRoute();
			this.connectLog.getItems().add(ic);
		} catch (RouteNotFoundException | NoCurrentTransportException e) {
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error in executing Interconnect");
			alert.setContentText(e.getMessage());

			alert.show();
		}
	}

	@FXML
	public void pullAction() {
		this.n.getAirsupplier().pull();
	}

	@FXML
	public void pushAction() {
		this.n.getAirsupplier().push();
	}

	@FXML
	public void stopAction() {
		this.n.getAirsupplier().stop();
	}

	@FXML
	public void homeAction() {
		this.selected.home();
	}

	@FXML
	public void passedAction() {
		this.n.getState().arrived();
	}
}
