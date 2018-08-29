package main.java.com.github.nianna.karedi.controller;

import org.controlsfx.control.action.Action;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.ModifierValue;
import javafx.scene.input.KeyEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.context.AppContext;

public class MenuBarController implements Controller {
	@FXML
	private MenuItem editMenuItem;
	@FXML
	private MenuBar menuBar;
	@FXML
	private Menu selectionMenu;

	@FXML
	private MenuItem newSongMenuItem;
	@FXML
	private MenuItem openMenuItem;
	@FXML
	private MenuItem reloadMenuItem;
	@FXML
	private MenuItem saveMenuItem;
	@FXML
	private MenuItem saveAsMenuItem;
	@FXML
	private MenuItem importAudioMenuItem;
	@FXML
	private MenuItem exportAsSingleplayerMenuItem;
	@FXML
	private MenuItem exportAsDuetMenuItem;
	@FXML
	private MenuItem exitMenuItem;

	@FXML
	private Menu helpMenu;
	@FXML
	private MenuItem aboutMenuItem;

	@FXML
	private MenuItem redoMenuItem;
	@FXML
	private MenuItem undoMenuItem;
	@FXML
	private MenuItem cutMenuItem;
	@FXML
	private MenuItem copyMenuItem;
	@FXML
	private MenuItem pasteMenuItem;
	@FXML
	private MenuItem mergeTonesMenuItem;
	@FXML
	private MenuItem mergeSynchroMenuItem;
	@FXML
	private MenuItem mergeLyricsMenuItem;
	@FXML
	private MenuItem mergeTonesSynchroMenuItem;
	@FXML
	private MenuItem mergeSynchroLyricsMenuItem;
	@FXML
	private MenuItem mergeTonesLyricsMenuItem;
	@FXML
	private MenuItem mergeTonesSynchroLyricsMenuItem;

	@FXML
	private MenuItem markAsGoldenMenuItem;
	@FXML
	private MenuItem markAsFreestyleMenuItem;
	@FXML
	private MenuItem markAsRapMenuItem;
	@FXML
	private MenuItem addNoteMenuItem;
	@FXML
	private MenuItem addTrackMenuItem;
	@FXML
	private MenuItem deleteSelectedNotesMenuItem;
	@FXML
	private MenuItem deleteSelectedLyricsMenuItem;
	@FXML
	private MenuItem deleteSelectedNotesAndLyricsMenuItem;
	@FXML
	private MenuItem deleteTrackMenuItem;
	@FXML
	private MenuItem toggleLineBreakMenuItem;
	@FXML
	private MenuItem splitNoteMenuItem;
	@FXML
	private MenuItem joinNotesMenuItem;
	@FXML
	private MenuItem moveSelectionLeftMenuItem;
	@FXML
	private MenuItem moveSelectionRightMenuItem;
	@FXML
	private MenuItem moveSelectionDownMenuItem;
	@FXML
	private MenuItem moveSelectionUpMenuItem;
	@FXML
	private MenuItem startEarlierMenuItem;
	@FXML
	private MenuItem startLaterMenuItem;
	@FXML
	private MenuItem endEarlierMenuItem;
	@FXML
	private MenuItem endLaterMenuItem;
	@FXML
	private MenuItem showPreferencesMenuItem;

	@FXML
	private MenuItem rollLyricsLeftMenuItem;
	@FXML
	private MenuItem rollLyricsRightMenuItem;
	@FXML
	private MenuItem editLyricsMenuItem;
	@FXML
	private MenuItem insertMinusMenuItem;
	@FXML
	private MenuItem insertSpaceMenuItem;

	@FXML
	private MenuItem selectMoreMenuItem;
	@FXML
	private MenuItem selectLessMenuItem;
	@FXML
	private MenuItem selectAllMenuItem;
	@FXML
	private MenuItem selectVisibleMenuItem;
	@FXML
	private MenuItem selectNoneMenuItem;
	@FXML
	private MenuItem selectNextMenuItem;
	@FXML
	private MenuItem selectPreviousMenuItem;

	@FXML
	private MenuItem nextLineMenuItem;
	@FXML
	private MenuItem previousLineMenuItem;
	@FXML
	private MenuItem nextTrackMenuItem;
	@FXML
	private MenuItem previousTrackMenuItem;
	@FXML
	private MenuItem fitToSelectionMenuItem;
	@FXML
	private MenuItem fitToVisibleMenuItem;
	@FXML
	private MenuItem fitVerticallyMenuItem;
	@FXML
	private MenuItem fitHorizontallyMenuItem;
	@FXML
	private MenuItem viewMedleyMenuItem;
	@FXML
	private MenuItem togglePianoMenuItem;
	@FXML
	private MenuItem switchModeMenuItem;

