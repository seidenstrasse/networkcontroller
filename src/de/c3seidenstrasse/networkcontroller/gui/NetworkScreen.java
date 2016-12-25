package de.c3seidenstrasse.networkcontroller.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NetworkScreen extends Application {
	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.initStyle(StageStyle.DECORATED);
		BorderPane page;
		try {
			final URL resource = this.getClass().getResource("NetworkScreen.fxml");
			final FXMLLoader loader = new FXMLLoader(resource);
			page = (BorderPane) loader.load();
			final Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Network Display");
			// primaryStage.setResizable(false);
			final NetworkScreenController c = loader.getController();
			c.init();
			primaryStage.setOnCloseRequest(e -> {
				Platform.exit();
			});
			primaryStage.sizeToScene();
			primaryStage.show();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(final String[] args) {
		javafx.application.Application.launch(NetworkScreen.class);
	}

}
