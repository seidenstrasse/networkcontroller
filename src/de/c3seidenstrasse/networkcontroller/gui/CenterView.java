package de.c3seidenstrasse.networkcontroller.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public abstract class CenterView extends VBox {
	protected final Label title;

	final Label left1;
	final Label left2;
	final Label right1;
	final Label right2;

	protected CenterView() {
		this.setPadding(new Insets(0, 20, 0, 20));

		this.title = new Label("Hallo123");
		this.title.setFont(new Font(36));
		this.getChildren().add(this.title);

		final GridPane subtitle = new GridPane();
		subtitle.setGridLinesVisible(true);
		final ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);
		final ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(50);
		subtitle.getColumnConstraints().addAll(column1, column2);
		this.getChildren().add(subtitle);

		final VBox left = new VBox();
		final VBox right = new VBox();
		right.setAlignment(Pos.TOP_RIGHT);
		subtitle.add(left, 0, 0);
		subtitle.add(right, 1, 0);

		this.left1 = new Label("Links 1");
		this.left2 = new Label("Links 2");
		left.getChildren().addAll(this.left1, this.left2);

		this.right1 = new Label("Rechts 1");
		this.right2 = new Label("Rechts 2");
		right.getChildren().addAll(this.right1, this.right2);
	}
}
