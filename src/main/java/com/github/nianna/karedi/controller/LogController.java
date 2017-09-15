package main.java.com.github.nianna.karedi.controller;

import java.text.SimpleDateFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.event.StateEvent;
import main.java.com.github.nianna.karedi.event.StateEvent.State;

public class LogController implements Controller {

	private static final FontAwesome GLYPH_FONT = new FontAwesome();
	private static final FontAwesome.Glyph ERROR_GLYPH = FontAwesome.Glyph.TIMES;
	private static final FontAwesome.Glyph WARNING_GLYPH = FontAwesome.Glyph.EXCLAMATION;
	private static final FontAwesome.Glyph INFO_GLYPH = FontAwesome.Glyph.CHECK;
	private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

	@FXML
	private AnchorPane pane;
	@FXML
	private ListView<LogRecord> list;

	@FXML
	public void initialize() {
		list.setCellFactory(param -> new LogListCell());
	}

	@FXML
	private void handleClear() {
		list.getItems().clear();
	}

	@Override
	public void setAppContext(AppContext appContext) {
		appContext.getMainLogger().addHandler(new Handler() {

			@Override
			public void publish(LogRecord record) {
				list.getItems().add(record);
				Platform.runLater(() -> list.scrollTo(record));
				list.fireEvent(new StateEvent(State.NEEDS_ATTENTION));
			}

			@Override
			public void flush() {
				list.getItems().clear();
			}

			@Override
			public void close() throws SecurityException {
				list.setDisable(true);
			}
		});

		list.setDisable(false);
	}

	@Override
	public Node getContent() {
		return pane;
	}

	private static Glyph createGlyph(FontAwesome.Glyph glyph, Color color) {
		return GLYPH_FONT.create(glyph).size(18).color(color).useGradientEffect();
	}

	private static Glyph glyphForLevel(Level level) {
		if (level == Level.SEVERE) {
			return createGlyph(ERROR_GLYPH, Color.RED);
		}
		if (level == Level.WARNING) {
			return createGlyph(WARNING_GLYPH, Color.YELLOW);
		}
		return createGlyph(INFO_GLYPH, Color.GREEN);
	}

	private class LogListCell extends ListCell<LogRecord> {
		private GridPane grid;
		private Glyph glyph;
		private Label date;
		private Label msg;

		public LogListCell() {
			grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(4);
			grid.setPadding(new Insets(0, 10, 0, 10));

			date = new Label();
			date.getStyleClass().add("log-list-date");
			msg = new Label();
			msg.getStyleClass().add("log-list-msg");

			grid.add(date, 1, 1);
			grid.add(msg, 1, 0);

			ColumnConstraints col1 = new ColumnConstraints();
			col1.setHalignment(HPos.CENTER);
			col1.setPrefWidth(20);
			grid.getColumnConstraints().addAll(col1);
		}

		@Override
		protected void updateItem(LogRecord item, boolean empty) {
			super.updateItem(item, empty);
			setText(null);
			if (empty || item == null) {
				setGraphic(null);
			} else {
				grid.getChildren().remove(glyph);
				glyph = glyphForLevel(item.getLevel());
				grid.add(glyph, 0, 0, 1, 2);
				date.setText(SDF.format(item.getMillis()));
				msg.setText(item.getMessage());
				setGraphic(grid);
			}
		}
	}

}