	@FXML
	private MenuItem playSelectionAudioMenuItem;
	@FXML
	private MenuItem playSelectionMidiMenuItem;
	@FXML
	private MenuItem playSelectionAudioMidiMenuItem;
	@FXML
	private MenuItem playVisibleAudioMenuItem;
	@FXML
	private MenuItem playVisibleMidiMenuItem;
	@FXML
	private MenuItem playVisibleAudioMidiMenuItem;
	@FXML
	private MenuItem playAllAudioMenuItem;
	@FXML
	private MenuItem playAllMidiMenuItem;
	@FXML
	private MenuItem playAllAudioMidiMenuItem;
	@FXML
	private MenuItem playToTheEndAudioMenuItem;
	@FXML
	private MenuItem playToTheEndMidiMenuItem;
	@FXML
	private MenuItem playToTheEndAudioMidiMenuItem;
	@FXML
	private MenuItem playMedleyAudioMenuItem;
	@FXML
	private MenuItem playMedleyAudioMidiMenuItem;
	@FXML
	private MenuItem playMedleyMidiMenuItem;
	@FXML
	private MenuItem playBeforeMenuItem;
	@FXML
	private MenuItem playAfterMenuItem;
	@FXML
	private MenuItem stopPlaybackMenuItem;
	@FXML
	private CheckMenuItem toggleTicksMenuItem;
	@FXML
	private CheckMenuItem toggleMidiMenuItem;

	@FXML
	private MenuItem multiplyBpmByTwoMenuItem;
	@FXML
	private MenuItem divideBpmByTwoMenuItem;
	@FXML
	private MenuItem editBpmMenuItem;
	@FXML
	private MenuItem medleyFromSelectionMenuItem;
	@FXML
	private MenuItem editMedleyMenuItem;
	@FXML
	private MenuItem setMedleyStartMenuItem;
	@FXML
	private MenuItem setMedleyEndMenuItem;
	@FXML
	private MenuItem renameMenuItem;
	@FXML
	private MenuItem addTagMenuItem;
	@FXML
	private MenuItem setGapTagValueMenuItem;
	@FXML
	private MenuItem setStartTagValueMenuItem;
	@FXML
	private MenuItem setEndTagValueMenuItem;

	@FXML
	private MenuItem tapNotesMenuItem;
	@FXML
	private MenuItem writeTonesMenuItem;

	@FXML
	private MenuItem resetPianoMenuItem;

	private AppContext appContext;

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;

		bindFileMenu();
		bindEditMenu();
		bindLyricsMenu();
		bindPlayMenu();
		bindSelectMenu();
		bindTagsMenu();
		bindViewMenu();
		bindExtrasMenu();
		bindHelpMenu();

