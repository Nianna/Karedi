package com.github.nianna.karedi.context;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.KarediApp.ViewMode;
import com.github.nianna.karedi.action.KarediAction;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.audio.Player.Mode;
import com.github.nianna.karedi.audio.Player.Status;
import com.github.nianna.karedi.context.actions.ActionHelper;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.txt.TxtFacade;
import com.github.nianna.karedi.util.ListenersUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class AppContext {
	public static final Logger LOGGER = Logger.getLogger(KarediApp.class.getPackage().getName());

	public final ReadOnlyObjectWrapper<Song> activeSong = new ReadOnlyObjectWrapper<>();
	public final ReadOnlyObjectWrapper<SongTrack> activeTrack = new ReadOnlyObjectWrapper<>();
	public final ReadOnlyObjectWrapper<SongLine> activeLine = new ReadOnlyObjectWrapper<>();
	private final ReadOnlyObjectWrapper<File> activeFile = new ReadOnlyObjectWrapper<>();
	public final ReadOnlyObjectWrapper<ViewMode> activeViewMode = new ReadOnlyObjectWrapper<>(
			KarediApp.getInstance().getViewMode());

	public final TxtFacade txtFacade = new TxtFacade();
	private final SongNormalizer songNormalizer = new SongNormalizer();
	private final ActionHelper actionHelper = new ActionHelper(this);

	public final BeatRangeContext beatRangeContext = new BeatRangeContext(this);

	public final SongPlayer player = new SongPlayer(beatRangeContext.getBeatMillisConverter());

	private File directory;

	private final ListChangeListener<? super Note> noteListChangeListener = ListenersUtils
			.createListContentChangeListener(ListenersUtils::pass, this::onNoteRemoved);
	private final ListChangeListener<? super SongLine> lineListChangeListener = ListenersUtils
			.createListContentChangeListener(ListenersUtils::pass, this::onLineRemoved);
	private final InvalidationListener markerPositionChangeListener = this::onMarkerPositionWhilePlayingChanged;

	// Convenience bindings for actions
	public final BooleanBinding activeSongIsNull = activeSongProperty().isNull();
	public final BooleanBinding activeTrackIsNull = activeTrackProperty().isNull();
	public final BooleanBinding activeFileIsNull = activeFileProperty().isNull();

	private final IntegerProperty activeSongTrackCount = new SimpleIntegerProperty();
	public final BooleanBinding activeSongHasOneOrZeroTracks = activeSongTrackCount
			.lessThanOrEqualTo(1);

	public final AudioContext audioContext = new AudioContext(this, player);

	public final SelectionContext selectionContext = new SelectionContext();

	public final VisibleAreaContext visibleAreaContext = new VisibleAreaContext(this, beatRangeContext);

	public final CommandContext commandContext = new CommandContext(this);

	public AppContext() {
		LOGGER.setUseParentHandlers(false);
		actionHelper.addActions();
		player.statusProperty().addListener(this::onPlayerStatusChanged);
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

	// Player
	public ReadOnlyObjectProperty<Status> playerStatusProperty() {
		return player.statusProperty();
	}

	public Status getPlayerStatus() {
		return player.getStatus();
	}

	public void playRange(int fromBeat, int toBeat, Mode mode) {
		visibleAreaContext.assertAllNeededTonesVisible(fromBeat, toBeat);
		player.play(fromBeat, toBeat, mode);
	}

	public void play(long startMillis, long endMillis, List<Note> notes, Mode mode) {
		if (!visibleAreaContext.isMarkerVisible()) {
			setMarkerBeat(visibleAreaContext.getLowerXBound());
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
				commandContext.updateLastSavedCommand();
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
			if (oldSong != null) {
				beatRangeContext.onSongDeactivated(oldSong);
				activeSongTrackCount.unbind();
			}
			if (song == null) {
				setActiveTrack(null);
				activeSongTrackCount.set(0);
			} else {
				beatRangeContext.onSongActivated(song);
				activeSongTrackCount.bind(song.trackCount());
				setActiveTrack(song.getDefaultTrack().orElse(null));
			}
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
			selectionContext.selection.clear();
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
					visibleAreaContext.assertAllNeededTonesVisible();
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
			visibleAreaContext.onLineDeactivated(oldLine);
			if (line != null) {
				player.stop();
			}
			activeLine.set(line);
			visibleAreaContext.onLineActivated(line);
			selectionContext.onLineActivated(line);
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

	private void onMarkerPositionWhilePlayingChanged(Observable obs) {
		visibleAreaContext.scrollVisibleAreaToMarkerBeat();
	}

	private void onLineRemoved(SongLine line) {
		if (line == getActiveLine()) {
			setActiveLine(null);
		}
	}

	private void onNoteRemoved(Note note) {
		selectionContext.removeIfSelected(note);
	}

	// Other
	public boolean needsSaving() {
		return getSong() != null && commandContext.hasUnsavedCommands();
	}

	public Logger getMainLogger() {
		return LOGGER;
	}

	public void reset(boolean resetPlayer) {
		setSong(null);
		commandContext.reset();
		player.stop();
		visibleAreaContext.reset();
		if (resetPlayer) {
			player.reset();
		}
	}

}
