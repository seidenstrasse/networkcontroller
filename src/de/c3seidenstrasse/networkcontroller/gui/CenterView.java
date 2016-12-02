package de.c3seidenstrasse.networkcontroller.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public abstract class CenterView extends VBox {
	protected final Label title;

	protected final VBox content;
	protected final HBox bottom;

	protected final Label left1;
	protected final Label left2;
	protected final Label right1;
	protected final Label right2;

	protected CenterView() {
		this.setPadding(new Insets(0, 20, 0, 20));

		this.content = new VBox();
		this.bottom = new HBox();
		this.content.prefHeightProperty().bind(this.heightProperty().subtract(this.bottom.heightProperty()));
		this.getChildren().addAll(this.content, this.bottom);

		this.title = new Label("Hallo123");
		this.title.setFont(new Font(36));
		this.content.getChildren().add(this.title);

		final GridPane subtitle = new GridPane();
		subtitle.setGridLinesVisible(true);
		final ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);
		final ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(50);
		subtitle.getColumnConstraints().addAll(column1, column2);
		this.content.getChildren().add(subtitle);

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
