package com.github.nianna.karedi.context;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.KarediApp.ViewMode;
import com.github.nianna.karedi.action.KarediAction;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.audio.Player.Mode;
import com.github.nianna.karedi.audio.Player.Status;
import com.github.nianna.karedi.command.BackupStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.context.actions.ActionHelper;
import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.txt.TxtFacade;
import com.github.nianna.karedi.util.BeatMillisConverter;
import com.github.nianna.karedi.util.ListenersUtils;
import com.github.nianna.karedi.util.MathUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class AppContext {
	public static final Logger LOGGER = Logger.getLogger(KarediApp.class.getPackage().getName());

	private static final int MAX_HISTORY_SIZE = 1000;

	public final ReadOnlyObjectWrapper<Song> activeSong = new ReadOnlyObjectWrapper<>();
	public final ReadOnlyObjectWrapper<SongTrack> activeTrack = new ReadOnlyObjectWrapper<>();
	public final ReadOnlyObjectWrapper<SongLine> activeLine = new ReadOnlyObjectWrapper<>();
	private final ReadOnlyObjectWrapper<File> activeFile = new ReadOnlyObjectWrapper<>();
	public final ReadOnlyObjectWrapper<ViewMode> activeViewMode = new ReadOnlyObjectWrapper<>(
			KarediApp.getInstance().getViewMode());
	public final ObjectProperty<Command> lastSavedCommand = new SimpleObjectProperty<>();

	public final TxtFacade txtFacade = new TxtFacade();
	private final SongNormalizer songNormalizer = new SongNormalizer();
	private final ActionHelper actionHelper = new ActionHelper(this);

	public final History history = new History();
	public final NoteSelection selection = new NoteSelection();
	public final BeatMillisConverter beatMillisConverter = new BeatMillisConverter(
			Song.DEFAULT_GAP, Song.DEFAULT_BPM);
	public final SongPlayer player = new SongPlayer(beatMillisConverter);
	private final BeatRange beatRange = new BeatRange(beatMillisConverter);
	public final VisibleArea visibleArea = new VisibleArea(beatRange);

	public final ObservableList<Note> observableSelection = FXCollections
			.observableArrayList(note -> new Observable[] { note });
	public final IntBounded selectionBounds = new BoundingBox<>(observableSelection);
	private File directory;

	private final ListChangeListener<? super Note> noteListChangeListener = ListenersUtils
			.createListContentChangeListener(ListenersUtils::pass, this::onNoteRemoved);
	private final ListChangeListener<? super SongLine> lineListChangeListener = ListenersUtils
			.createListContentChangeListener(ListenersUtils::pass, this::onLineRemoved);
	private final InvalidationListener markerPositionChangeListener = this::onMarkerPositionWhilePlayingChanged;
	private final InvalidationListener beatMillisConverterInvalidationListener = obs -> onBeatMillisConverterInvalidated();
	private final InvalidationListener boundsListener = obs -> onBoundsInvalidated();

	// Convenience bindings for actions
	public final BooleanBinding selectionIsEmpty = selection.sizeProperty().isEqualTo(0);
	public final BooleanBinding activeSongIsNull = activeSongProperty().isNull();
	public final BooleanBinding activeTrackIsNull = activeTrackProperty().isNull();
	public final BooleanBinding activeFileIsNull = activeFileProperty().isNull();

	private final IntegerProperty activeSongTrackCount = new SimpleIntegerProperty();
	public final BooleanBinding activeSongHasOneOrZeroTracks = activeSongTrackCount
			.lessThanOrEqualTo(1);

	public final AudioContext audioContext = new AudioContext(this, player);

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

	public void setVisibleAreaXBounds(int lowerXBound, int upperXBound, boolean setLineToNull) {
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

	public boolean isInVisibleBeatRange(Note note) {
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
	void setMaxTime(Long maxTime) {
		beatRange.setMaxTime(maxTime);
	}

	// Player
	public ReadOnlyObjectProperty<Status> playerStatusProperty() {
		return player.statusProperty();
	}

	public Status getPlayerStatus() {
		return player.getStatus();
	}

	public void playRange(int fromBeat, int toBeat, Mode mode) {
		assertAllNeededTonesVisible(fromBeat, toBeat);
		player.play(fromBeat, toBeat, mode);
	}

	public void play(long startMillis, long endMillis, List<Note> notes, Mode mode) {
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

	public boolean isMarkerVisible() {
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

	public void setActiveFile(File file) {
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

	public void loadSongFile(File file, boolean resetPlayer) {
		if (file != null) {
			reset(resetPlayer);
			setActiveFile(file);
			Song song = txtFacade.loadFromTxtFile(file);
			song.getTagValue(TagKey.MP3).ifPresent(audioFileName -> {
				audioContext.loadAudioFile(new File(file.getParent(), audioFileName));
			});
			setSong(song);
			LOGGER.info(I18N.get("load.success"));
		}
	}

	public boolean saveSongToFile(File file) {
		if (file != null) {
			if (txtFacade.saveSongToFile(file, getSong())) {
				lastSavedCommand.set(history.getLastCommandRequiringSave());
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
		songNormalizer.normalize(song);
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
		return getSong() != null && lastSavedCommand.get() != history.getLastCommandRequiringSave();
	}

	public Logger getMainLogger() {
		return LOGGER;
	}

	public void reset(boolean resetPlayer) {
		setSong(null);
		lastSavedCommand.set(null);
		history.clear();
		player.stop();
		visibleArea.setDefault();
		if (resetPlayer) {
			player.reset();
		}
	}

}
