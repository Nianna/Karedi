package main.java.com.github.nianna.karedi.context;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.KarediApp.ViewMode;
import main.java.com.github.nianna.karedi.Settings;
import main.java.com.github.nianna.karedi.action.ActionMap;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.audio.AudioFileLoader;
import main.java.com.github.nianna.karedi.audio.CachedAudioFile;
import main.java.com.github.nianna.karedi.audio.MidiPlayer;
import main.java.com.github.nianna.karedi.audio.Player.Mode;
import main.java.com.github.nianna.karedi.audio.Player.Status;
import main.java.com.github.nianna.karedi.command.AddNoteCommand;
import main.java.com.github.nianna.karedi.command.BackupStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.ChangePreStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandComposite;
import main.java.com.github.nianna.karedi.command.DeleteNotesCommand;
import main.java.com.github.nianna.karedi.command.DeleteTextCommand;
import main.java.com.github.nianna.karedi.command.JoinNotesCommand;
import main.java.com.github.nianna.karedi.command.MarkAsTypeCommand;
import main.java.com.github.nianna.karedi.command.MergeNotesCommand;
import main.java.com.github.nianna.karedi.command.MergeNotesCommand.MergeMode;
import main.java.com.github.nianna.karedi.command.MoveCollectionCommand;
import main.java.com.github.nianna.karedi.command.PasteCommand;
import main.java.com.github.nianna.karedi.command.ResizeNotesCommand;
import main.java.com.github.nianna.karedi.command.RollLyricsLeftCommand;
import main.java.com.github.nianna.karedi.command.RollLyricsRightCommand;
import main.java.com.github.nianna.karedi.command.SplitNoteCommand;
import main.java.com.github.nianna.karedi.command.ToggleLineBreakCommand;
import main.java.com.github.nianna.karedi.command.tag.ChangeBpmCommand;
import main.java.com.github.nianna.karedi.command.tag.ChangeMedleyCommand;
import main.java.com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import main.java.com.github.nianna.karedi.command.tag.RescaleSongToBpmCommand;
import main.java.com.github.nianna.karedi.command.track.AddTrackCommand;
import main.java.com.github.nianna.karedi.command.track.DeleteTrackCommand;
import main.java.com.github.nianna.karedi.dialog.ChooseTracksDialog;
import main.java.com.github.nianna.karedi.dialog.EditBpmDialog;
import main.java.com.github.nianna.karedi.dialog.EditFilenamesDialog;
import main.java.com.github.nianna.karedi.dialog.EditFilenamesDialog.FilenamesEditResult;
import main.java.com.github.nianna.karedi.dialog.EditMedleyDialog;
import main.java.com.github.nianna.karedi.dialog.EditTagDialog;
import main.java.com.github.nianna.karedi.dialog.ExportWithErrorsAlert;
import main.java.com.github.nianna.karedi.dialog.ModifyBpmDialog;
import main.java.com.github.nianna.karedi.dialog.ModifyBpmDialog.BpmEditResult;
import main.java.com.github.nianna.karedi.dialog.OverwriteAlert;
import main.java.com.github.nianna.karedi.dialog.PreferencesDialog;
import main.java.com.github.nianna.karedi.parser.BaseParser;
import main.java.com.github.nianna.karedi.parser.BaseUnparser;
import main.java.com.github.nianna.karedi.parser.Parser;
import main.java.com.github.nianna.karedi.parser.Unparser;
import main.java.com.github.nianna.karedi.parser.element.InvalidSongElementException;
import main.java.com.github.nianna.karedi.parser.element.LineBreakElement;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Note.Type;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.Song.Medley;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.song.tag.Tag;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import main.java.com.github.nianna.karedi.util.BindingsUtils;
import main.java.com.github.nianna.karedi.util.Converter;
import main.java.com.github.nianna.karedi.util.ForbiddenCharacterRegex;
import main.java.com.github.nianna.karedi.util.ListenersUtils;
import main.java.com.github.nianna.karedi.util.MathUtils;

public class AppContext {
	private static final Logger LOGGER = Logger.getLogger(KarediApp.class.getPackage().getName());
	private static final int NEW_NOTE_DEFAULT_LENGTH = 3;
	private static final int MAX_HISTORY_SIZE = 1000;

	private final ReadOnlyObjectWrapper<Song> activeSong = new ReadOnlyObjectWrapper<>();
	private final ReadOnlyObjectWrapper<SongTrack> activeTrack = new ReadOnlyObjectWrapper<>();
	private final ReadOnlyObjectWrapper<SongLine> activeLine = new ReadOnlyObjectWrapper<>();
	private final ReadOnlyObjectWrapper<File> activeFile = new ReadOnlyObjectWrapper<>();
	private final ReadOnlyObjectWrapper<ViewMode> activeViewMode = new ReadOnlyObjectWrapper<>(
			KarediApp.getInstance().getViewMode());
	private final ObjectProperty<Command> lastSavedCommand = new SimpleObjectProperty<>();

	private final Parser parser = new BaseParser();
	private final Unparser unparser = new BaseUnparser();
	private final SongLoader songLoader = new SongLoader(parser, new BasicSongBuilder());
	private final SongDisassembler songDisassembler = new SongDisassembler();
	private final SongSaver songSaver = new SongSaver(unparser, songDisassembler);
	private final ActionHelper actionHelper = new ActionHelper();

	private final History history = new History();
	private final NoteSelection selection = new NoteSelection();
	private final BeatMillisConverter beatMillisConverter = new BeatMillisConverter(
			Song.DEFAULT_GAP, Song.DEFAULT_BPM);
	private final SongPlayer player = new SongPlayer(beatMillisConverter);
	private final BeatRange beatRange = new BeatRange(beatMillisConverter);
	private final VisibleArea visibleArea = new VisibleArea(beatRange);

	private final ObservableList<Note> observableSelection = FXCollections
			.observableArrayList(note -> new Observable[] { note });
	private final IntBounded selectionBounds = new BoundingBox<>(observableSelection);
	private File directory;

	private final ListChangeListener<? super Note> noteListChangeListener = ListenersUtils
			.createListContentChangeListener(ListenersUtils::pass, this::onNoteRemoved);
	private final ListChangeListener<? super SongLine> lineListChangeListener = ListenersUtils
			.createListContentChangeListener(ListenersUtils::pass, this::onLineRemoved);
	private final InvalidationListener markerPositionChangeListener = this::onMarkerPositionWhilePlayingChanged;
	private final InvalidationListener beatMillisConverterInvalidationListener = obs -> onBeatMillisConverterInvalidated();
	private final InvalidationListener boundsListener = obs -> onBoundsInvalidated();

	// Convenience bindings for actions
	private final BooleanBinding selectionIsEmpty = selection.sizeProperty().isEqualTo(0);
	private final BooleanBinding activeSongIsNull = activeSongProperty().isNull();
	private final BooleanBinding activeTrackIsNull = activeTrackProperty().isNull();
	private final BooleanBinding activeFileIsNull = activeFileProperty().isNull();
	private final BooleanBinding activeAudioIsNull = player.activeAudioFileProperty().isNull();
	private final IntegerProperty activeSongTrackCount = new SimpleIntegerProperty();
	private final BooleanBinding activeSongHasOneOrZeroTracks = activeSongTrackCount
			.lessThanOrEqualTo(1);

	public AppContext() {
		LOGGER.setUseParentHandlers(false);
		history.setMaxSize(MAX_HISTORY_SIZE);
		actionHelper.addActions();

		Bindings.bindContent(observableSelection, getSelected());
		player.statusProperty().addListener(this::onPlayerStatusChanged);
		selectionBounds.addListener(obs -> onSelectionBoundsInvalidated());
	}

	// Actions
	public void addAction(KarediActions key, KarediAction action) {
		actionHelper.add(key, action);
	}

	public KarediAction getAction(KarediActions key) {
		return actionHelper.get(key);
	}

	public void execute(KarediActions action) {
		actionHelper.execute(action);
	}

	public boolean canExecute(KarediActions action) {
		return actionHelper.canExecute(action);
	}

	// Beat range
	public Integer getMinBeat() {
		return beatRange.getMinBeat();
	}

	public ReadOnlyIntegerProperty minBeatProperty() {
		return beatRange.minBeatProperty();
	}

	public Integer getMaxBeat() {
		return beatRange.getMaxBeat();
	}

	public ReadOnlyIntegerProperty maxBeatProperty() {
		return beatRange.maxBeatProperty();
	}

	// Visible area
	public void invalidateVisibleArea() {
		visibleArea.invalidate();
	}

	public void assertAllNeededTonesVisible() {
		assertAllNeededTonesVisible(visibleArea.getLowerXBound(), visibleArea.getUpperXBound());
	}

	public void assertAllNeededTonesVisible(int fromBeat, int toBeat) {
		List<Note> notes = getSong().getVisibleNotes(fromBeat, toBeat);
		visibleArea.assertBoundsYVisible(addMargins(new BoundingBox<>(notes)));
	}

	public void setVisibleAreaXBounds(int lowerXBound, int upperXBound) {
		setVisibleAreaXBounds(lowerXBound, upperXBound, true);
	}