		menuBar.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.isAltDown() && (event.isShiftDown() || event.isShortcutDown())) {
				if (!menuBar.getScene().getAccelerators().keySet().stream()
						.anyMatch(accelerator -> accelerator.match(event))) {
					event.consume();
				}
			}
		});
	}

	private void bindFileMenu() {
		bind(newSongMenuItem, KarediActions.NEW);
		bind(openMenuItem, KarediActions.LOAD);
		bind(reloadMenuItem, KarediActions.RELOAD);
		bind(saveMenuItem, KarediActions.SAVE);
		bind(saveAsMenuItem, KarediActions.SAVE_AS);
		bind(importAudioMenuItem, KarediActions.IMPORT_AUDIO);
		bind(exportAsSingleplayerMenuItem, KarediActions.EXPORT_AS_SINGLEPLAYER);
		bind(exportAsDuetMenuItem, KarediActions.EXPORT_AS_DUET);
		bind(exitMenuItem, KarediActions.EXIT);
	}

	private void bindEditMenu() {
		bind(cutMenuItem, KarediActions.CUT);
		bind(copyMenuItem, KarediActions.COPY);
		bind(pasteMenuItem, KarediActions.PASTE);
		bind(mergeTonesMenuItem, KarediActions.SET_TONES);
		bind(mergeSynchroMenuItem, KarediActions.SET_SYNCHRO);
		bind(mergeLyricsMenuItem, KarediActions.SET_LYRICS);
		bind(mergeTonesSynchroMenuItem, KarediActions.SET_TONES_AND_SYNCHRO);
		bind(mergeTonesLyricsMenuItem, KarediActions.SET_TONES_AND_LYRICS);
		bind(mergeSynchroLyricsMenuItem, KarediActions.SET_SYNCHRO_AND_LYRICS);
		bind(mergeTonesSynchroLyricsMenuItem, KarediActions.SET_TONES_SYNCHRO_AND_LYRICS);

		bind(addNoteMenuItem, KarediActions.ADD_NOTE);
		bind(addTrackMenuItem, KarediActions.ADD_TRACK);
		bind(deleteSelectedLyricsMenuItem, KarediActions.DELETE_LYRICS);
		bind(deleteSelectedNotesMenuItem, KarediActions.DELETE_SELECTION);
		bind(deleteSelectedNotesAndLyricsMenuItem, KarediActions.DELETE_SELECTION_HARD);
		bind(deleteTrackMenuItem, KarediActions.DELETE_TRACK);

		bind(moveSelectionDownMenuItem, KarediActions.MOVE_SELECTION_DOWN);
		bind(moveSelectionUpMenuItem, KarediActions.MOVE_SELECTION_UP);
		bind(moveSelectionLeftMenuItem, KarediActions.MOVE_SELECTION_LEFT);
		bind(moveSelectionRightMenuItem, KarediActions.MOVE_SELECTION_RIGHT);
		bind(startEarlierMenuItem, KarediActions.LENGTHEN_LEFT_SIDE);
		bind(startLaterMenuItem, KarediActions.SHORTEN_LEFT_SIDE);
		bind(endEarlierMenuItem, KarediActions.SHORTEN_RIGHT_SIDE);
		bind(endLaterMenuItem, KarediActions.LENGTHEN_RIGHT_SIDE);

		bind(markAsFreestyleMenuItem, KarediActions.MARK_AS_FREESTYLE);
		bind(markAsGoldenMenuItem, KarediActions.MARK_AS_GOLDEN);
		bind(markAsRapMenuItem, KarediActions.MARK_AS_RAP);
		bind(toggleLineBreakMenuItem, KarediActions.TOGGLE_LINEBREAK);
		bind(splitNoteMenuItem, KarediActions.SPLIT_SELECTION);
		bind(joinNotesMenuItem, KarediActions.JOIN_SELECTION);

		bind(undoMenuItem, KarediActions.UNDO);
		bind(redoMenuItem, KarediActions.REDO);
		
		bind(showPreferencesMenuItem, KarediActions.SHOW_PREFERENCES);
	}

	private void bindLyricsMenu() {
		bind(editLyricsMenuItem, KarediActions.EDIT_LYRICS);
		bind(rollLyricsLeftMenuItem, KarediActions.ROLL_LYRICS_LEFT);
		bind(rollLyricsRightMenuItem, KarediActions.ROLL_LYRICS_RIGHT);
		bind(insertMinusMenuItem, KarediActions.INSERT_MINUS);
		bind(insertSpaceMenuItem, KarediActions.INSERT_SPACE);
	}

	private void bindPlayMenu() {
		bind(playMedleyAudioMenuItem, KarediActions.PLAY_MEDLEY_AUDIO);
		bind(playMedleyAudioMidiMenuItem, KarediActions.PLAY_MEDLEY_AUDIO_MIDI);
		bind(playMedleyMidiMenuItem, KarediActions.PLAY_MEDLEY_MIDI);

		bind(playSelectionAudioMenuItem, KarediActions.PLAY_SELECTION_AUDIO);
		bind(playSelectionMidiMenuItem, KarediActions.PLAY_SELECTION_MIDI);
		bind(playSelectionAudioMidiMenuItem, KarediActions.PLAY_SELECTION_AUDIO_MIDI);

		bind(playVisibleAudioMenuItem, KarediActions.PLAY_VISIBLE_AUDIO);
		bind(playVisibleMidiMenuItem, KarediActions.PLAY_VISIBLE_MIDI);
		bind(playVisibleAudioMidiMenuItem, KarediActions.PLAY_VISIBLE_AUDIO_MIDI);

		bind(playAllAudioMenuItem, KarediActions.PLAY_ALL_AUDIO);
		bind(playAllMidiMenuItem, KarediActions.PLAY_ALL_MIDI);
		bind(playAllAudioMidiMenuItem, KarediActions.PLAY_ALL_AUDIO_MIDI);

		bind(playToTheEndAudioMenuItem, KarediActions.PLAY_TO_THE_END_AUDIO);
		bind(playToTheEndMidiMenuItem, KarediActions.PLAY_TO_THE_END_MIDI);
		bind(playToTheEndAudioMidiMenuItem, KarediActions.PLAY_TO_THE_END_AUDIO_MIDI);

		bind(playBeforeMenuItem, KarediActions.PLAY_BEFORE_SELECTION);
		bind(playAfterMenuItem, KarediActions.PLAY_AFTER_SELECTION);

		bind(stopPlaybackMenuItem, KarediActions.STOP_PLAYBACK);
		bind(toggleTicksMenuItem, KarediActions.TOGGLE_TICKS);
		bind(toggleMidiMenuItem, KarediActions.TOGGLE_MIDI);
	}

	private void bindSelectMenu() {
		bind(selectNextMenuItem, KarediActions.SELECT_NEXT);
		bind(selectPreviousMenuItem, KarediActions.SELECT_PREVIOUS);
		bind(selectMoreMenuItem, KarediActions.INCREASE_SELECTION);
		bind(selectLessMenuItem, KarediActions.DECREASE_SELECTION);
		bind(selectNoneMenuItem, KarediActions.CLEAR_SELECTION);
		bind(selectAllMenuItem, KarediActions.SELECT_ALL);
		bind(selectVisibleMenuItem, KarediActions.SELECT_VISIBLE);
	}

	private void bindTagsMenu() {
		bind(multiplyBpmByTwoMenuItem, KarediActions.MULTIPLY_BPM_BY_TWO);
		bind(divideBpmByTwoMenuItem, KarediActions.DIVIDE_BPM_BY_TWO);
		bind(editBpmMenuItem, KarediActions.EDIT_BPM);

		bind(editMedleyMenuItem, KarediActions.EDIT_MEDLEY);
		bind(medleyFromSelectionMenuItem, KarediActions.MEDLEY_FROM_SELECTION);
		bind(setMedleyEndMenuItem, KarediActions.MEDLEY_SET_END);
		bind(setMedleyStartMenuItem, KarediActions.MEDLEY_SET_START);

		bind(setGapTagValueMenuItem, KarediActions.SET_GAP_TAG);
		bind(setStartTagValueMenuItem, KarediActions.SET_START_TAG);
		bind(setEndTagValueMenuItem, KarediActions.SET_END_TAG);

		bind(renameMenuItem, KarediActions.RENAME);
		bind(addTagMenuItem, KarediActions.ADD_TAG);
	}

	private void bindViewMenu() {
		bind(nextLineMenuItem, KarediActions.VIEW_NEXT_LINE);
		bind(previousLineMenuItem, KarediActions.VIEW_PREVIOUS_LINE);
		bind(nextTrackMenuItem, KarediActions.VIEW_NEXT_TRACK);
		bind(previousTrackMenuItem, KarediActions.VIEW_PREVIOUS_TRACK);

		bind(togglePianoMenuItem, KarediActions.TOGGLE_PIANO);
		bind(switchModeMenuItem, KarediActions.SWITCH_MODE);

		bind(fitToSelectionMenuItem, KarediActions.FIT_TO_SELECTION);
		bind(fitToVisibleMenuItem, KarediActions.FIT_TO_VISIBLE);
		bind(fitHorizontallyMenuItem, KarediActions.FIT_HORIZONTALLY);
		bind(fitVerticallyMenuItem, KarediActions.FIT_VERTICALLY);

		bind(viewMedleyMenuItem, KarediActions.VIEW_MEDLEY);
	}

	private void bindExtrasMenu() {
		bind(tapNotesMenuItem, KarediActions.TAP_NOTES);
		bind(writeTonesMenuItem, KarediActions.WRITE_TONES);
	}

	private void bindHelpMenu() {
		bind(resetPianoMenuItem, KarediActions.RESET_SEQUENCER);
	}

	private void bind(MenuItem menuItem, KarediActions actionKey) {
		Action action = appContext.getAction(actionKey);
		action.acceleratorProperty().bind(menuItem.acceleratorProperty());
		menuItem.disableProperty().bind(action.disabledProperty());
		menuItem.setOnAction(action::handle);

		KeyCombination accelerator = menuItem.getAccelerator();
		if (accelerator != null && accelerator.getAlt() == ModifierValue.DOWN) {
			menuBar.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				if (menuItem.getAccelerator().match(event) && action.isDisabled()) {
					event.consume();
				}
			});
		}
	}

	private void bind(CheckMenuItem menuItem, KarediActions actionKey) {
		Action action = appContext.getAction(actionKey);
		action.acceleratorProperty().bind(menuItem.acceleratorProperty());
		menuItem.disableProperty().bind(action.disabledProperty());
		menuItem.setOnAction(action::handle);
		menuItem.setSelected(action.isSelected());
		menuItem.selectedProperty().addListener(inv -> action.setSelected(menuItem.isSelected()));
	}

	@Override
	public Node getContent() {
		return menuBar;
	}

}