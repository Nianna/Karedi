package main.java.com.github.nianna.karedi.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javafx.beans.Observable;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.util.Pair;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class Player {
	private static final int TIME_UPDATE_INTERVAL_MS = 5;

	public enum Mode {
		AUDIO_ONLY,
		AUDIO_MIDI,
		MIDI_ONLY
	}

	public enum Status {
		READY,
		PLAYING,
		UNKNOWN
	}

	private final Mp3Player mp3Player = new Mp3Player();
	private final ClipPlayer clipPlayer = new ClipPlayer();
	private final Playlist playlist = new Playlist();
	private LongProperty updateInterval = new SimpleLongProperty(TIME_UPDATE_INTERVAL_MS);
	private ReadOnlyLongWrapper currentTime = new ReadOnlyLongWrapper();
	private ReadOnlyObjectWrapper<Status> status = new ReadOnlyObjectWrapper<>(Status.UNKNOWN);
	private TimeUpdater timeUpdater;

	private boolean tickingEnabled = true;
	private boolean midiEnabled;
	private boolean midiToggled = false;

	private ChangeListener<Number> timeListener = this::onTimeChanged;
	private ChangeListener<Number> volumeChangeListener = this::onVolumeChanged;
	private ChangeListener<CachedAudioFile> activeAudioFileChangeListener =
			this::onActiveAudioFileChanged;
	private PlaybackListener playbackListener = new TimeUpdaterManager();

	private Queue<Pair<Long, Integer>> notes;

	private long startMillis;
	private long endMillis;

	private Mode mode;

	public Player() {
		clipPlayer.setClip(getClass().getResource("/player/Tick.wav"));
		mp3Player.setPlaybackListener(playbackListener);
		playlist.activeAudioFileProperty().addListener(activeAudioFileChangeListener);
		setStatus(Status.READY);
	}

	private void onTimeChanged(ObservableValue<? extends Number> obs, Number oldTime,
			Number newTime) {
		if (notes == null || notes.isEmpty()) {
			obs.removeListener(timeListener);
			return;
		}
		List<Integer> notesToPlay = getNotesToPlay(newTime.longValue());
		if (!notesToPlay.isEmpty()) {
			if (midiEnabled || midiToggled) {
				MidiPlayer.play(notesToPlay);
			} else {
				if (tickingEnabled) {
					clipPlayer.play();
				}
			}
		}
	}

	private List<Integer> getNotesToPlay(long currentTime) {
		List<Integer> notesToPlay = new ArrayList<>();
		Pair<Long, Integer> timeNotePair = notes.peek();
		while (timeNotePair != null && timeNotePair.getKey() <= currentTime) {
			notes.poll();
			if (timeNotePair.getKey() >= startMillis) {
				notesToPlay.add(timeNotePair.getValue());
			}
			timeNotePair = notes.peek();
		}
		return notesToPlay;
	}

	private void onVolumeChanged(ObservableValue<? extends Number> obs, Number oldVolume,
			Number newVolume) {
		if (getStatus() == Status.PLAYING) {
			play(getCurrentTime(), endMillis, notes, mode);
		}
	}

	private void startTimeUpdater() {
		if (timeUpdater != null) {
			Thread th = new Thread(timeUpdater);
			th.setDaemon(true);
			th.start();
		}
	}

	public void play(long startMillis, long endMillis, Queue<Pair<Long, Integer>> notes,
			Mode mode) {
		stop();

		setMode(mode);
		setStartMillis(startMillis);
		setEndMillis(endMillis);
		setNotes(notes);

		resetTimeUpdater();
		currentTime.addListener(timeListener);
		play();
	}

	private void setMode(Mode mode) {
		this.mode = mode;
		updateMidiEnabled();
	}

	private void setStartMillis(long startMillis) {
		if (mode != Mode.MIDI_ONLY) {
			this.startMillis = Math.max(startMillis, 0);
		} else {
			this.startMillis = startMillis;
		}
	}

	private void setEndMillis(long endMillis) {
		this.endMillis = endMillis;
	}

	private void setNotes(Queue<Pair<Long, Integer>> notes) {
		// Notes are filtered later, in the timeListener
		this.notes = notes;
	}

	private void resetTimeUpdater() {
		timeUpdater = new TimeUpdater(startMillis, endMillis, updateInterval.get());
		timeUpdater.setOnCancelled(event -> setStatus(Status.READY));
		timeUpdater.setOnSucceeded(event -> setStatus(Status.READY));
		currentTime.bind(timeUpdater.valueProperty());
	}

	private void updateMidiEnabled() {
		switch (mode) {
		case MIDI_ONLY:
		case AUDIO_MIDI:
			midiEnabled = true;
			break;
		default:
			midiEnabled = false;
		}
	}

	private void play() {
		setStatus(Status.PLAYING);
		if (getActiveMp3File() == null || mode.equals(Mode.MIDI_ONLY)) {
			startTimeUpdater();
		} else {
			mp3Player.play(startMillis, endMillis);
			getActiveMp3File().volumeProperty().addListener(volumeChangeListener);
		}
	}

	public ReadOnlyObjectProperty<Status> statusProperty() {
		return status.getReadOnlyProperty();
	}

	public final Status getStatus() {
		return status.get();
	}

	private void setStatus(Status value) {
		status.set(value);
	}

	public void stop() {
		if (timeUpdater != null) {
			timeUpdater.cancel();
		}
		mp3Player.stop();
		MidiPlayer.stop();
		currentTime.removeListener(timeListener);
		if (getActiveMp3File() != null) {
			getActiveMp3File().volumeProperty().removeListener(volumeChangeListener);
		}
	}

	public ReadOnlyLongProperty currentTimeProperty() {
		return currentTime.getReadOnlyProperty();
	}

	public final long getCurrentTime() {
		return currentTime.get();
	}

	public Playlist getPlaylist() {
		return playlist;
	}

	private void onActiveAudioFileChanged(Observable obs, CachedAudioFile oldFile,
			CachedAudioFile newFile) {
		stop();
		setActiveAudioFile(newFile);
	}

	private void setActiveAudioFile(Mp3File file) {
		mp3Player.setFile(file);
	}

	private void setActiveAudioFile(CachedAudioFile file) {
		// TODO expand this when new audio file types are supported
		if (file instanceof Mp3File) {
			setActiveAudioFile((Mp3File) file);
		} else {
			mp3Player.setFile(null);
		}
	}

	private CachedAudioFile getActiveMp3File() {
		return mp3Player.getFile();
	}

	public void reset() {
		stop();
		playlist.clear();
	}

	public void setTickingEnabled(boolean value) {
		tickingEnabled = value;
	}

	public boolean isTickingEnabled() {
		return tickingEnabled;
	}

	public void setMidiToggled(boolean value) {
		midiToggled = value;
	}

	public boolean isMidiToggled() {
		return midiToggled;
	}

	private class TimeUpdater extends Task<Long> {
		private long startMillis;
		private long endMillis;
		private long updateInterval;

		public TimeUpdater(long startMillis, long endMillis, long updateInterval) {
			super();
			this.startMillis = startMillis;
			this.endMillis = endMillis;
			this.updateInterval = updateInterval;
			updateValue(startMillis);
		}

		@Override
		protected Long call() throws Exception {
			long base = System.nanoTime();
			long curTime = base;
			while (!isCancelled()) {
				long timeElapsed = (System.nanoTime() - base) / 1_000_000;
				curTime = (startMillis + timeElapsed);
				updateValue(curTime);
				if (curTime > endMillis) {
					break;
				}
				Thread.sleep(updateInterval);
			}
			return curTime;
		}
	}

	private class TimeUpdaterManager extends PlaybackListener {
		@Override
		public void playbackFinished(PlaybackEvent arg0) {
			switch (timeUpdater.getState()) {
			case SCHEDULED:
			case RUNNING:
				timeUpdater.cancel();
				break;
			default:
			}
		}

		@Override
		public void playbackStarted(PlaybackEvent arg0) {
			startTimeUpdater();
		}
	}

}