	private void setVisibleAreaXBounds(int lowerXBound, int upperXBound, boolean setLineToNull) {
		if (visibleArea.setXBounds(lowerXBound, upperXBound) && setLineToNull) {
			setActiveLine(null);
		}
	}

	public void setVisibleAreaYBounds(int lowerBound, int upperBound) {
		if (visibleArea.setYBounds(lowerBound, upperBound)) {
			setActiveLine(null);
		}
	}

	public void increaseVisibleAreaXBounds(int by) {
		if (visibleArea.increaseXBounds(by)) {
			setActiveLine(null);
		}
	}

	public void increaseVisibleAreaYBounds(int by) {
		if (visibleArea.increaseYBounds(by)) {
			setActiveLine(null);
		}
	}

	public IntBounded addMargins(IntBounded bounds) {
		return visibleArea.addMargins(bounds);
	}

	public IntBounded getVisibleAreaBounds() {
		return visibleArea;
	}

	public void moveVisibleArea(Direction direction, int by) {
		visibleArea.move(direction, by);
		setActiveLine(null);
	}

	private boolean isInVisibleBeatRange(Note note) {
		return MathUtils.inRange(note.getStart(), visibleArea.getLowerXBound(),
				visibleArea.getUpperXBound());
	}

	// History
	public boolean execute(Command command) {
		return history.push(new BackupStateCommandDecorator(command, this));
	}

	public ObservableList<Command> getHistory() {
		return history.getList();
	}

	public void clearHistory() {
		history.clear();
	}

	public ReadOnlyObjectProperty<Command> activeCommandProperty() {
		return history.activeCommandProperty();
	}

	public ReadOnlyIntegerProperty activeCommandIndexProperty() {
		return history.activeIndexProperty();
	}

	public Integer getActiveCommandIndex() {
		return history.getActiveIndex();
	}

	public Command getActiveCommand() {
		return history.getActiveCommand();
	}

	// Selection
	public NoteSelection getSelection() {
		return selection;
	}

	public ObservableList<Note> getSelected() {
		return selection.get();
	}

	// Audio
	public CachedAudioFile getActiveAudioFile() {
		return player.getActiveAudioFile();
	}

	public void removeAudioFile(CachedAudioFile file) {
		player.removeAudioFile(file);
	}

	private void loadAudioFile(File file) {
		AudioFileLoader.loadMp3File(file, (newAudio -> {
			if (newAudio.isPresent()) {
				player.addAudioFile(newAudio.get());
				setActiveAudioFile(newAudio.get());
				LOGGER.info(I18N.get("import.audio.success"));
			} else {
				LOGGER.severe(I18N.get("import.audio.fail"));
			}
		}));
	}

	public void setActiveAudioFile(CachedAudioFile file) {
		if (file != getActiveAudioFile()) {
			execute(KarediActions.STOP_PLAYBACK);
			player.setActiveAudioFile(file);
			if (file != null) {
				beatRange.setMaxTime(file.getDuration());
			} else {
				beatRange.setMaxTime(null);
			}
		}
	}

	public ReadOnlyObjectProperty<CachedAudioFile> activeAudioFileProperty() {
		return player.activeAudioFileProperty();
	}

	public ObservableList<CachedAudioFile> getAudioFiles() {
		return player.getAudioFiles();
	}

	// Player
	public ReadOnlyObjectProperty<Status> playerStatusProperty() {
		return player.statusProperty();
	}

	public Status getPlayerStatus() {
		return player.getStatus();
	}

	public boolean isTickingEnabled() {
		return player.isTickingEnabled();
	}

	private void playRange(int fromBeat, int toBeat, Mode mode) {
		assertAllNeededTonesVisible(fromBeat, toBeat);
		player.play(fromBeat, toBeat, mode);
	}

	private void play(long startMillis, long endMillis, List<Note> notes, Mode mode) {
		if (!isMarkerVisible()) {
			setMarkerBeat(visibleArea.getLowerXBound());
		}
		player.play(startMillis, endMillis, notes, mode);
	}

	// Marker
	public ReadOnlyIntegerProperty markerBeatProperty() {
		return player.markerBeatProperty();
	}

	public int getMarkerBeat() {
		return player.getMarkerBeat();
	}

	public void setMarkerBeat(int beat) {
		player.setMarkerBeat(beat);
	}

	public ReadOnlyLongProperty markerTimeProperty() {
		return player.markerTimeProperty();
	}

	public Long getMarkerTime() {
		return player.getMarkerTime();
	}

	public void setMarkerTime(long time) {
		player.setMarkerTime(time);
	}

	private boolean isMarkerVisible() {
		return MathUtils.inRange(getMarkerTime(), beatToMillis(visibleArea.getLowerXBound()),
				beatToMillis(visibleArea.getUpperXBound()));
	}

	// Beat <-> millis convertion
	public long beatToMillis(int beat) {
		return beatMillisConverter.beatToMillis(beat);
	}

	public int millisToBeat(long millis) {
		return beatMillisConverter.millisToBeat(millis);
	}

	public BeatMillisConverter getBeatMillisConverter() {
		return beatMillisConverter;
	}

	// Files
	public ReadOnlyObjectProperty<File> activeFileProperty() {
		return activeFile.getReadOnlyProperty();
	}

	public File getActiveFile() {
		return activeFileProperty().get();
	}

	private void setActiveFile(File file) {
		this.activeFile.set(file);
		KarediApp.getInstance().updatePrimaryStageTitle(file);
		directory = file == null ? null : file.getParentFile();
	}

	public File getDirectory() {
		return directory;
	}

	public void loadSongFile(File file) {
		loadSongFile(file, true);
	}

	private void loadSongFile(File file, boolean resetPlayer) {
		if (file != null) {
			reset(resetPlayer);
			setActiveFile(file);
			Song song = songLoader.load(file);
			song.getTagValue(TagKey.MP3).ifPresent(audioFileName -> {
				loadAudioFile(new File(file.getParent(), audioFileName));
			});
			setSong(song);
			LOGGER.info(I18N.get("load.success"));
		}
	}

	private boolean saveSongToFile(File file) {
		if (file != null) {
			if (songSaver.saveSongToFile(file, getSong())) {
				lastSavedCommand.set(history.getActiveCommand());
				return true;
			}
		}
		return false;
	}

	// Getters and setters for properties
	public ReadOnlyObjectProperty<Song> activeSongProperty() {
		return activeSong.getReadOnlyProperty();
	}

	public final Song getSong() {
		return activeSongProperty().get();
	}

	public final void setSong(Song song) {
		Song oldSong = getSong();
		new SongNormalizer(song).normalize();
		// The song has at least one track now
		if (song != oldSong) {
			activeSong.set(song);
			player.setSong(song);
			onBeatMillisConverterInvalidated();
			if (oldSong != null) {
				oldSong.getBeatMillisConverter()
						.removeListener(beatMillisConverterInvalidationListener);
				activeSongTrackCount.unbind();
			}
			if (song == null) {
				setActiveTrack(null);
				activeSongTrackCount.set(0);
			} else {
				song.getBeatMillisConverter().addListener(beatMillisConverterInvalidationListener);
				activeSongTrackCount.bind(song.trackCount());
				setActiveTrack(song.getDefaultTrack().orElse(null));
			}
			beatRange.setBounds(song);
		}
	}

	public ReadOnlyObjectProperty<SongTrack> activeTrackProperty() {
		return activeTrack.getReadOnlyProperty();
	}

	public final SongTrack getActiveTrack() {
		return activeTrack.get();
	}

	public final void setActiveTrack(SongTrack track) {
		SongTrack oldTrack = getActiveTrack();
		if (track != oldTrack) {
			selection.clear();
			activeTrack.set(track);
			setActiveLine(null);
			if (oldTrack != null) {
				oldTrack.removeLineListListener(lineListChangeListener);
				oldTrack.removeNoteListListener(noteListChangeListener);
			}
			if (track != null) {
				track.addLineListListener(lineListChangeListener);
				track.addNoteListListener(noteListChangeListener);
				track.setVisible(true);
				track.setMuted(false);
				if (oldTrack == null) {
					setActiveLine(track.getDefaultLine());
				} else {
					assertAllNeededTonesVisible();
				}
			} else {
				assert (getSong() == null);
			}
		}
	}

	public ReadOnlyObjectProperty<SongLine> activeLineProperty() {
		return activeLine.getReadOnlyProperty();
	}

	public final SongLine getActiveLine() {
		return activeLine.get();
	}

	public final void setActiveLine(SongLine line) {
		SongLine oldLine = getActiveLine();
		if (line != oldLine) {
			if (oldLine != null) {
				oldLine.removeListener(boundsListener);
			}
			if (line != null) {
				player.stop();
			}
			activeLine.set(line);
			if (line != null) {
				line.addListener(boundsListener);
				if (line.size() > 0) {
					visibleArea.adjustToBounds(line);
					selection.selectOnly(line.getNotes().get(0));
				}
			}
		}
	}

	public ReadOnlyObjectProperty<ViewMode> activeViewModeProperty() {
		return activeViewMode.getReadOnlyProperty();
	}

