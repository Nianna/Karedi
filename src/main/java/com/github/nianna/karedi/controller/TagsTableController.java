package com.github.nianna.karedi.controller;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import com.github.nianna.karedi.command.tag.DeleteTagCommand;
import com.github.nianna.karedi.command.tag.ReorderTagsCommand;
import com.github.nianna.karedi.context.ActionContext;
import com.github.nianna.karedi.context.ActiveSongContext;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.context.CommandContext;
import com.github.nianna.karedi.control.RestrictedTextField;
import com.github.nianna.karedi.event.ControllerEvent;
import com.github.nianna.karedi.event.TagsControllerEvent;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.FormatSpecification;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.song.tag.TagValueValidators;
import com.github.nianna.karedi.util.BindingsUtils;
import com.github.nianna.karedi.util.ContextMenuBuilder;
import com.github.nianna.karedi.util.Converter;
import com.github.nianna.karedi.util.MathUtils;
import com.github.nianna.karedi.util.NumericNodeUtils;
import com.github.nianna.karedi.util.TableViewUtils;
import com.github.nianna.karedi.util.ValidationUtils;
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
import javafx.scene.control.TablePosition;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import org.controlsfx.validation.decoration.ValidationDecoration;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class TagsTableController implements Controller {

    @FXML
    private TableView<Tag> table;

    @FXML
    private TableColumn<Tag, String> keyColumn;

    @FXML
    private TableColumn<Tag, String> valueColumn;

    @FXML
    private ContextMenu baseContextMenu;

    private ActiveSongContext activeSongContext;

    private ActionContext actionContext;

    private CommandContext commandContext;

    private Song song;

    private TablePosition<Tag, ?> nextFocusPosition;


    /**
     * Requests that this table get input focus. <br />
     * If it has been set in advance, focuses on the specified cell and causes it to switch into its editing state.
     *
     * @see TagsTableController#handleEvent(ControllerEvent)
     */
    @Override
    public void requestFocus() {
        Controller.super.requestFocus();
        if (nextFocusPosition != null) {
            table.getFocusModel().focus(nextFocusPosition);
            table.edit(nextFocusPosition.getRow(), nextFocusPosition.getTableColumn());
            nextFocusPosition = null;
        }
    }

    @FXML
    private void initialize() {
        keyColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getKey()));
        valueColumn.setCellValueFactory(cell -> cell.getValue().valueProperty());
        valueColumn.setCellFactory(
                params -> new TagValueTableCell(tag -> TagValueValidators.forKey(tag.getKey())));
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.activeSongContext = appContext.getActiveSongContext();
        this.actionContext = appContext.getActionContext();
        this.commandContext = appContext.getCommandContext();

        activeSongContext.activeSongProperty().addListener((obs, oldVal, newVal) -> display(newVal));

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
        return table;
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
        ContextMenu result = builder.getResult();
        result.getItems().forEach(item -> item.disableProperty().bind(table.editingCellProperty().isNotNull()));
        return result;
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
        actionContext.execute(KarediActions.ADD_TAG);
    }

    private void handleEdit(int index) {
        if (index >= 0) {
            table.edit(index, valueColumn);
        }
    }

    private void handleRemove(Tag tag) {
        commandContext.execute(new DeleteTagCommand(song, tag));
    }

    private void changeTagValueIfValid(TagKey key, String value) {
        if (!TagValueValidators.hasValidationErrors(key, value)) {
            commandContext.execute(new ChangeTagValueCommand(activeSongContext.getSong(), key, value));
        }
    }

    private void changeTagValue(String key, String value) {
        commandContext.execute(new ChangeTagValueCommand(activeSongContext.getSong(), key, value));
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
        draggedIndices.forEach(
                index -> commandContext.execute(new ReorderTagsCommand(song, index, absoluteDropIndex(dropIndex)))
        );
        table.getSelectionModel().select(absoluteDropIndex(dropIndex));
    }

    private int absoluteDropIndex(int dropIndex) {
        return dropIndex == -1 ? table.getItems().size() - 1 : dropIndex;
    }

    @Override
    public void handleEvent(ControllerEvent controllerEvent) {
        if (controllerEvent instanceof TagsControllerEvent tagsEvent) {
            tagsEvent.getAffectedKeys().stream()
                    .findFirst()
                    .flatMap(song::getTag)
                    .ifPresent(tag -> {
                        table.scrollTo(tag);
                        table.getSelectionModel().select(tag);
                        nextFocusPosition = new TablePosition<>(
                                table,
                                table.getSelectionModel().getSelectedIndex(),
                                valueColumn);
                    });
        }
    }

    private class TagValueTableCell extends TableCell<Tag, String> {
        private final RestrictedTextField textField = new RestrictedTextField("");
        private Function<Tag, Validator<String>> validatorSupplier;
        private Validator<String> validator;
        private ValidationDecoration validationDecoration = new GraphicValidationDecoration();
        private Tag lastTag;
        private AutoCompletionBinding<?> valueSuggestions;

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
            TagValueValidators.forbiddenCharacterRegex(tag.getKey()).ifPresent(textField::setForbiddenCharacterRegex);
            resetValueSuggestions(tag);
            textField.setText(getText());
            setText(null);
            textField.selectAll();

            textField.requestFocus();
        }

        private void resetValueSuggestions(Tag tag) {
            if (valueSuggestions != null) {
                valueSuggestions.dispose();
            }
            valueSuggestions = TagKey.optionalValueOf(tag.getKey())
                    .map(key -> BindingsUtils.bindAutoCompletion(
                            textField,
                            key.suggestedValues(),
                            FormatSpecification.supportsMultipleValues(song.getFormatSpecificationVersion(), key))
                    )
                    .orElse(null);
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
