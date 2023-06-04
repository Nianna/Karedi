package com.github.nianna.karedi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

public final class TableViewUtils {
	private TableViewUtils() {
	}

	public static <T> void makeRowsDraggable(TableView<T> tableView, TransferMode mode,
			Function<TransferMode, BiConsumer<List<Integer>, Integer>> dropConsumer) {

		// TODO check on OSX, supposedly may not work correctly
		DataFormat dataFormat = new DataFormat(String.valueOf(tableView.hashCode()));
		Callback<TableView<T>, TableRow<T>> rowFactory = tableView.getRowFactory();
		tableView.setRowFactory(tv -> {
			TableRow<T> row = rowFactory == null ? new TableRow<>() : rowFactory.call(tableView);
			row.setOnDragDetected(event -> {
				if (!row.isEmpty()) {
					ArrayList<Integer> indices = new ArrayList<Integer>(
							tv.getSelectionModel().getSelectedIndices());
					Dragboard db = row.startDragAndDrop(mode);
					db.setDragView(row.snapshot(null, null));
					ClipboardContent cc = new ClipboardContent();
					cc.put(dataFormat, indices);
					db.setContent(cc);
				}
				event.consume();
			});

			row.setOnDragOver(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasContent(dataFormat)) {
					event.acceptTransferModes(mode);
				}
				event.consume();
			});

			row.setOnDragDropped(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasContent(dataFormat)) {
					ArrayList<Integer> draggedIndices = (ArrayList<Integer>) db
							.getContent(dataFormat);
					int dropIndex = row.isEmpty() ? -1 : row.getIndex();
					dropConsumer.apply(event.getTransferMode()).accept(draggedIndices, dropIndex);
					event.setDropCompleted(true);

				}
				event.consume();
			});

			return row;
		});
	}
}
