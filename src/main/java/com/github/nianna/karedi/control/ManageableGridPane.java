package main.java.com.github.nianna.karedi.control;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

public class ManageableGridPane extends GridPane {

	public List<Node> getRow(int index) {
		return getChildren().stream().filter(child -> getChildRowIndex(child) == index)
				.collect(Collectors.toList());
	}

	public List<Node> getColumn(int index) {
		return getChildren().stream().filter(child -> getChildColumnIndex(child) == index)
				.collect(Collectors.toList());
	}

	public int getChildRowIndex(Node child) {
		return Optional.ofNullable(GridPane.getRowIndex(child)).orElse(0);
	}

	public int getChildColumnIndex(Node child) {
		return Optional.ofNullable(GridPane.getColumnIndex(child)).orElse(0);
	}

	public RowConstraints getRowConstraints(int index) {
		return getRowConstraints().get(index);
	}

	public ColumnConstraints getColumnConstraints(int index) {
		return getColumnConstraints().get(index);
	}

	public void changeColumnVisibility(int index, boolean visible) {
		getColumn(index).forEach(node -> node.setVisible(visible));
		getColumnConstraints(index).setMaxWidth(visible ? Region.USE_COMPUTED_SIZE : 0);
	}

	public void changeRowVisibility(int index, boolean visible) {
		getRow(index).forEach(node -> node.setVisible(visible));
		getRowConstraints(index).setMaxHeight(visible ? Region.USE_COMPUTED_SIZE : 0);
	}

	public int getMaxRowIndex() {
		return getChildren().stream().map(this::getChildRowIndex).max(Integer::compare).orElse(0);
	}

	public int getMaxColumnIndex() {
		return getChildren().stream().map(this::getChildColumnIndex).max(Integer::compare)
				.orElse(0);
	}

	public void addRow(Node... children) {
		if (children.length > 0) {
			addRow(getMaxRowIndex() + 1, children);
		}
	}
}