	public final ViewMode getActiveViewMode() {
		return activeViewModeProperty().get();
	}

	// Listeners that are necessary to assure consistency
	private void onPlayerStatusChanged(Observable obs, Status oldStatus, Status newStatus) {
		if (newStatus == Status.PLAYING) {
			markerTimeProperty().addListener(markerPositionChangeListener);
		} else {
			markerTimeProperty().removeListener(markerPositionChangeListener);
		}
	}

	private void onBeatMillisConverterInvalidated() {
		player.stop();
		if (getSong() == null) {
			beatMillisConverter.setBpm(Song.DEFAULT_BPM);
			beatMillisConverter.setGap(Song.DEFAULT_GAP);
		} else {
			beatMillisConverter.setBpm(getSong().getBpm());
			beatMillisConverter.setGap(getSong().getGap());
		}
	}

	private void onBoundsInvalidated() {
		SongLine activeLine = getActiveLine();
		if (activeLine != null && activeLine.isValid()) {
			visibleArea.adjustToBounds(activeLine);
		}
	}

	private void onSelectionBoundsInvalidated() {
		if (selection.size() > 0 && selectionBounds.isValid()) {
			setMarkerBeat(selectionBounds.getLowerXBound());
			if (visibleArea.assertBorderlessBoundsVisible(selectionBounds)) {
				setActiveLine(null);
				assertAllNeededTonesVisible();
			}
		}
	}

	private void onMarkerPositionWhilePlayingChanged(Observable obs) {
		int markerBeat = getMarkerBeat();
		if (!visibleArea.inBoundsX(markerBeat)) {
			int xRange = visibleArea.getUpperXBound() - visibleArea.getLowerXBound();
			setVisibleAreaXBounds(markerBeat - 1, markerBeat - 1 + xRange);
		}
	}

	private void onLineRemoved(SongLine line) {
		if (line == getActiveLine()) {
			setActiveLine(null);
		}
	}

	private void onNoteRemoved(Note note) {
		if (selection.isSelected(note)) {
			selection.deselect(note);
		}
	}

	// Other
	public boolean needsSaving() {
		return getSong() != null && lastSavedCommand.get() != history.getActiveCommand();
	}

	public Logger getMainLogger() {
		return LOGGER;
	}

	private void reset(boolean resetPlayer) {
		setSong(null);
		lastSavedCommand.set(null);
		history.clear();
		player.stop();
		visibleArea.setDefault();
		if (resetPlayer) {
			player.reset();
		}
	}

	// *************************** ACTIONS ***************************

	private class ActionHelper {
		private ActionMap actionMap = new ActionMap();

		public void add(KarediActions key, KarediAction action) {
			actionMap.put(key.toString(), action);
		}

		public void execute(KarediActions action) {
			if (canExecute(action)) {
				getAction(action).handle(null);
			}
		}

		public boolean canExecute(KarediActions action) {
			return !getAction(action).isDisabled();
		}

		public KarediAction get(KarediActions key) {
			return actionMap.get(key.toString());
		}

		private void addActions() {
			addFileActions();
			addEditActions();
			addPlayActions();
			addSelectionActions();
			addTagsActions();
			addViewActions();
			addHelpActions();
		}

		private void addFileActions() {
			add(KarediActions.NEW, new NewSongAction());
			add(KarediActions.LOAD, new LoadSongAction());
			add(KarediActions.RELOAD, new ReloadSongAction());
			add(KarediActions.SAVE, new SaveSongAction());
			add(KarediActions.SAVE_AS, new SaveSongAsAction());
			add(KarediActions.IMPORT_AUDIO, new ImportAudioAction());
			add(KarediActions.EXPORT_AS_SINGLEPLAYER, new ExportTracksAction(1));
			add(KarediActions.EXPORT_AS_DUET, new ExportTracksAction(2));
			add(KarediActions.EXIT, new ExitAction());
		}

		private void addEditActions() {
			add(KarediActions.UNDO, new UndoAction());
			add(KarediActions.REDO, new RedoAction());

			add(KarediActions.MOVE_SELECTION_UP, new MoveSelectionAction(Direction.UP));
			add(KarediActions.MOVE_SELECTION_DOWN, new MoveSelectionAction(Direction.DOWN));
			add(KarediActions.MOVE_SELECTION_LEFT, new MoveSelectionAction(Direction.LEFT));
			add(KarediActions.MOVE_SELECTION_RIGHT, new MoveSelectionAction(Direction.RIGHT));

			add(KarediActions.SHORTEN_LEFT_SIDE, new ResizeAction(Direction.LEFT, -1));
			add(KarediActions.SHORTEN_RIGHT_SIDE, new ResizeAction(Direction.RIGHT, -1));
			add(KarediActions.LENGTHEN_LEFT_SIDE, new ResizeAction(Direction.LEFT, 1));
			add(KarediActions.LENGTHEN_RIGHT_SIDE, new ResizeAction(Direction.RIGHT, 1));

			add(KarediActions.CUT, new CutSelectionAction());
			add(KarediActions.COPY, new CopySelectionAction());
			add(KarediActions.PASTE, new PasteAction());
			add(KarediActions.SET_TONES, new MergeAction(MergeMode.TONES));
			add(KarediActions.SET_SYNCHRO, new MergeAction(MergeMode.SYNCHRO));
			add(KarediActions.SET_LYRICS, new MergeAction(MergeMode.LYRICS));
			add(KarediActions.SET_TONES_AND_SYNCHRO, new MergeAction(MergeMode.TONES_SYNCHRO));
			add(KarediActions.SET_TONES_AND_LYRICS, new MergeAction(MergeMode.TONES_LYRICS));
			add(KarediActions.SET_SYNCHRO_AND_LYRICS, new MergeAction(MergeMode.SYNCHRO_LYRICS));
			add(KarediActions.SET_TONES_SYNCHRO_AND_LYRICS,
					new MergeAction(MergeMode.TONES_SYNCHRO_LYRICS));

			add(KarediActions.ADD_NOTE, new AddNoteAction());
			add(KarediActions.ADD_TRACK, new AddTrackAction());
			add(KarediActions.DELETE_SELECTION, new DeleteSelectionAction(true));
			add(KarediActions.DELETE_LYRICS, new DeleteSelectionLyricsAction());
			add(KarediActions.DELETE_SELECTION_HARD, new DeleteSelectionAction(false));
			add(KarediActions.DELETE_TRACK, new DeleteTrackAction());
			add(KarediActions.TOGGLE_LINEBREAK, new ToggleLineBreakAction());
			add(KarediActions.SPLIT_SELECTION, new SplitSelectionAction());
			add(KarediActions.JOIN_SELECTION, new JoinSelectionAction());
			add(KarediActions.MARK_AS_FREESTYLE, new ChangeSelectionTypeAction(Type.FREESTYLE));
			add(KarediActions.MARK_AS_GOLDEN, new ChangeSelectionTypeAction(Type.GOLDEN));
			add(KarediActions.MARK_AS_RAP, new ChangeSelectionTypeAction(Type.RAP));

			add(KarediActions.ROLL_LYRICS_LEFT, new RollLyricsLeftAction());
			add(KarediActions.ROLL_LYRICS_RIGHT, new RollLyricsRightAction());

			add(KarediActions.SHOW_PREFERENCES, new ShowPreferencesAction());
		}

		private void addPlayActions() {
			add(KarediActions.PLAY_SELECTION_AUDIO, new PlaySelectionAction(Mode.AUDIO_ONLY));
			add(KarediActions.PLAY_SELECTION_MIDI, new PlaySelectionAction(Mode.MIDI_ONLY));
			add(KarediActions.PLAY_SELECTION_AUDIO_MIDI, new PlaySelectionAction(Mode.AUDIO_MIDI));
			add(KarediActions.PLAY_VISIBLE_AUDIO, new PlayRangeAction(Mode.AUDIO_ONLY,
					visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty()));
			add(KarediActions.PLAY_VISIBLE_MIDI, new PlayRangeAction(Mode.MIDI_ONLY,
					visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty()));
			add(KarediActions.PLAY_VISIBLE_AUDIO_MIDI, new PlayRangeAction(Mode.AUDIO_MIDI,
					visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty()));
			add(KarediActions.PLAY_ALL_AUDIO,
					new PlayRangeAction(Mode.AUDIO_ONLY, minBeatProperty(), maxBeatProperty()));
			add(KarediActions.PLAY_ALL_MIDI,
					new PlayRangeAction(Mode.MIDI_ONLY, minBeatProperty(), maxBeatProperty()));
			add(KarediActions.PLAY_ALL_AUDIO_MIDI,
					new PlayRangeAction(Mode.AUDIO_MIDI, minBeatProperty(), maxBeatProperty()));

			IntegerBinding playToTheEndStartBeat = Bindings.createIntegerBinding(() -> {
				if (isMarkerVisible()) {
					return getMarkerBeat();
				} else {
					return visibleArea.getLowerXBound();
				}
			}, markerBeatProperty(), visibleArea);
			add(KarediActions.PLAY_TO_THE_END_AUDIO,
					new PlayRangeAction(Mode.AUDIO_ONLY, playToTheEndStartBeat, maxBeatProperty()));
			add(KarediActions.PLAY_TO_THE_END_MIDI,
					new PlayRangeAction(Mode.MIDI_ONLY, playToTheEndStartBeat, maxBeatProperty()));
			add(KarediActions.PLAY_TO_THE_END_AUDIO_MIDI,
					new PlayRangeAction(Mode.AUDIO_MIDI, playToTheEndStartBeat, maxBeatProperty()));
			add(KarediActions.PLAY_MEDLEY_AUDIO, new PlayMedleyAction(Mode.AUDIO_ONLY));
			add(KarediActions.PLAY_MEDLEY_AUDIO_MIDI, new PlayMedleyAction(Mode.AUDIO_MIDI));
			add(KarediActions.PLAY_MEDLEY_MIDI, new PlayMedleyAction(Mode.MIDI_ONLY));
			add(KarediActions.PLAY_BEFORE_SELECTION, new PlayAuxiliaryNoteBeforeSelectionAction());
			add(KarediActions.PLAY_AFTER_SELECTION, new PlayAuxiliaryNoteAfterSelectionAction());
			add(KarediActions.STOP_PLAYBACK, new StopPlaybackAction());
			add(KarediActions.TOGGLE_TICKS, new ToggleTicksAction());
			add(KarediActions.TOGGLE_MIDI, new ToggleMidiAction());
		}

