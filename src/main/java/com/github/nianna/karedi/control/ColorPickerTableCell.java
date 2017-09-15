package main.java.com.github.nianna.karedi.control;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class ColorPickerTableCell<T> extends TableCell<T, Color> {
	private final ColorPicker colorPicker = new ColorPicker();

	public ColorPickerTableCell(TableColumn<T, Color> column) {
		// Hide label
		colorPicker.setStyle("-fx-color-label-visible: false ;");
		colorPicker.setFocusTraversable(false);

		colorPicker.setOnShowing(event -> {
			final TableView<T> tableView = getTableView();
			tableView.getSelectionModel().clearAndSelect(getTableRow().getIndex());
			tableView.edit(tableView.getSelectionModel().getSelectedIndex(), column);
			colorPicker.requestFocus();
		});

		colorPicker.setOnAction(event -> {
			if (isEditing()) {
				commitEdit(colorPicker.getValue());
			}
		});

		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}

	@Override
	protected void updateItem(Color item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			setText(null);
			setGraphic(null);
		} else {
			colorPicker.setValue(item);
			setGraphic(colorPicker);
		}
	}

	public static <T> Callback<TableColumn<T, Color>, TableCell<T, Color>> forTableColumn(
			TableColumn<T, Color> column) {
		return (param -> {
			return new ColorPickerTableCell<>(param);
		});
	}
}
