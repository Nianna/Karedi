package com.github.nianna.karedi.audio;

import javafx.concurrent.Task;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

public class AudioFileLoader {

	private AudioFileLoader() {
	}

	/**
	 * Loads the specified mp3 file on a background thread and passes the
	 * optional result to the given consumer.
	 * 
	 * @param file
	 *            the file to be loaded
	 * @param fileConsumer
	 *            the consumer to which the loaded file should be passed
	 */
	public static void loadAudioFile(File file, Consumer<Optional<PreloadedAudioFile>> fileConsumer) {
		Task<PreloadedAudioFile> task = new LoadAudioFileTask(file);
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		task.setOnSucceeded(event -> {
			fileConsumer.accept(Optional.of(task.getValue()));
		});
		task.setOnFailed(event -> {
			fileConsumer.accept(Optional.empty());
		});
		task.setOnCancelled(task.getOnFailed());
	}

	private static class LoadAudioFileTask extends Task<PreloadedAudioFile> {
		private File file;

		LoadAudioFileTask(File file) {
			this.file = file;
		}

		@Override
		protected PreloadedAudioFile call() throws Exception {
			return new Mp3File(file);
		}
	}

}
