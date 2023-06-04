package com.github.nianna.karedi.controller;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import org.controlsfx.validation.decoration.ValidationDecoration;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import com.github.nianna.karedi.command.tag.DeleteTagCommand;
import com.github.nianna.karedi.command.tag.ReorderTagsCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.control.RestrictedTextField;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.song.tag.TagValidators;
import com.github.nianna.karedi.util.ContextMenuBuilder;
import com.github.nianna.karedi.util.Converter;
import com.github.nianna.karedi.util.MathUtils;
import com.github.nianna.karedi.util.NumericNodeUtils;
import com.github.nianna.karedi.util.TableViewUtils;
import com.github.nianna.karedi.util.ValidationUtils;

public class TagsTableController implements Controller {
	@FXML
	private AnchorPane pane;
	@FXML
	private TableView<Tag> table;
	@FXML
	private TableColumn<Tag, String> keyColumn;
	@FXML
	private TableColumn<Tag, String> valueColumn;
	@FXML
	private ContextMenu baseContextMenu;

	private AppContext appContext;
	private Song song;

	@FXML
	private void initialize() {
		keyColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getKey()));
		valueColumn.setCellValueFactory(cell -> cell.getValue().valueProperty());
		valueColumn.setCellFactory(
				params -> new TagValueTableCell(tag -> TagValidators.forKey(tag.getKey())));
	}

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
		appContext.activeSongProperty().addListener((obs, oldVal, newVal) -> display(newVal));

		table.setRowFactory(getRowFactory());
		TableViewUtils.makeRowsDraggable(table, TransferMode.MOVE, mode -> {
			switch (mode) {
			case MOVE:
				return this::consumeDrag;
			default:
				return (draggedIndices, dropIndex) -> {
				};
			}
		});
	}

	@Override
	public Node getContent() {
		return pane;
	}

	@FXML
	private void onTableMouseClicked(MouseEvent event) {
		if (table.getEditingCell() != null && table.isFocused()) {
			table.refresh();
		}
	}

	@FXML
	private void onValueColumnEditStart(CellEditEvent<Tag, String> event) {
		table.setOnKeyPressed(keyEvent -> keyEvent.consume());
		table.requestFocus();
	}

	@FXML
	private void onValueColumnEditCancel(CellEditEvent<Tag, String> event) {
		table.setOnKeyPressed(this::onKeyPressed);
	}

	@FXML
	private void onValueColumnEditCommit(CellEditEvent<Tag, String> event) {
		table.setOnKeyPressed(this::onKeyPressed);
		changeTagValue(event.getRowValue().getKey(), event.getNewValue());
	}

	@FXML
	private void onKeyColumnEditStart(CellEditEvent<Tag, String> event) {
		table.edit(event.getTablePosition().getRow(), valueColumn);
	}

	@FXML
	private void onKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE) {
			table.getSelectionModel().getSelectedItems().forEach(this::handleRemove);
			event.consume();
		}
	}

	private Callback<TableView<Tag>, TableRow<Tag>> getRowFactory() {
		return (tv -> {
			TableRow<Tag> row = new TableRow<Tag>();

			row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then(baseContextMenu)
					.otherwise(getContextMenuForRow(row)));

			row.itemProperty().addListener((obs, oldItem, newItem) -> {
				row.setOnScroll(null);
				if (newItem != null) {
					TagKey.optionalValueOf(newItem.getKey()).ifPresent(tagKey -> {
						row.setOnScroll(
								getScrollHandlerForRow(row, tagKey, newItem.valueProperty()));
					});
				}
			});

			return row;
		});
	}

	private ContextMenu getContextMenuForRow(TableRow<Tag> row) {
		ContextMenuBuilder builder = new ContextMenuBuilder();
		builder.addItem(I18N.get("common.add"), event -> handleAdd());
		builder.addItem(I18N.get("common.edit"), event -> handleEdit(row.getIndex()));
		builder.addItem(I18N.get("common.delete"), event -> handleRemove(row.getItem()));
		return builder.getResult();
	}

	private EventHandler<ScrollEvent> getScrollHandlerForRow(TableRow<Tag> row, TagKey key,
			ReadOnlyStringProperty value) {
		EventHandler<ScrollEvent> handler = null;
		if (TagKey.expectsAnInteger(key)) {
			handler = NumericNodeUtils.createUpdateIntValueOnScrollHandler(
					() -> Converter.toInteger(value.get()), newValue -> changeTagValueIfValid(key,
							Converter.toString(newValue.intValue())));
		}
		if (TagKey.expectsADouble(key)) {
			handler = NumericNodeUtils.createUpdateDoubleValueOnScrollHandler(
					() -> Converter.toDouble(value.get()), newValue -> changeTagValueIfValid(key,
							Converter.toString(MathUtils.roundTo(newValue.doubleValue(), 3))));
		}
		return adaptScrollHandlerForRow(row, handler);
	}

	private EventHandler<ScrollEvent> adaptScrollHandlerForRow(TableRow<Tag> row,
			EventHandler<ScrollEvent> handler) {
		if (handler != null) {
			return (event -> {
				if (row.isSelected() && !row.isEditing()) {
					handler.handle(event);
				}
			});
		}
		return null;
	}

	@FXML
	private void handleAdd() {
		appContext.execute(KarediActions.ADD_TAG);
	}

	private void handleEdit(int index) {
		if (index >= 0) {
			table.edit(index, valueColumn);
		}
	}

	private void handleRemove(Tag tag) {
		appContext.execute(new DeleteTagCommand(song, tag));
	}

	private void changeTagValueIfValid(TagKey key, String value) {
		if (!TagValidators.hasValidationErrors(key, value)) {
			appContext.execute(new ChangeTagValueCommand(appContext.getSong(), key, value));
		}
	}

	private void changeTagValue(String key, String value) {
		appContext.execute(new ChangeTagValueCommand(appContext.getSong(), key, value));
	}

	private void display(Song song) {
		this.song = song;
		if (song != null) {
			table.setItems(song.getTags());
		}
		table.setDisable(song == null);
	}

	private void consumeDrag(List<Integer> draggedIndices, Integer dropIndex) {
		Collections.reverse(draggedIndices);
		draggedIndices.forEach(index -> {
			appContext.execute(new ReorderTagsCommand(song, index, absoluteDropIndex(dropIndex)));
		});
		table.getSelectionModel().select(absoluteDropIndex(dropIndex));
	}

	private int absoluteDropIndex(int dropIndex) {
		return dropIndex == -1 ? table.getItems().size() - 1 : dropIndex;
	}

	private class TagValueTableCell extends TableCell<Tag, String> {
		private final RestrictedTextField textField = new RestrictedTextField("");
		private Function<Tag, Validator<String>> validatorSupplier;
		private Validator<String> validator;
		private ValidationDecoration validationDecoration = new GraphicValidationDecoration();
		private Tag lastTag;

		private ValidationResult applyValidator() {
			return validator.apply(textField, textField.getText());
		}

		TagValueTableCell(Function<Tag, Validator<String>> validatorSupplier) {
			this.validatorSupplier = validatorSupplier;
			textField.setOnAction(event -> {
				if (applyValidator().getErrors().size() == 0) {
					commitEdit(textField.getText());
				}
				event.consume();
			});
			textField.setOnKeyReleased(t -> {
				if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
					t.consume();
				}
			});
			textField.textProperty().addListener(obs -> {
				ValidationResult result = applyValidator();
				validationDecoration.removeDecorations(textField);
				ValidationUtils.getHighestPriorityMessage(result)
						.ifPresent(msg -> validationDecoration.applyValidationDecoration(msg));
			});
		}

		@Override
		public void startEdit() {
			if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
				return;
			}
			super.startEdit();
			TableRow<Tag> row = getTableRow();
			Tag tag = row.getItem();
			setGraphic(textField);
			setPadding(new Insets(5));

			validator = validatorSupplier.apply(tag);
			TagValidators.forbiddenCharacterRegex(tag.getKey()).ifPresent(regex -> {
				textField.setForbiddenCharacterRegex(regex);
			});
			textField.setText(getText());
			setText(null);
			textField.selectAll();

			textField.requestFocus();

		}

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			TableRow<Tag> row = getTableRow();
			if (empty || item == null || row == null || row.getItem() == null) {
				clearContent();
			} else {
				addContent(item);
			}
		}

		private void addContent(String item) {
			TableRow<Tag> row = getTableRow();
			Tag tag = row.getItem();
			if (lastTag != tag & isEditing()) {
				cancelEdit();
			}

			if (isEditing()) {
				setText(null);
				setGraphic(textField);
				textField.setText(getItemText());
			} else {
				setPadding(new Insets(2)); // necessary for validation
											// decoration to fit nicely
				setText(item);
				setGraphic(null);
			}

			lastTag = tag;
		}

		private void clearContent() {
			setText(null);
			setGraphic(null);
			textField.textProperty().unbind();
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();
			setPadding(new Insets(0));
			setText(getItemText());
			setGraphic(null);
		}

		private String getItemText() {
			return getItem();
		}
	}
}