		private void addSelectionActions() {
			add(KarediActions.SELECT_PREVIOUS, new SelectPreviousAction());
			add(KarediActions.SELECT_NEXT, new SelectNextAction());
			add(KarediActions.DECREASE_SELECTION, new SelectLessAction());
			add(KarediActions.INCREASE_SELECTION, new SelectMoreAction());
			add(KarediActions.CLEAR_SELECTION, new SelectNoneAction());
			add(KarediActions.SELECT_VISIBLE, new SelectVisibleAction());
			add(KarediActions.SELECT_ALL, new SelectAllAction());
		}

		private void addViewActions() {
			add(KarediActions.VIEW_NEXT_LINE, new NextLineAction());
			add(KarediActions.VIEW_PREVIOUS_LINE, new PreviousLineAction());
			add(KarediActions.VIEW_NEXT_TRACK, new NextTrackAction());
			add(KarediActions.VIEW_PREVIOUS_TRACK, new PreviousTrackAction());

			add(KarediActions.FIT_TO_VISIBLE, new FitToVisibleAction(true, true));
			add(KarediActions.FIT_TO_SELECTION, new FitToSelectionAction());
			add(KarediActions.FIT_VERTICALLY, new FitToVisibleAction(true, false));
			add(KarediActions.FIT_HORIZONTALLY, new FitToVisibleAction(false, true));

			add(KarediActions.MOVE_VISIBLE_AREA_LEFT, new MoveVisibleAreaAction(Direction.LEFT, 1));
			add(KarediActions.MOVE_VISIBLE_AREA_RIGHT,
					new MoveVisibleAreaAction(Direction.RIGHT, 1));
			add(KarediActions.MOVE_VISIBLE_AREA_UP, new MoveVisibleAreaAction(Direction.UP, 1));
			add(KarediActions.MOVE_VISIBLE_AREA_DOWN, new MoveVisibleAreaAction(Direction.DOWN, 1));

			add(KarediActions.VIEW_MEDLEY, new ViewMedleyAction());
			add(KarediActions.SWITCH_MODE, new SwitchModeAction());
		}

		private void addTagsActions() {
			add(KarediActions.MULTIPLY_BPM_BY_TWO, new EditBpmAction(2));
			add(KarediActions.DIVIDE_BPM_BY_TWO, new EditBpmAction(0.5));
			add(KarediActions.EDIT_BPM, new EditBpmAction());

			add(KarediActions.MEDLEY_FROM_SELECTION, new SetMedleyFromSelectionAction(true, true));
			add(KarediActions.MEDLEY_SET_START, new SetMedleyFromSelectionAction(true, false));
			add(KarediActions.MEDLEY_SET_END, new SetMedleyFromSelectionAction(false, true));
			add(KarediActions.EDIT_MEDLEY, new EditMedleyAction());

			add(KarediActions.SET_START_TAG, new SetTagValueFromMarkerPositionAction(TagKey.START));
			add(KarediActions.SET_END_TAG, new SetTagValueFromMarkerPositionAction(TagKey.END));
			add(KarediActions.SET_GAP_TAG, new SetTagValueFromMarkerPositionAction(TagKey.GAP));

			add(KarediActions.RENAME, new RenameAction());
			add(KarediActions.ADD_TAG, new AddTagAction());
		}

		private void addHelpActions() {
			add(KarediActions.RESET_SEQUENCER, new ResetSequencerAction());
		}
	}

	private class ExitAction extends KarediAction {

		@Override
		protected void onAction(ActionEvent event) {
			KarediApp.getInstance().exit(event);
		}

	}

	private class PlaySelectionAction extends KarediAction {
		private Mode mode;

		private PlaySelectionAction(Mode mode) {
			this.mode = mode;
			BooleanBinding condition = selectionIsEmpty;
			if (mode != Mode.MIDI_ONLY) {
				condition = condition.or(activeAudioIsNull);
			}
			setDisabledCondition(condition);
		}

		@Override
		protected void onAction(ActionEvent event) {
			playSelection(mode);
		}

		private void playSelection(Mode mode) {
			if (selection.size() > 0 && selectionBounds.isValid()) {
				long startMillis = beatToMillis(selectionBounds.getLowerXBound());
				long endMillis = beatToMillis(selectionBounds.getUpperXBound());
				play(startMillis, endMillis, getSelected(), mode);
			}
		}
	}

	private class PlayRangeAction extends KarediAction {
		private Mode mode;
		private ObservableValue<? extends Number> from;
		private ObservableValue<? extends Number> to;

		private PlayRangeAction(Mode mode, ObservableValue<? extends Number> from,
				ObservableValue<? extends Number> to) {
			this.mode = mode;
			this.from = from;
			this.to = to;

			BooleanBinding condition = activeSongIsNull;
			if (mode != Mode.MIDI_ONLY) {
				condition = condition.or(activeAudioIsNull);
			}
			setDisabledCondition(condition);
		}

		@Override
		protected void onAction(ActionEvent event) {
			playRange(from.getValue().intValue(), to.getValue().intValue(), mode);
		}

	}

	private class PlayMedleyAction extends KarediAction {
		private Mode mode;
		private BooleanBinding basicCondition;
		private Medley medley;

