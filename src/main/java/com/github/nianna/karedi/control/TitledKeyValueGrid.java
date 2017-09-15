package main.java.com.github.nianna.karedi.control;

import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TitledKeyValueGrid extends ManageableGridPane {

	public TitledKeyValueGrid() {
		setAlignment(Pos.CENTER);
		setHgap(10);
		setVgap(4);
	}

	public Label addTitle(Label titleLabel) {
		titleLabel.setPadding(new Insets(0, 0, 5, 0));
		titleLabel.getStyleClass().add("tooltip-header");
		addRow(titleLabel);
		GridPane.setColumnSpan(titleLabel, 2);
		GridPane.setHalignment(titleLabel, HPos.CENTER);
		return titleLabel;
	}

	public Label addTitle(ObservableValue<String> title) {
		return addTitle(createNewLabel(title));
	}

	public Label addRow(String key, Label valueLabel) {
		Label keyLabel = new Label(key);
		addRow(keyLabel, valueLabel);
		keyLabel.getStyleClass().add("tooltip-key");
		valueLabel.getStyleClass().add("tooltip-value");
		return valueLabel;
	}

	public void addRow(String key, ObservableValue<String> value) {
		addRow(key, createNewLabel(value));
	}

	private Label createNewLabel(ObservableValue<String> text) {
		Label label = new Label();
		label.textProperty().bind(text);
		return label;
	}

	public Label addRow(String key, String value) {
		return addRow(key, new Label(value));
	}
}
