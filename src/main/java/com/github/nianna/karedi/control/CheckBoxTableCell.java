package main.java.com.github.nianna.karedi.control;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class CheckBoxTableCell<S> extends TableCell<S, Boolean> {
	private ObservableValue<Boolean> boundTo;
	private CheckBox checkBox = new CheckBox();
	private ChangeListener<Boolean> valueListener;

	private boolean freezed = false;

	public CheckBoxTableCell(TableColumn<S, Boolean> column) {
		valueListener = ((obsVal, oldVal, newVal) -> {
			freezed = true;
			checkBox.setSelected(newVal);
			freezed = false;
		});

		this.getStyleClass().add("check-box-table-cell");

		checkBox.setFocusTraversable(false);
		checkBox.selectedProperty().addListener((obsVal, oldVal, newVal) -> {
			if (!freezed) {
				commitEdit(newVal);
			}
		});
	}

	@Override
	protected void updateItem(Boolean item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setGraphic(null);
		} else {
			checkBox.setSelected(item);
			setGraphic(checkBox);

			if (boundTo != null) {
				boundTo.removeListener(valueListener);
			}

			boundTo = getTableColumn().getCellObservableValue(getIndex());
			boundTo.addListener(valueListener);
		}
	}

	public static <S> Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>> forTableColumn(
			TableColumn<S, Boolean> column) {
		return (param -> {
			return new CheckBoxTableCell<>(param);
		});
	}

}