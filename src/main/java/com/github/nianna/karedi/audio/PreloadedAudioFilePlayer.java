package com.github.nianna.karedi.audio;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.concurrent.Task;

/**
 * A player operating on a preloaded audio file.
 */
class PreloadedAudioFilePlayer {

	private final ReadOnlyObjectWrapper<PlaybackState> playbackState = new ReadOnlyObjectWrapper<>(PlaybackState.STOPPED);
	private PreloadedAudioFile file;
	private Task<Long> playTask;

	void play(long startMillis, long endMillis) {
		stop();

		if (file != null && startMillis < endMillis) {
			playTask = createPlayTask(file, startMillis, endMillis);
			Thread th = new Thread(playTask);
			th.setDaemon(true);
			th.start();
			playbackState.setValue(PlaybackState.STARTED);
		}
	}

	private static Task<Long> createPlayTask(PreloadedAudioFile file, Long startMillis, Long endMillis) {
		if (file instanceof Mp3File) {
			return new Mp3FilePlayTask((Mp3File) file, startMillis, endMillis);
		}
		if (file instanceof SourceDataLineAudioFile) {
			return new SourceDataLineAudioFilePlayTask((SourceDataLineAudioFile) file, startMillis, endMillis);
		}
		throw new IllegalArgumentException("Unsupported preloaded audio file type");
	}

	void stop() {
		if (playTask != null) {
			playTask.cancel();
		}
		playbackState.setValue(PlaybackState.STOPPED);
	}

	void setFile(PreloadedAudioFile file) {
		this.file = file;
	}

	PreloadedAudioFile getFile() {
		return file;
	}

	ReadOnlyObjectProperty<PlaybackState> playbackState() {
		return playbackState.getReadOnlyProperty();
	}

	enum PlaybackState {
		STARTED,
		STOPPED
	}

}
