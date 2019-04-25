package main.java.com.github.nianna.karedi.controller;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import main.java.com.github.nianna.karedi.command.track.ChangeTrackFontColorCommand;
import org.controlsfx.control.action.Action;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.command.track.ChangeTrackColorCommand;
import main.java.com.github.nianna.karedi.command.track.ChangeTrackNameCommand;
import main.java.com.github.nianna.karedi.command.track.ReorderTracksCommand;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.control.CheckBoxTableCell;
import main.java.com.github.nianna.karedi.control.ColorPickerTableCell;
import main.java.com.github.nianna.karedi.control.TitledKeyValueGrid;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.util.ContextMenuBuilder;
import main.java.com.github.nianna.karedi.util.TableViewUtils;

public class TracksController implements Controller {
	@FXML
	private AnchorPane pane;
	@FXML
	private TableView<SongTrack> table;
	@FXML
	private TableColumn<SongTrack, Color> colorColumn;
	@FXML
	private TableColumn<SongTrack, Color> fontColorColumn;
	@FXML
	private TableColumn<SongTrack, String> nameColumn;
	@FXML
	private TableColumn<SongTrack, Boolean> visibleColumn;
	@FXML
	private TableColumn<SongTrack, Boolean> mutedColumn;
	@FXML
	private ContextMenu baseContextMenu;

	private AppContext appContext;
	private Song song;

	@FXML
	public void initialize() {
		configureColorColumn();
		configureFontColorColumn();
		configureNameColumn();
		configureVisibleColumn();
		configureMutedColumn();

		mutedColumn.setMaxWidth(Integer.MAX_VALUE * 15f);
		visibleColumn.setMaxWidth(Integer.MAX_VALUE * 15f);
		nameColumn.setMaxWidth(Integer.MAX_VALUE * 50f);
	}

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
		song = appContext.getSong();

