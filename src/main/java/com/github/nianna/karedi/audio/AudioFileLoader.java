package main.java.com.github.nianna.karedi.audio;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.concurrent.Task;

public class AudioFileLoader {

	/**
	 * Loads the specified mp3 file on a background thread and passes the
	 * optional result to the given consumer.
	 * 
	 * @param file
	 *            the file to be loaded
	 * @param fileConsumer
	 *            the consumer to which the loaded file should be passed
	 */
	public static void loadMp3File(File file, Consumer<Optional<Mp3File>> fileConsumer) {
		Task<Mp3File> task = new LoadMp3FileTask(file);
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

	private static class LoadMp3FileTask extends Task<Mp3File> {
		private File file;

		public LoadMp3FileTask(File file) {
			this.file = file;
		}

		@Override
		protected Mp3File call() throws Exception {
			return new Mp3File(file);
		}
	}

}
