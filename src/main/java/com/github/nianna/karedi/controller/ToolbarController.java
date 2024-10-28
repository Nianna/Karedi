package com.github.nianna.karedi.controller;

import com.github.nianna.karedi.KarediApp.ViewMode;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.context.ActionContext;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.event.ControllerEvent;
import com.github.nianna.karedi.util.PercentageStringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToolBar;
import org.controlsfx.control.action.Action;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

public class ToolbarController implements Controller {
	@FXML
	private ToolBar toolBar;
	@FXML
	private Button saveButton;
	@FXML
	private Button openButton;
	@FXML
	private Button newButton;

	@FXML
	private Button undoButton;
	@FXML
	private Button redoButton;

	@FXML
	private Button cutButton;
	@FXML
	private Button copyButton;
	@FXML
	private Button pasteButton;

	@FXML
	private Button addNoteButton;
	@FXML
	private Button deleteNotesButton;
	@FXML
	private Button joinNotesButton;
	@FXML
	private Button splitNoteButton;

	@FXML
	private Button rollLeftButton;
	@FXML
	private Button rollRightButton;

	@FXML
	private Button markAsGoldenButton;
	@FXML
	private Button markAsFreestyleButton;
	@FXML
	private Button markAsRapButton;

	@FXML
	private Button switchModeButton;
	@FXML
	private Glyph switchModeGlyph;

	@FXML
	private Glyph playbackSpeedGlyph;
	@FXML
	private Spinner<Integer> playbackSpeedSpinner;

	private AppContext appContext;

	private ActionContext actionContext;

	// Must be saved as a field to avoid premature garbage collection
	private ObjectProperty<Integer> playbackSpeedPropertyWrapper;

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
		this.actionContext = appContext.getActionContext();

		bind(newButton, KarediActions.NEW);
		bind(openButton, KarediActions.LOAD);
		bind(saveButton, KarediActions.SAVE);

		bind(undoButton, KarediActions.UNDO);
		bind(redoButton, KarediActions.REDO);

		bind(cutButton, KarediActions.CUT);
		bind(copyButton, KarediActions.COPY);
		bind(pasteButton, KarediActions.PASTE);

		bind(addNoteButton, KarediActions.ADD_NOTE);
		bind(deleteNotesButton, KarediActions.DELETE_SELECTION);
		bind(joinNotesButton, KarediActions.JOIN_SELECTION);
		bind(splitNoteButton, KarediActions.SPLIT_SELECTION);

		bind(rollLeftButton, KarediActions.ROLL_LYRICS_LEFT);
		bind(rollRightButton, KarediActions.ROLL_LYRICS_RIGHT);

		bind(markAsGoldenButton, KarediActions.MARK_AS_GOLDEN);
		bind(markAsFreestyleButton, KarediActions.MARK_AS_FREESTYLE);
		bind(markAsRapButton, KarediActions.MARK_AS_RAP);

		bind(switchModeButton, KarediActions.SWITCH_MODE);

		switchModeGlyph.setIcon(iconForViewMode(appContext.getActiveViewMode()));
		appContext.activeViewModeProperty().addListener(inv -> {
			switchModeGlyph.setIcon(iconForViewMode(appContext.getActiveViewMode()));
		});

		configurePlaybackSpeedController();
	}

	private void configurePlaybackSpeedController() {
		playbackSpeedSpinner.getValueFactory().setConverter(new PercentageStringConverter());
		playbackSpeedPropertyWrapper = appContext.getAudioContext().playbackSpeedProperty().asObject();
		playbackSpeedSpinner.getValueFactory().valueProperty().bindBidirectional(playbackSpeedPropertyWrapper);
		playbackSpeedSpinner.setOnScroll(event -> {
			if (event.getDeltaY() > 0) {
				playbackSpeedSpinner.increment();
			} else {
				playbackSpeedSpinner.decrement();
			}
		});
		playbackSpeedSpinner.setOnMouseClicked(
				event -> toolBar.fireEvent(new ControllerEvent(ControllerEvent.FOCUS_EDITOR))
		);
		playbackSpeedGlyph.setOnMouseClicked(event -> playbackSpeedSpinner.getValueFactory().setValue(100));
		playbackSpeedSpinner.disableProperty().bind(appContext.getActiveSongContext().activeSongIsNullBinding());
		playbackSpeedGlyph.disableProperty().bind(playbackSpeedSpinner.disabledProperty());
	}

	private void bind(Button button, KarediActions actionKey) {
		Action action = bindDisable(button, actionKey);
		button.setOnAction(action);
	}

	private Action bindDisable(Control control, KarediActions actionKey) {
		Action action = actionContext.getAction(actionKey);
		control.disableProperty().bind(action.disabledProperty());
		return action;
	}

	private FontAwesome.Glyph iconForViewMode(ViewMode mode) {
		if (appContext.getActiveViewMode() == ViewMode.NIGHT) {
			return FontAwesome.Glyph.SUN_ALT;
		} else {
			return FontAwesome.Glyph.MOON_ALT;
		}
	}

	@Override
	public Node getContent() {
		return toolBar;
	}
}
