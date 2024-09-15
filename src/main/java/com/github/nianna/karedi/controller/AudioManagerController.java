package com.github.nianna.karedi.controller;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.audio.PreloadedAudioFile;
import com.github.nianna.karedi.context.ActionContext;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.context.AudioContext;
import com.github.nianna.karedi.control.SliderTableCell;
import com.github.nianna.karedi.util.ContextMenuBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.util.Optional;

public class AudioManagerController implements Controller {
	@FXML
	private AnchorPane pane;
	@FXML
	private TableView<PreloadedAudioFile> table;
	@FXML
	private TableColumn<PreloadedAudioFile, String> nameColumn;
	@FXML
	private TableColumn<PreloadedAudioFile, DoubleProperty> volumeColumn;
	@FXML
	private ContextMenu baseContextMenu;

	private AudioContext audioContext;

	private ActionContext actionContext;

	@FXML
	public void initialize() {
		nameColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getName()));
		volumeColumn.setCellValueFactory(
				item -> new SimpleObjectProperty<>(item.getValue().volumeProperty()));

		volumeColumn.setCellFactory(params -> new SliderTableCell<>(0.0, 1.0));

		nameColumn.setMaxWidth(Integer.MAX_VALUE * 75f); // 75% width
		volumeColumn.setMaxWidth(Integer.MAX_VALUE * 25f); // 25% width
	}

	private Callback<TableView<PreloadedAudioFile>, TableRow<PreloadedAudioFile>> getRowFactory() {
		return (tv -> {
			TableRow<PreloadedAudioFile> row = new TableRow<>();

			ContextMenuBuilder builder = new ContextMenuBuilder();
			builder.addItem(I18N.get("common.add"), this::handleAdd);
			builder.addItem(I18N.get("common.delete"), event -> handleRemove(row.getItem()));

			row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then(baseContextMenu)
					.otherwise(builder.getResult()));
			return row;
		});
	}

	@Override
	public void setAppContext(AppContext appContext) {
		this.audioContext = appContext.getAudioContext();
		this.actionContext = appContext.getActionContext();

		table.setRowFactory(getRowFactory());

		audioContext.activeAudioFileProperty().addListener(
				(obsValue, oldValue, newValue) -> table.getSelectionModel().select(newValue));

		table.setItems(audioContext.getAudioFiles());
		table.disableProperty().bind(appContext.getActiveSongContext().activeSongProperty().isNull());

		table.getSelectionModel().selectedItemProperty().addListener((obsVal, oldVal, newVal) -> {
			if (newVal != null && newVal != audioContext.getActiveAudioFile()) {
				audioContext.setActiveAudioFile(newVal);
			}
		});
	}

	@FXML
	private void handleAdd(ActionEvent event) {
		actionContext.execute(KarediActions.IMPORT_AUDIO);
	}

	private void handleRemove(PreloadedAudioFile file) {
		audioContext.removeAudioFile(file);
	}

	@Override
	public Node getContent() {
		return pane;
	}

	@FXML
	private void onKeyPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.DELETE)) {
			confirmAndRemoveSelected();
			event.consume();
		}
	}

	private void confirmAndRemoveSelected() {
		if (!table.getSelectionModel().isEmpty()) {
			PreloadedAudioFile file = table.getSelectionModel().getSelectedItem();
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle(I18N.get("dialog.delete_audio.title"));
			alert.setHeaderText(I18N.get("dialog.delete_audio.header"));
			alert.setContentText(file.getName());
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				handleRemove(file);
			}
		}
	}
}
