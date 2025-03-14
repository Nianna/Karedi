package com.github.nianna.karedi.dialog;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import com.github.nianna.karedi.control.ManageableGridPane;

public class CheckListViewDialog<T> extends StyleableDialog<List<T>> {
	private ManageableGridPane grid = new ManageableGridPane();
	private Function<T, String> stringConverter;
	private Optional<Integer> upperLimit = Optional.empty();
	private Optional<Integer> lowerLimit = Optional.empty();
	private LinkedList<T> selected = new LinkedList<>();
	private Map<T, CheckBox> checkBoxMap = new HashMap<>();

	public CheckListViewDialog(List<? extends T> items) {
		this(items, Object::toString);
	}

	public CheckListViewDialog(List<? extends T> items, Function<T, String> stringConverter) {
		this.stringConverter = stringConverter;
		items.forEach(item -> grid.addRow(createNodesForNewRow(item)));

		grid.setHgap(10);
		grid.setVgap(5);
		getDialogPane().setContent(grid);

		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		setOkButtonsDisable(true);

		setResultConverter(type -> {
			if (type == ButtonType.OK) {
				selected.sort((o1, o2) -> {
					return Integer.compare(items.indexOf(o1), items.indexOf(o2));
				});
				return selected;
			}
			return null;
		});

	}

	public void setUpperLimit(int upperLimit) {
		assert upperLimit > 0;
		this.upperLimit = Optional.of(upperLimit);
		while (selected.size() > upperLimit) {
			selected.remove();
		}
	}

	public void setLowerLimit(int lowerLimit) {
		this.lowerLimit = Optional.of(lowerLimit);
		updateButtonsState();
	}

	private Node[] createNodesForNewRow(T item) {
		CheckBox checkBox = new CheckBox();
		Label label = new Label(stringConverter.apply(item));
		checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
			onSelectedPropertyChanged(item, newVal);
		});
		label.setOnMouseClicked(event -> checkBox.setSelected(!checkBox.isSelected()));
		checkBoxMap.put(item, checkBox);
		Node[] nodes = { checkBox, label };
		return nodes;
	}

	private void onSelectedPropertyChanged(T item, boolean shouldSelect) {
		if (shouldSelect) {
			select(item);
		} else {
			deselect(item);
		}
	}

	private void setOkButtonsDisable(boolean value) {
		getDialogPane().getButtonTypes()
				.filtered(type -> type.getButtonData().equals(ButtonData.OK_DONE))
				.forEach(type -> getDialogPane().lookupButton(type).setDisable(value));

	}

	public void select(T item) {
		if (!isSelected(item)) {
			upperLimit.ifPresent(limit -> {
				if (selected.size() == limit) {
					deselect(selected.peek());
				}
			});
			selected.add(item);
			checkBoxMap.get(item).setSelected(true);
			updateButtonsState();
		}
	}

	public void deselect(T item) {
		if (isSelected(item)) {
			selected.remove(item);
			checkBoxMap.get(item).setSelected(false);
			updateButtonsState();
		}
	}

	public boolean isSelected(T item) {
		return selected.contains(item);
	}

	private void updateButtonsState() {
		lowerLimit.ifPresent(limit -> {
			setOkButtonsDisable(selected.size() < limit);
		});
	}
}
