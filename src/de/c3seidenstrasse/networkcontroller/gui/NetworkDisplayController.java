package de.c3seidenstrasse.networkcontroller.gui;

import de.c3seidenstrasse.networkcontroller.network.NetworkComponent;
import de.c3seidenstrasse.networkcontroller.route.Network;
import de.c3seidenstrasse.networkcontroller.route.Transport;
import de.c3seidenstrasse.networkcontroller.utils.NoAttachmentException;
import de.c3seidenstrasse.networkcontroller.utils.RouteNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class NetworkDisplayController {
	private Network n;
	@FXML
	TreeView<NetworkComponent> tree;
	@FXML
	ListView<Transport> routelist;
	@FXML
	BorderPane borderpane;

	private final NetworkComponentView ncv;
	private final TransportView tv;
	@FXML
	VBox statusbox;
	@FXML
	VBox rightbox;
	@FXML
	VBox buttonbox;

	public NetworkDisplayController() {
		this.ncv = new NetworkComponentView();
		this.tv = new TransportView();
	}

	protected void init() {
		this.n = new Network();
		this.n.getRoot().create33c3();

		// Netzwerkliste
		final TreeItem<NetworkComponent> ti = this.n.getRoot().getTreeItem();
		this.tree.setRoot(ti);
		this.tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			this.ncv.update(newValue.getValue());
			System.out.println("New value: " + newValue.toString());
			this.borderpane.setCenter(this.ncv);
		});

		// Routenliste
		final ObservableList<Transport> ol = FXCollections.observableArrayList();
		try {
			final NetworkComponent poc = this.n.getRoot().getChild().getChildAt(3).getChildAt(3);
			final NetworkComponent F1_E0 = this.n.getRoot().getChild().getChildAt(3).getChildAt(4).getChildAt(4);
			final NetworkComponent F2_E0 = F1_E0.getChildAt(1);
			final NetworkComponent F2_E2 = F1_E0.getChildAt(3);
			final Transport t1 = new Transport(poc, F2_E2);
			final Transport t2 = new Transport(F2_E0, F2_E2);
			ol.addAll(t1, t2);
		} catch (final RouteNotFoundException | NoAttachmentException e) {
			e.printStackTrace();
		}
		this.routelist.setItems(ol);
		this.routelist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("New value: " + newValue.toString());
			this.tv.update(newValue);
			this.borderpane.setCenter(this.tv);
		});

		// Status
		this.statusbox.prefHeightProperty().bind(this.rightbox.heightProperty().subtract(this.buttonbox.getHeight()));

	}

	public void stopNetwork() {
		this.n.stop();
	}
}