		appContext.activeSongProperty().addListener(obs -> display(appContext.getSong()));
		appContext.activeTrackProperty().addListener(obs -> {
			table.getSelectionModel().select(appContext.getActiveTrack());
		});

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
		table.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
	}

	private Callback<TableView<SongTrack>, TableRow<SongTrack>> getRowFactory() {
		return (tv -> {
			TableRow<SongTrack> row = new TableRow<>();

			row.itemProperty().addListener((obsVal, oldItem, newItem) -> {
				if (newItem != null) {
					row.setContextMenu(getContextMenuForRow(row));
					row.setTooltip(new TrackTooltip(newItem));
				} else {
					row.setContextMenu(baseContextMenu);
					row.setTooltip(null);
				}
			});
			return row;
		});
	}

	private ContextMenu getContextMenuForRow(TableRow<SongTrack> row) {
		ContextMenuBuilder builder = new ContextMenuBuilder();
		bind(builder.addItem(I18N.get("common.add")), KarediActions.ADD_TRACK);
		builder.addItem(I18N.get("common.edit"), event -> handleEdit(row.getIndex()));
		bind(builder.addItem(I18N.get("common.delete")), KarediActions.DELETE_TRACK);
		return builder.getResult();
	}

	private void bind(MenuItem menuItem, KarediActions actionKey) {
		Action action = appContext.getAction(actionKey);
		menuItem.disableProperty().bind(action.disabledProperty());
		menuItem.setOnAction(action::handle);
	}

	private void handleEdit(int index) {
		if (index >= 0) {
			table.edit(index, nameColumn);
		}
	}

	@FXML
	private void handleAdd() {
		appContext.execute(KarediActions.ADD_TRACK);
	}

	@FXML
	private void onKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE) {
			appContext.execute(KarediActions.DELETE_TRACK);
			event.consume();
		}
	}

	private void configureColorColumn() {
		colorColumn.setCellValueFactory(cell -> cell.getValue().colorProperty());
		colorColumn.setCellFactory(ColorPickerTableCell.forTableColumn(colorColumn));
	}

	private void configureFontColorColumn() {
		fontColorColumn.setCellValueFactory(cell -> cell.getValue().fontColorProperty());
		fontColorColumn.setCellFactory(ColorPickerTableCell.forTableColumn(fontColorColumn));
	}

	@FXML
	private void onColorColumnEditCommit(CellEditEvent<SongTrack, Color> event) {
		appContext.execute(new ChangeTrackColorCommand(event.getRowValue(), event.getNewValue()));
		table.requestFocus();
	}

	@FXML
	private void onFontColorColumnEditCommit(CellEditEvent<SongTrack, Color> event) {
		appContext.execute(new ChangeTrackFontColorCommand(event.getRowValue(), event.getNewValue()));
		table.requestFocus();
	}

	private void configureNameColumn() {
		nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
	}

	@FXML
	private void onNameColumnEditStart(CellEditEvent<SongTrack, String> event) {
		table.setOnKeyPressed(keyEvent -> keyEvent.consume());
	}

	@FXML
	private void onNameColumnEditCancel(CellEditEvent<SongTrack, String> event) {
		table.setOnKeyPressed(this::onKeyPressed);
	}

	@FXML
	private void onNameColumnEditCommit(CellEditEvent<SongTrack, String> event) {
		table.setOnKeyPressed(this::onKeyPressed);
		appContext.execute(new ChangeTrackNameCommand(event.getRowValue(), event.getNewValue()));
		table.requestFocus();
	}

	private void configureVisibleColumn() {
		visibleColumn.setCellValueFactory(cell -> cell.getValue().visibleProperty());
		visibleColumn.setCellFactory(column -> new TrackPropertyCheckboxTableCell(column,
				(track, isVisible) -> track.setVisible(isVisible)));
	}

	private void configureMutedColumn() {
		mutedColumn.setCellValueFactory(cell -> cell.getValue().mutedProperty());
		mutedColumn.setCellFactory(
				column -> new TrackPropertyCheckboxTableCell(column, (track, isMuted) -> {
					appContext.execute(KarediActions.STOP_PLAYBACK);
					track.setMuted(isMuted);
				}));
	}

	@Override
	public Node getContent() {
		return pane;
	}

	private void onSelectedItemChanged(Observable obs, SongTrack oldTrack, SongTrack newTrack) {
		SongTrack activeTrack = appContext.getActiveTrack();
		if (newTrack != null && newTrack != activeTrack) {
			appContext.setActiveTrack(newTrack);
		} else {
			table.getSelectionModel().select(activeTrack);
		}
	}

	private void display(Song song) {
		this.song = song;
		if (song != null) {
			table.setItems(song.getTracks());
			table.setDisable(false);
		} else {
			table.setItems(null);
			table.setDisable(true);
		}
		table.refresh();
	}

	private void consumeDrag(List<Integer> draggedIndices, Integer dropIndex) {
		Collections.reverse(draggedIndices);
		draggedIndices.forEach(index -> {
			appContext.execute(new ReorderTracksCommand(song, index,
					dropIndex == -1 ? table.getItems().size() - 1 : dropIndex));
		});
		appContext.setActiveTrack(song.get(dropIndex));
	}

	private class TrackTooltip extends Tooltip {
		private TitledKeyValueGrid grid = new TitledKeyValueGrid();

		public TrackTooltip(SongTrack track) {
			grid.addTitle(track.nameProperty());
			Label bonusLabel = grid.addRow(I18N.get("tracks.tooltip.golden_bonus"), "");
			Label beatLabel = grid.addRow(I18N.get("tracks.tooltip.beat_range"), "");
			Label toneLabel = grid.addRow(I18N.get("tracks.tooltip.tone_range"), "");
			setGraphic(grid);

			setOnShowing(event -> {
				bonusLabel.setText(track.getGoldenBonusPoints() + "");
				beatLabel.setText(
						"<" + track.getLowerXBound() + ", " + track.getUpperXBound() + ")");
				toneLabel.setText(
						"<" + track.getLowerYBound() + ", " + track.getUpperYBound() + ">");
			});
		}
	}

	private class TrackPropertyCheckboxTableCell extends CheckBoxTableCell<SongTrack> {
		private BiConsumer<SongTrack, Boolean> newValueConsumer;

		public TrackPropertyCheckboxTableCell(TableColumn<SongTrack, Boolean> column,
				BiConsumer<SongTrack, Boolean> newValueConsumer) {
			super(column);
			this.newValueConsumer = newValueConsumer;
		}

		@Override
		public void updateItem(Boolean item, boolean empty) {
			super.updateItem(item, empty);
			SongTrack track = getTrack();
			if (track != null) {
				disableProperty().bind(appContext.activeTrackProperty().isEqualTo(track));
			}
		}

		@Override
		public void commitEdit(Boolean newValue) {
			super.commitEdit(newValue);
			SongTrack track = getTrack();
			newValueConsumer.accept(track, newValue);
		}

		private SongTrack getTrack() {
			if (getTableRow() != null) {
				return (SongTrack) getTableRow().getItem();
			}
			return null;
		}
	}
}