		private PlayMedleyAction(Mode mode) {
			this.mode = mode;

			basicCondition = activeSongIsNull;
			if (mode != Mode.MIDI_ONLY) {
				basicCondition = basicCondition.or(activeAudioIsNull);
			}
			setDisabledCondition(basicCondition);
			activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
				if (newVal == null) {
					setDisabledCondition(basicCondition);
				} else {
					medley = newVal.getMedley();
					setDisabledCondition(
							basicCondition.or(medley.sizeProperty().lessThanOrEqualTo(0)));
				}
			});
		}

		@Override
		protected void onAction(ActionEvent event) {
			selection.clear();
			playRange(medley.getStartBeat(), medley.getEndBeat(), mode);
		}

	}

	private class LoadSongAction extends KarediAction {

		@Override
		protected void onAction(ActionEvent event) {
			if (KarediApp.getInstance().saveChangesIfUserWantsTo()) {
				File file = KarediApp.getInstance().getTxtFileToOpen();
				if (file != null) {
					loadSongFile(file, true);
				}
			}
		}
	}

	private class ReloadSongAction extends KarediAction {
		private Integer trackNumber;
		private Integer lineNumber;
		private List<Color> colors;

		private ReloadSongAction() {
			setDisabledCondition(activeFileIsNull.or(activeSongIsNull));
		}

		@Override
		protected void onAction(ActionEvent event) {
			if (KarediApp.getInstance().saveChangesIfUserWantsTo()) {
				backupTrackAndLine();
				backupColors();
				loadSongFile(getActiveFile(), false);

				if (getSong() != null) {
					restoreTrackAndLine();
					restoreColors();
				}
			}
		}

		private void backupTrackAndLine() {
			trackNumber = null;
			lineNumber = null;
			if (getActiveTrack() != null) {
				trackNumber = getSong().indexOf(getActiveTrack());
				if (getActiveLine() != null) {
					lineNumber = getActiveTrack().indexOf(getActiveLine());
				}
			}
		}

		private void backupColors() {
			colors = getSong().getTracks().stream().map(SongTrack::getColor)
					.collect(Collectors.toList());
		}

		private void restoreTrackAndLine() {
			if (trackNumber != null && getSong().size() > trackNumber) {
				setActiveTrack(getSong().get(trackNumber));
				if (lineNumber != null && getActiveTrack().size() > lineNumber) {
					setActiveLine(getActiveTrack().get(lineNumber));
				}
			}
		}

		private void restoreColors() {
			for (int i = 0; i < getSong().size() && i < colors.size(); ++i) {
				getSong().getTrack(i).setColor(colors.get(i));
			}
		}
	}

	private class SaveSongAction extends KarediAction {

		private SaveSongAction() {
			setDisabledCondition(activeSongIsNull
					.or(lastSavedCommand.isEqualTo(history.activeCommandProperty())));
		}

		@Override
		protected void onAction(ActionEvent event) {
			if (getActiveFile() != null) {
				saveSongToFile(getActiveFile());
			} else {
				execute(KarediActions.SAVE_AS);
			}
		}
	}

	private class SaveSongAsAction extends KarediAction {

		private SaveSongAsAction() {
			setDisabledCondition(activeSongIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			File file = KarediApp.getInstance().getTxtFileToSave();
			if (saveSongToFile(file)) {
				setActiveFile(file);
			}
		}
	}

	private class ImportAudioAction extends KarediAction {

		private ImportAudioAction() {
			setDisabledCondition(activeSongIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			File file = KarediApp.getInstance().getMp3FileToOpen();
			if (file != null) {
				loadAudioFile(file);
			}
		}
	}

	private class MoveSelectionAction extends KarediAction {
		private Direction direction;

		private MoveSelectionAction(Direction direction) {
			this.direction = direction;
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			execute(new MoveCollectionCommand<Integer, Note>(getSelection().get(), direction, 1));
		}

	}

	private class SelectNoneAction extends KarediAction {

		private SelectNoneAction() {
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			selection.clear();
		}
	}

	private class SelectMoreAction extends KarediAction {

		private SelectMoreAction() {
			setDisabledCondition(activeTrackIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			selection.makeSelectionConsecutive();
			Optional<Note> lastNote = selection.getLast();
			if (lastNote.isPresent()) {
				lastNote.flatMap(Note::getNext).ifPresent(nextNote -> {
					selection.select(nextNote);
					correctVisibleArea(lastNote.get(), nextNote);
				});
			} else {
				getActiveTrack().noteAtOrLater(getMarkerBeat()).ifPresent(selection::select);
			}
		}

		private void correctVisibleArea(Note lastNote, Note nextNote) {
			int lowerXBound = visibleArea.getLowerXBound();
			int upperXBound = visibleArea.getUpperXBound();
			if (lastNote.getLine() != nextNote.getLine()) {
				int nextLineUpperBound = nextNote.getLine().getLast().getStart() + 1;
				if (!visibleArea.inBoundsX(nextLineUpperBound)) {
					upperXBound = nextLineUpperBound;
					setVisibleAreaXBounds(lowerXBound, upperXBound);
					List<Note> visibleNotes = getActiveTrack().getNotes(lowerXBound, upperXBound);
					if (visibleNotes.size() > 0) {
						visibleArea.assertBorderlessBoundsVisible(new BoundingBox<>(visibleNotes));
					}
				}
				setActiveLine(null);
			}
		}
	}

	private class SelectLessAction extends KarediAction {

		private SelectLessAction() {
			setDisabledCondition(selection.sizeProperty().lessThanOrEqualTo(1));
		}

		@Override
		protected void onAction(ActionEvent event) {
			selection.makeSelectionConsecutive();
			selection.getLast().ifPresent(note -> selection.deselect(note));
		}
	}

	private class SelectAllAction extends KarediAction {

		private SelectAllAction() {
			setDisabledCondition(activeTrackIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			selection.set(getActiveTrack().getNotes());
		}

	}

	private class SelectVisibleAction extends KarediAction {

		private SelectVisibleAction() {
			setDisabledCondition(activeTrackIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			List<Note> notes;
			if (getActiveLine() != null) {
				notes = getActiveLine().getNotes();
			} else {
				notes = getActiveTrack().getNotes(visibleArea.getLowerXBound(),
						visibleArea.getUpperXBound());
			}
			selection.set(notes);
		}

	}

	private class SelectNextAction extends KarediAction {

		private SelectNextAction() {
			setDisabledCondition(activeTrackIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			Optional<Note> nextNote = selection.getLast().flatMap(Note::getNext)
					.filter(AppContext.this::isInVisibleBeatRange);
			if (!nextNote.isPresent() && selection.size() == 0) {
				int markerBeat = getMarkerBeat();
				nextNote = getActiveTrack().noteAtOrLater(markerBeat)
						.filter(AppContext.this::isInVisibleBeatRange);
			}
			if (!nextNote.isPresent()) {
				nextNote = getActiveTrack().noteAtOrLater(visibleArea.getLowerXBound());
			}
			if (getActiveLine() != null) {
				nextNote = Optional
						.ofNullable(nextNote.filter(note -> note.getLine().equals(getActiveLine()))
								.orElse(getActiveLine().getFirst()));
			}
			nextNote.ifPresent(selection::selectOnly);
		}
	}

	private class SelectPreviousAction extends KarediAction {

		private SelectPreviousAction() {
			setDisabledCondition(activeTrackIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			Optional<Note> prevNote = selection.getFirst().flatMap(Note::getPrevious)
					.filter(AppContext.this::isInVisibleBeatRange);

			if (!prevNote.isPresent() && selection.size() == 0) {
				int markerBeat = getMarkerBeat();
				if (beatMillisConverter.beatToMillis(markerBeat) > getMarkerTime()) {
					markerBeat -= 1;
				}
				prevNote = getActiveTrack().noteAtOrEarlier(markerBeat)
						.filter(AppContext.this::isInVisibleBeatRange);
			}
			if (!prevNote.isPresent()) {
				prevNote = getActiveTrack().noteAtOrEarlier(visibleArea.getUpperXBound() - 1);
			}
			if (getActiveLine() != null) {
				prevNote = Optional
						.ofNullable(prevNote.filter(note -> note.getLine().equals(getActiveLine()))
								.orElse(getActiveLine().getLast()));
			}
			prevNote.ifPresent(selection::selectOnly);
		}
	}

	private class NextLineAction extends KarediAction {

		private NextLineAction() {
			setDisabledCondition(Bindings.createBooleanBinding(() -> {
				if (getActiveTrack() == null || !computeNextLine().isPresent()) {
					return true;
				}
				return false;
			}, activeTrack, activeLine, markerBeatProperty()));
		}

		@Override
		protected void onAction(ActionEvent event) {
			computeNextLine().ifPresent(line -> setActiveLine(line));
		}

		private Optional<SongLine> computeNextLine() {
			if (getActiveLine() != null) {
				return getActiveLine().getNext();
			} else {
				SongLine nextLine = selection.getLast().map(Note::getLine)
						.orElse(getActiveTrack().lineAtOrLater(getMarkerBeat()).orElse(null));
				return Optional.ofNullable(nextLine);
			}

		}
	}

	private class PreviousLineAction extends KarediAction {

		private PreviousLineAction() {
			setDisabledCondition(Bindings.createBooleanBinding(() -> {
				if (getActiveTrack() == null || !computePreviousLine().isPresent()) {
					return true;
				}
				return false;
			}, activeTrack, activeLine, markerBeatProperty()));

		}

		@Override
		protected void onAction(ActionEvent event) {
			computePreviousLine().ifPresent(line -> setActiveLine(line));
		}

		private Optional<SongLine> computePreviousLine() {
			if (getActiveLine() != null) {
				return getActiveLine().getPrevious();
			} else {
				SongLine previousLine = selection.getFirst().map(Note::getLine)
						.orElse(getActiveTrack().lineAtOrEarlier(getMarkerBeat()).orElse(null));
				return Optional.ofNullable(previousLine);
			}

		}
	}

	private class UndoAction extends KarediAction {

		private UndoAction() {
			setDisabledCondition(Bindings.createBooleanBinding(() -> {
				return !history.canUndo();
			}, history.activeIndexProperty()));
		}

		@Override
		protected void onAction(ActionEvent event) {
			history.undo();
		}
	}

	private class RedoAction extends KarediAction {
		private BooleanBinding redoDisabled;

		private RedoAction() {
			redoDisabled = Bindings.createBooleanBinding(() -> {
				return !history.canRedo();
			}, history.activeIndexProperty(), history.sizeProperty());

			setDisabledCondition(redoDisabled);
		}

		@Override
		protected void onAction(ActionEvent event) {
			history.redo();
		}

	}

	private class ToggleLineBreakAction extends KarediAction {

		private ToggleLineBreakAction() {
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			Note splittingNote = selection.getFirst().get();
			Command cmd = new ToggleLineBreakCommand(splittingNote);
			execute(new ChangePostStateCommandDecorator(cmd, (command) -> {
				setActiveLine(splittingNote.getLine());
				selection.selectOnly(splittingNote);
			}));
		}
	}

	private class SplitSelectionAction extends KarediAction {
		private ObjectBinding<Note> splitNote = BindingsUtils.valueAt(getSelection().get(), 0);
		private BooleanProperty disabled = new SimpleBooleanProperty();

		private SplitSelectionAction() {
			InvalidationListener lengthListener = ((inv) -> {
				refreshDisabled();
			});

			splitNote.addListener((obsVal, oldVal, newVal) -> {
				if (oldVal != null) {
					oldVal.lengthProperty().removeListener(lengthListener);
				}
				if (newVal != null) {
					newVal.lengthProperty().addListener(lengthListener);
				}
				refreshDisabled();
			});

			refreshDisabled();
			setDisabledCondition(disabled);
		}

		private void refreshDisabled() {
			Note note = splitNote.get();
			disabled.set(!SplitNoteCommand.canExecute(note, splitPoint(note)));
		}

		@Override
		protected void onAction(ActionEvent event) {
			Note note = splitNote.get();
			Command cmd = new SplitNoteCommand(note, splitPoint(note));
			execute(new ChangePostStateCommandDecorator(cmd, (command) -> {
				selection.selectOnly(note);
			}));
		}

		private int splitPoint(Note note) {
			if (note == null) {
				return 0;
			}
			return (int) Math.ceil(note.getLength() / 2.0);
		}
	}

	private class JoinSelectionAction extends KarediAction {

		private JoinSelectionAction() {
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			Note outcome = selection.getFirst().get();
			if (selection.size() == 1) {
				selection.getLast().flatMap(Note::getNext).ifPresent(selection::select);
			}
			execute(new JoinNotesCommand(getSelected()));
			selection.selectOnly(outcome);
		}

	}

	private class DeleteSelectionAction extends KarediAction {
		private boolean keepLyrics;

		private DeleteSelectionAction(boolean keepLyrics) {
			this.keepLyrics = keepLyrics;
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			player.stop();
			execute(getCommand());
		}

		private Command getCommand() {
			Command cmd = new DeleteNotesCommand(getSelected(), keepLyrics);
			IntBounded bounds = BoundingBox.boundsFrom(getVisibleAreaBounds());
			return new ChangePostStateCommandDecorator(cmd, (command) -> {
				selection.clear();
				if (getActiveLine() != null && !getActiveLine().isValid()) {
					setActiveLine(null);
					visibleArea.setBounds(bounds);
				}
			});
		}

	}

	private class DeleteSelectionLyricsAction extends KarediAction {

		private DeleteSelectionLyricsAction() {
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			Note first = selection.getFirst().get();
			execute(new DeleteTextCommand(first, selection.getLast().get()));
		}

	}

	private class AddNoteAction extends KarediAction {
		private AddNoteAction() {
			setDisabledCondition(Bindings.createBooleanBinding(() -> {
				if (getActiveTrack() == null) {
					return true;
				} else {
					int newNotePosition = computePosition();
					return getActiveTrack().noteAt(newNotePosition).isPresent();
				}
			}, selectionBounds, markerTimeProperty(), activeTrackProperty()));
		}

		@Override
		protected void onAction(ActionEvent event) {
			int startBeat = computePosition();
			int length = computeLength(startBeat);
			Optional<SongLine> optLine = computeLine();

			int tone = optLine.flatMap(line -> computeTone(line, startBeat)).orElse(0);
			Note note = new Note(startBeat, length, tone);

			Command cmd;
			if (optLine.isPresent()) {
				cmd = new AddNoteCommand(note, optLine.get());
			} else {
				cmd = new AddNoteCommand(note, getActiveTrack());
			}
			execute(new ChangePostStateCommandDecorator(cmd, (command) -> {
				selection.selectOnly(note);
			}));
		}

		private int computePosition() {
			if (selection.size() > 0 && selectionBounds.isValid()) {
				return selectionBounds.getUpperXBound();
			} else {
				return getMarkerBeat();
			}
		}

		private Optional<Integer> computeTone(SongLine line, int beat) {
			return line.noteAtOrEarlier(beat).map(Note::getTone);
		}

		private int computeLength(int startBeat) {
			Optional<Integer> nextNoteStartBeat = getActiveTrack().noteAtOrLater(startBeat)
					.map(Note::getStart);
			if (nextNoteStartBeat.isPresent()) {
				return Math.min(NEW_NOTE_DEFAULT_LENGTH,
						Math.max(nextNoteStartBeat.get() - startBeat - 1, 1));
			} else {
				return NEW_NOTE_DEFAULT_LENGTH;
			}
		}

		private Optional<SongLine> computeLine() {
			if (getActiveLine() != null) {
				return Optional.of(getActiveLine());
			}
			Optional<SongLine> line = selection.getLast().map(Note::getLine);
			if (!line.isPresent()) {
				line = getLastVisibleLineBeforeMarker();
			}
			return line;
		}

		private Optional<SongLine> getLastVisibleLineBeforeMarker() {
			return getActiveTrack().lineAtOrEarlier(getMarkerBeat())
					.filter(prevLine -> prevLine.getUpperXBound() > visibleArea.getLowerXBound());
		}
	}

	private class AddTrackAction extends KarediAction {

		private AddTrackAction() {
			setDisabledCondition(activeSongIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			Command cmd = new AddTrackCommand(getSong());
			execute(new ChangePostStateCommandDecorator(cmd, c -> {
				getSong().getLastTrack().ifPresent(AppContext.this::setActiveTrack);
			}));
		}

	}

	private class DeleteTrackAction extends KarediAction {

		private DeleteTrackAction() {
			setDisabledCondition(activeSongHasOneOrZeroTracks);
		}

		@Override
		protected void onAction(ActionEvent event) {
			execute(new DeleteTrackCommand(getSong(), getActiveTrack()));
		}

	}

	private class RollLyricsLeftAction extends KarediAction {
		private RollLyricsLeftAction() {
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			List<Note> notes = getActiveTrack().getNotes(getSelection().getFirst().get(), null);
			Command cmd = new RollLyricsLeftCommand(notes);
			execute(cmd);
		}
	}

	private class RollLyricsRightAction extends KarediAction {
		private RollLyricsRightAction() {
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			List<Note> notes = getActiveTrack().getNotes(getSelection().getFirst().get(), null);
			Command cmd = new RollLyricsRightCommand(notes);
			execute(cmd);
		}
	}

	private class NextTrackAction extends KarediAction {

		private NextTrackAction() {
			setDisabledCondition(activeSongHasOneOrZeroTracks);
		}

		@Override
		protected void onAction(ActionEvent event) {
			Song song = getSong();
			int nextIndex = (song.indexOf(getActiveTrack()) + 1) % song.size();
			setActiveTrack(song.get(nextIndex));
		}
	}

	private class PreviousTrackAction extends KarediAction {

		private PreviousTrackAction() {
			setDisabledCondition(activeSongHasOneOrZeroTracks);
		}

		@Override
		protected void onAction(ActionEvent event) {
			Song song = getSong();
			int prevIndex = (song.indexOf(getActiveTrack()) + song.size() - 1) % song.size();
			setActiveTrack(song.get(prevIndex));
		}
	}

	private abstract class PlayAuxiliaryNoteAction extends KarediAction {
		private int oldLowerBound;
		private int oldUpperBound;
		private ChangeListener<? super Status> statusListener;

		private PlayAuxiliaryNoteAction() {
			setDisabledCondition(selectionIsEmpty.or(activeAudioIsNull));
			statusListener = (obs, oldStatus, newStatus) -> {
				if (oldStatus == Status.PLAYING && newStatus == Status.READY) {
					setVisibleAreaXBounds(oldLowerBound, oldUpperBound, false);
					obs.removeListener(statusListener);
				}
			};
		}

		@Override
		protected void onAction(ActionEvent event) {
			player.stop();

			int auxiliaryNoteStartBeat = getAuxiliaryNoteStartBeat();
			int auxiliaryNoteEndBeat = auxiliaryNoteStartBeat + getAuxiliaryNoteLength();
			adjustVisibleArea(auxiliaryNoteStartBeat, auxiliaryNoteEndBeat);
			player.play(beatMillisConverter.beatToMillis(auxiliaryNoteStartBeat),
					beatMillisConverter.beatToMillis(auxiliaryNoteEndBeat), null, Mode.AUDIO_ONLY);
		}

		private void adjustVisibleArea(int auxiliaryNoteStartBeat, int auxiliaryNoteEndBeat) {
			oldLowerBound = visibleArea.getLowerXBound();
			oldUpperBound = visibleArea.getUpperXBound();
			int newLowerBound = Math.min(oldLowerBound, auxiliaryNoteStartBeat);
			int newUpperBound = Math.max(oldUpperBound, auxiliaryNoteEndBeat);
			if (newLowerBound != oldLowerBound || newUpperBound != oldUpperBound) {
				setVisibleAreaXBounds(newLowerBound, newUpperBound, false);
				player.statusProperty().addListener(statusListener);
			}
		}

		protected int getAuxiliaryNoteLength() {
			return (int) (beatMillisConverter.getBpm() / 100) + 1;
		}

		protected abstract int getAuxiliaryNoteStartBeat();
	}

	private class PlayAuxiliaryNoteBeforeSelectionAction extends PlayAuxiliaryNoteAction {

		@Override
		protected int getAuxiliaryNoteStartBeat() {
			return selection.getFirst().get().getStart() + 1 - getAuxiliaryNoteLength();
		}

	}

	private class PlayAuxiliaryNoteAfterSelectionAction extends PlayAuxiliaryNoteAction {

		@Override
		protected int getAuxiliaryNoteStartBeat() {
			return selection.getLast().get().getEnd() - 1;
		}

	}

	private class StopPlaybackAction extends KarediAction {

		private StopPlaybackAction() {
			setDisabledCondition(player.statusProperty().isNotEqualTo(Status.PLAYING));
		}

		@Override
		protected void onAction(ActionEvent event) {
			// player.stop();
			setMarkerTime(getMarkerTime());
		}

	}

	private class ResizeAction extends KarediAction {
		private Direction direction;
		private int by;
		private BooleanProperty disabled;

		private ResizeAction(Direction direction, int by) {
			this.direction = direction;
			this.by = by;

			if (by < 0) {
				disabled = new SimpleBooleanProperty(true);
				observableSelection.addListener((InvalidationListener) inv -> refreshDisabled());
				setDisabledCondition(disabled);
			} else {
				setDisabledCondition(selectionIsEmpty);
			}
		}

		@Override
		protected void onAction(ActionEvent event) {
			execute(new ResizeNotesCommand(getSelection().get(), direction, by));
		}

		private void refreshDisabled() {
			disabled.set(!ResizeNotesCommand.canExecute(getSelection().get(), direction, by));
		}

	}

	private class ToggleTicksAction extends KarediAction {

		private ToggleTicksAction() {
			setSelected(player.isTickingEnabled());
		}

		@Override
		protected void onAction(ActionEvent event) {
			player.setTickingEnabled(!player.isTickingEnabled());
		}

	}

	private class ToggleMidiAction extends KarediAction {

		private ToggleMidiAction() {
			setSelected(player.isMidiToggled());
		}

		@Override
		protected void onAction(ActionEvent event) {
			player.setMidiToggled(!player.isMidiToggled());
		}

	}

	private class MoveVisibleAreaAction extends KarediAction {
		private Direction direction;
		private int by;

		private MoveVisibleAreaAction(Direction direction, int by) {
			this.direction = direction;
			this.by = by;
		}

		@Override
		protected void onAction(ActionEvent event) {
			moveVisibleArea(direction, by);
		}

	}

	private class ViewMedleyAction extends KarediAction {

		private ViewMedleyAction() {
			setDisabledCondition(true);
			activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
				if (newVal == null) {
					setDisabledCondition(true);
				} else {
					setDisabledCondition(newVal.getMedley().sizeProperty().lessThanOrEqualTo(0));
				}
			});
		}

		@Override
		protected void onAction(ActionEvent event) {
			Medley medley = getSong().getMedley();
			setVisibleAreaXBounds(medley.getStartBeat(), medley.getEndBeat());
			assertAllNeededTonesVisible();
		}

	}

	private class FitToVisibleAction extends KarediAction {
		private boolean vertically;
		private boolean horizontally;

		private FitToVisibleAction(boolean vertically, boolean horizontally) {
			this.vertically = vertically;
			this.horizontally = horizontally;
			setDisabledCondition(activeSongIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			List<Note> visibleNotes = getSong().getVisibleNotes(visibleArea.getLowerXBound(),
					visibleArea.getUpperXBound());
			if (visibleNotes.size() > 0) {
				IntBounded bounds = addMargins(new BoundingBox<>(visibleNotes));
				if (horizontally) {
					setVisibleAreaXBounds(bounds.getLowerXBound(), bounds.getUpperXBound());
				}
				if (vertically) {
					setVisibleAreaYBounds(bounds.getLowerYBound(), bounds.getUpperYBound());
				}
			}
		}
	}

	private class FitToSelectionAction extends KarediAction {

		private FitToSelectionAction() {
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			player.stop();
			setActiveLine(null);
			visibleArea.setBounds(addMargins(selectionBounds));
		}
	}

	private class CutSelectionAction extends KarediAction {

		private CutSelectionAction() {
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			player.stop();
			Command cmd = new ChangePreStateCommandDecorator(
					new DeleteSelectionAction(false).getCommand(), c -> {
						execute(KarediActions.COPY);
					});
			cmd.setTitle(I18N.get("common.cut"));
			execute(cmd);
		}
	}

	private class CopySelectionAction extends KarediAction {
		private boolean includeLineBreak;

		private CopySelectionAction() {
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();

			includeLineBreak = false;
			String result = getSelected().stream()
					.collect(
							Collectors.groupingBy(Note::getLine, TreeMap::new, Collectors.toList()))
					.entrySet().stream().flatMap(this::disassembleLinePart).map(unparser::unparse)
					.collect(Collectors.joining(System.lineSeparator()));

			content.putString(result);
			clipboard.setContent(content);
		}

		private Stream<VisitableSongElement> disassembleLinePart(
				Map.Entry<SongLine, List<Note>> entry) {
			List<VisitableSongElement> list = entry.getValue().stream()
					.map(songDisassembler::disassemble).collect(Collectors.toList());
			if (includeLineBreak) {
				list.add(0, new LineBreakElement(entry.getKey().getLineBreak()));
			} else {
				includeLineBreak = true;
			}
			return list.stream();
		}
	}

	private abstract class ClipboardAction extends KarediAction {

		protected Song buildSong(String[] lines) {
			SongBuilder builder = new BasicSongBuilder();
			Arrays.asList(lines).forEach(line -> {
				try {
					builder.buildPart(parser.parse(line));
				} catch (InvalidSongElementException e) {
					// ignore
				}
			});
			return builder.getResult();
		}

		protected String[] getLinesFromClipboard() {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			if (clipboard.getString() == null) {
				return new String[0];
			}
			return clipboard.getString().split("\\R");
		}
	}

	private class PasteAction extends ClipboardAction {
		private PasteAction() {
			setDisabledCondition(activeTrackIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			Song pastedSong = buildSong(getLinesFromClipboard());
			List<Note> notesToSelect = new ArrayList<>();
			if (pastedSong.size() > 0) {
				notesToSelect.addAll(pastedSong.get(0).getNotes());
			}
			Command cmd = new CommandComposite(I18N.get("common.paste")) {
				@Override
				protected void buildSubCommands() {
					addSubCommand(new DeleteNotesCommand(getSelected(), false));
					addSubCommand(new PasteCommand(getActiveTrack(), pastedSong, getMarkerBeat()));
				}
			};
			execute(new ChangePostStateCommandDecorator(cmd, c -> {
				selection.set(notesToSelect);
			}));
		}

	}

	private class MergeAction extends ClipboardAction {
		private MergeMode mode;

		private MergeAction(MergeMode mode) {
			setDisabledCondition(selectionIsEmpty);
			this.mode = mode;
		}

		@Override
		protected void onAction(ActionEvent event) {
			Song pastedSong = buildSong(getLinesFromClipboard());
			if (pastedSong != null && pastedSong.size() > 0) {
				execute(new MergeNotesCommand(getSelected(), pastedSong.get(0).getNotes(), mode));
			}
		}
	}

	private class ChangeSelectionTypeAction extends KarediAction {
		private Type type;

		private ChangeSelectionTypeAction(Type type) {
			this.type = type;
			setDisabledCondition(selectionIsEmpty);
		}

		@Override
		protected void onAction(ActionEvent event) {
			execute(new MarkAsTypeCommand(new ArrayList<>(getSelected()), type));
		}

	}

	private class EditBpmAction extends KarediAction {
		private double scale;
		private boolean promptUser;

		private EditBpmAction() {
			setDisabledCondition(activeSongIsNull);
			promptUser = true;
		}

		private EditBpmAction(double scale) {
			this();
			this.scale = scale;
			promptUser = false;
		}

		@Override
		protected void onAction(ActionEvent event) {
			if (promptUser) {
				double oldBpm = getSong().getBpm();

				ModifyBpmDialog dialog = new EditBpmDialog();
				getSong().getTagValue(TagKey.BPM).ifPresent(dialog::setBpmFieldText);
				Optional<BpmEditResult> optionalResult = dialog.showAndWait();
				optionalResult.ifPresent(result -> {
					double newBpm = result.getBpm();
					if (result.shouldRescale()) {
						execute(new RescaleSongToBpmCommand(getSong(), newBpm / oldBpm));
					} else {
						execute(new ChangeBpmCommand(getSong(), newBpm));
					}
				});
			} else {
				execute(new RescaleSongToBpmCommand(getSong(), scale));
			}
		}

	}

	private class SetMedleyFromSelectionAction extends KarediAction {
		private boolean setStartBeat;
		private boolean setEndBeat;

		private SetMedleyFromSelectionAction(boolean setStartBeat, boolean setEndBeat) {
			setDisabledCondition(selectionIsEmpty);
			this.setEndBeat = setEndBeat;
			this.setStartBeat = setStartBeat;
		}

		@Override
		protected void onAction(ActionEvent event) {
			Integer startBeat = setStartBeat ? selectionBounds.getLowerXBound() : null;
			Integer endBeat = setEndBeat ? selectionBounds.getUpperXBound() : null;
			execute(new ChangeMedleyCommand(getSong(), startBeat, endBeat));
		}

	}

	private class EditMedleyAction extends KarediAction {

		private EditMedleyAction() {
			setDisabledCondition(activeSongIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			EditMedleyDialog dialog = new EditMedleyDialog();

			getSong().getTagValue(TagKey.MEDLEYSTARTBEAT)
					.ifPresent(value -> dialog.setStartBeat(value));
			getSong().getTagValue(TagKey.MEDLEYENDBEAT)
					.ifPresent(value -> dialog.setEndBeat(value));
			dialog.initModality(Modality.NONE);
			dialog.show();

			dialog.resultProperty().addListener(obs -> {
				Medley medley = dialog.getResult();
				if (medley != null) {
					execute(new ChangeMedleyCommand(getSong(), medley.getStartBeat(),
							medley.getEndBeat()));
				}
			});
		}
	}

	private class RenameAction extends KarediAction {

		private RenameAction() {
			setDisabledCondition(activeSongIsNull);
		}

		@Override
		protected void onAction(ActionEvent event) {
			EditFilenamesDialog dialog = new EditFilenamesDialog();

			getSong().getTagValue(TagKey.ARTIST).ifPresent(value -> dialog.setSongArtist(value));
			getSong().getTagValue(TagKey.TITLE).ifPresent(value -> dialog.setSongTitle(value));
			getSong().getTagValue(TagKey.MP3).ifPresent(value -> dialog.setAudioFilename(value));
			getSong().getTagValue(TagKey.COVER).ifPresent(value -> dialog.setCoverFilename(value));

			Optional<String> optVideoFilename = getSong().getTagValue(TagKey.VIDEO);
			if (optVideoFilename.isPresent()) {
				dialog.setVideoFilename(optVideoFilename.get());
			} else {
				dialog.hideVideo();
			}

			Optional<String> optBackgroundFilename = getSong().getTagValue(TagKey.BACKGROUND);
			if (optBackgroundFilename.isPresent()) {
				dialog.setBackgroundFilename(optBackgroundFilename.get());
			} else {
				dialog.hideBackground();
			}

			Optional<FilenamesEditResult> optionalResult = dialog.showAndWait();
			optionalResult.ifPresent(result -> execute(commandFromResults(result)));
		}

		private Command commandFromResults(FilenamesEditResult result) {
			return new CommandComposite(I18N.get("ui.common.rename")) {

				@Override
				protected void buildSubCommands() {
					addSubCommand(new ChangeTagValueCommand(getSong(), TagKey.ARTIST,
							result.getArtist()));
					addSubCommand(
							new ChangeTagValueCommand(getSong(), TagKey.TITLE, result.getTitle()));
					addSubCommand(new ChangeTagValueCommand(getSong(), TagKey.MP3,
							result.getAudioFilename()));
					addSubCommand(new ChangeTagValueCommand(getSong(), TagKey.COVER,
							result.getCoverFilename()));
					result.getBackgroundFilename().ifPresent(filename -> {
						addSubCommand(
								new ChangeTagValueCommand(getSong(), TagKey.BACKGROUND, filename));
					});
					result.getVideoFilename().ifPresent(filename -> {
						addSubCommand(new ChangeTagValueCommand(getSong(), TagKey.VIDEO, filename));
					});
				}
			};
		}
	}

	private class ExportTracksAction extends KarediAction {
		private int trackCount;

		private ExportTracksAction(int trackCount) {
			this.trackCount = trackCount;
			activeSong.addListener((obsVal, oldVal, newVal) -> {
				if (newVal != null) {
					setDisabledCondition(newVal.trackCount().lessThan(trackCount));
				} else {
					setDisabledCondition(true);
				}
			});
			setDisabledCondition(true);
		}

		@Override
		protected void onAction(ActionEvent event) {
			if (getSong().getProblems().size() > 0) {
				new ExportWithErrorsAlert().showAndWait().filter(result -> result == ButtonType.OK)
						.ifPresent(ok -> export());
			} else {
				export();
			}
		}

		private void export() {
			List<SongTrack> tracks = getSong().getTracks();
			if (tracks.size() != trackCount) {
				ChooseTracksDialog dialog = new ChooseTracksDialog(tracks, trackCount);
				dialog.select(getActiveTrack());
				Optional<List<SongTrack>> result = dialog.showAndWait();
				if (result.isPresent()) {
					tracks = result.get();
				} else {
					return;
				}
			}

			File file = KarediApp.getInstance().getTxtFileToSave(getInitialFileName());
			songSaver.exportToFile(file, getSong().getTags(), tracks);
		}

		private String getInitialFileName() {
			File file = getActiveFile();
			return file == null ? "" : file.getName();
		}
	}

	private abstract class TagAction extends KarediAction {

		private TagAction() {
			setDisabledCondition(activeSongIsNull);
		}

	}

	private class AddTagAction extends TagAction {

		@Override
		protected void onAction(ActionEvent event) {
			EditTagDialog dialog = new EditTagDialog(I18N.get("dialog.new_tag.title"));
			Optional<Tag> result = dialog.showAndWait();
			result.ifPresent(tag -> execute(
					new ChangeTagValueCommand(getSong(), tag.getKey(), tag.getValue())));
		}
	}

	private class SetTagValueFromMarkerPositionAction extends TagAction {
		private TagKey key;

		private SetTagValueFromMarkerPositionAction(TagKey key) {
			this.key = key;
		}

		@Override
		protected void onAction(ActionEvent event) {
			String value = null;
			if (TagKey.expectsADouble(key)) {
				value = Converter.toString(MathUtils.msToSeconds(getMarkerTime()));
			} else {
				if (TagKey.expectsAnInteger(key)) {
					value = Converter.toString(getMarkerTime());
				}
			}
			if (value != null) {
				execute(new ChangeTagValueCommand(getSong(), key, value));
			}
		}
	}

	private class SwitchModeAction extends KarediAction {

		@Override
		protected void onAction(ActionEvent event) {
			if (KarediApp.getInstance().getViewMode() == ViewMode.DAY) {
				KarediApp.getInstance().setViewMode(ViewMode.NIGHT);
			} else {
				KarediApp.getInstance().setViewMode(ViewMode.DAY);
			}
			activeViewMode.set(KarediApp.getInstance().getViewMode());
		}

	}

	private class NewSongAction extends KarediAction {
		private Song song;
		private File audioFile;
		private File outputDir;

		@Override
		protected void onAction(ActionEvent event) {
			if (needsSaving()) {
				boolean proceed = KarediApp.getInstance().saveChangesIfUserWantsTo();
				if (!proceed) {
					return;
				}
			}
			new NewSongWizard().start().ifPresent(result -> {
				song = result.getSong();
				audioFile = result.getAudioFile();
				outputDir = result.getOutputDir();
				finish();
			});
		}

		private boolean finish() {
			reset(true);
			if (outputDir == null && audioFile != null) {
				loadAudioFile(audioFile);
			}
			setSong(song);
			if (outputDir != null) {
				File songFolder = new File(outputDir, getSongFilename());
				if ((songFolder.exists() || songFolder.mkdirs()) && songFolder.canWrite()) {
					LOGGER.info(I18N.get("creator.subfolder.success"));
					KarediApp.getInstance().setInitialDirectory(songFolder);
					copyAudioFile(songFolder);
					createTxtFile(songFolder);
				} else {
					LOGGER.severe(I18N.get("creator.subfolder.fail"));
				}
			}
			return true;
		}

		private void createTxtFile(File songFolder) {
			File txtFile = new File(songFolder, getSongFilename() + ".txt");
			if (canProceedToWriteFile(txtFile)) {
				saveSongToFile(txtFile);
				setActiveFile(txtFile);
			}
		}

		private void copyAudioFile(File songFolder) {
			if (audioFile != null) {
				song.getTagValue(TagKey.MP3).ifPresent(audioFilename -> {
					File newAudioFile = new File(songFolder, audioFilename);
					if (canProceedToWriteFile(newAudioFile)) {
						try {
							Files.copy(audioFile.toPath(), newAudioFile.toPath(),
									StandardCopyOption.REPLACE_EXISTING);
						} catch (Exception e) {
							LOGGER.warning(I18N.get("creator.copy_audio.fail"));
							e.printStackTrace();
						}
						loadAudioFile(newAudioFile);
					}
				});
			}
		}

		private boolean canProceedToWriteFile(File file) {
			if (file.exists()) {
				return new OverwriteAlert(file).showAndWait().filter(type -> type == ButtonType.OK)
						.map(type -> true).orElse(false);
			}
			return true;
		}

		private String getSongFilename() {
			StringBuilder sb = new StringBuilder();
			sb.append(song.getTagValue(TagKey.ARTIST).get());
			sb.append(" - ");
			sb.append(song.getTagValue(TagKey.TITLE).get());
			return sb.toString().replaceAll(ForbiddenCharacterRegex.FOR_FILENAME, "");
		}
	}

	private class ResetSequencerAction extends KarediAction {

		private ResetSequencerAction() {
			setDisabledCondition(activeSongProperty().isNull());
		}

		@Override
		protected void onAction(ActionEvent event) {
			MidiPlayer.reset();
		}
	}

	private class ShowPreferencesAction extends KarediAction {

		private ShowPreferencesAction() {
			setDisabledCondition(false);
		}

		@Override
		protected void onAction(ActionEvent event) {
			PreferencesDialog dialog = new PreferencesDialog();
			dialog.showAndWait().filter(locale -> locale != I18N.getCurrentLocale())
					.ifPresent(Settings::setLocale);
		}
	}

}