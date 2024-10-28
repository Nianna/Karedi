package com.github.nianna.karedi.audio;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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

	private final PreloadedAudioFilePlayer preloadedAudioFilePlayer = new PreloadedAudioFilePlayer();
	private final ClipPlayer clipPlayer = new ClipPlayer();
	private final Playlist playlist = new Playlist();
	private LongProperty updateInterval = new SimpleLongProperty(TIME_UPDATE_INTERVAL_MS);
	private ReadOnlyLongWrapper currentTime = new ReadOnlyLongWrapper();
	private ReadOnlyObjectWrapper<Status> status = new ReadOnlyObjectWrapper<>(Status.UNKNOWN);
	private final IntegerProperty playbackSpeedProperty = new SimpleIntegerProperty(100);
	private TimeUpdater timeUpdater;

	private boolean tickingEnabled = true;
	private boolean midiEnabled;

	private ChangeListener<Number> timeListener = this::onTimeChanged;
	private ChangeListener<Number> volumeChangeListener = this::onVolumeChanged;
	private ChangeListener<PreloadedAudioFile> activeAudioFileChangeListener =
			this::onActiveAudioFileChanged;
	private ChangeListener<PreloadedAudioFilePlayer.PlaybackState> playbackStateChangeListener = new TimeUpdaterManager();

	private Queue<Pair<Long, Integer>> notes;

	private long startMillis;
	private long endMillis;

	private Mode mode;


	public Player() {
		clipPlayer.setClip(getClass().getResource("/player/Tick.wav"));
		preloadedAudioFilePlayer.playbackState().addListener(playbackStateChangeListener);
		playlist.activeAudioFileProperty().addListener(activeAudioFileChangeListener);
		setStatus(Status.READY);

		playbackSpeedProperty.addListener(obs -> stop());
	}

	private void onTimeChanged(ObservableValue<? extends Number> obs, Number oldTime,
			Number newTime) {
		if (notes == null || notes.isEmpty()) {
			obs.removeListener(timeListener);
			return;
		}
		List<Integer> notesToPlay = getNotesToPlay(newTime.longValue());
		if (!notesToPlay.isEmpty()) {
			if (midiEnabled) {
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

	public IntegerProperty playbackSpeedProperty() {
		return playbackSpeedProperty;
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
		timeUpdater = new TimeUpdater(startMillis, endMillis, updateInterval.get(), playbackSpeedProperty.get());
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
		if (getActivePreloadedAudioFile() == null || mode.equals(Mode.MIDI_ONLY)) {
			startTimeUpdater();
		} else {
			preloadedAudioFilePlayer.play(startMillis, endMillis, playbackSpeedProperty.get());
			getActivePreloadedAudioFile().volumeProperty().addListener(volumeChangeListener);
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
		preloadedAudioFilePlayer.stop();
		MidiPlayer.stop();
		currentTime.removeListener(timeListener);
		if (getActivePreloadedAudioFile() != null) {
			getActivePreloadedAudioFile().volumeProperty().removeListener(volumeChangeListener);
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

	private void onActiveAudioFileChanged(Observable obs, PreloadedAudioFile oldFile,
			PreloadedAudioFile newFile) {
		stop();
		setActiveAudioFile(newFile);
	}

	private void setActiveAudioFile(PreloadedAudioFile file) {
		preloadedAudioFilePlayer.setFile(file);
	}

	private PreloadedAudioFile getActivePreloadedAudioFile() {
		return preloadedAudioFilePlayer.getFile();
	}

	public void reset() {
		stop();
		playlist.clear();
		playbackSpeedProperty.setValue(100);
	}

	public void setTickingEnabled(boolean value) {
		tickingEnabled = value;
	}

	public boolean isTickingEnabled() {
		return tickingEnabled;
	}

	private static class TimeUpdater extends Task<Long> {
		private final long startMillis;
		private final long endMillis;
		private final long updateInterval;
        private final int speedPercent;

        TimeUpdater(long startMillis, long endMillis, long updateInterval, int speedPercent) {
			super();
			this.startMillis = startMillis;
			this.endMillis = endMillis;
			this.updateInterval = updateInterval;
            this.speedPercent = speedPercent;
            updateValue(startMillis);
		}

		@Override
		protected Long call() throws Exception {
			long base = System.nanoTime();
			long curTime = base;
			while (!isCancelled()) {
				long timeElapsed = (System.nanoTime() - base) / 1_000_000;
				curTime = (startMillis + timeElapsed * speedPercent / 100);
				updateValue(curTime);
				if (curTime > endMillis) {
					break;
				}
				Thread.sleep(updateInterval);
			}
			return curTime;
		}
	}

	private class TimeUpdaterManager implements ChangeListener<PreloadedAudioFilePlayer.PlaybackState> {

		@Override
		public void changed(ObservableValue<? extends PreloadedAudioFilePlayer.PlaybackState> observableValue,
							PreloadedAudioFilePlayer.PlaybackState oldState,
							PreloadedAudioFilePlayer.PlaybackState newState) {
			switch (newState) {
				case STARTED -> startTimeUpdater();
				case STOPPED -> {
					switch (timeUpdater.getState()) {
						case SCHEDULED:
						case RUNNING:
							timeUpdater.cancel();
							break;
						default:
					}
				}
			}
		}
	}

}
