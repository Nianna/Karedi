package com.github.nianna.karedi.audio;

import com.github.nianna.karedi.util.Utils;
import javafx.concurrent.Task;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AudioFileLoader {

	private static final List<String> MP3_EXT = List.of("mp3");
	private static final List<String> WAV_EXT = List.of("wav");
	private static final List<String> AAC_EXT = List.of("m4a", "mp4", "aac");
	private static final List<String> VORBIS_EXT = List.of("ogg");
	private static final List<String> SUPPORTED_EXTENSIONS = Stream.of(MP3_EXT, WAV_EXT, AAC_EXT, VORBIS_EXT)
			.flatMap(Collection::stream)
			.toList();

	private AudioFileLoader() {
	}

	/**
	 * Loads the specified audio file on a background thread and passes the
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
		task.setOnSucceeded(event -> fileConsumer.accept(Optional.of(task.getValue())));
		task.setOnFailed(event -> {
			fileConsumer.accept(Optional.empty());
			task.getException().printStackTrace();
		});
		task.setOnCancelled(task.getOnFailed());
	}

	public static List<String> supportedExtensions() {
		return SUPPORTED_EXTENSIONS;
	}

	private static class LoadAudioFileTask extends Task<PreloadedAudioFile> {
		private final File file;

		LoadAudioFileTask(File file) {
			this.file = file;
		}

		@Override
		protected PreloadedAudioFile call() {
			String extension = Utils.getFileExtension(file);
			if (MP3_EXT.contains(extension)) {
				return SourceDataLineAudioFile.mp3File(file);
			}
			if (WAV_EXT.contains(extension)) {
				return SourceDataLineAudioFile.wavFile(file);
			}
			if (VORBIS_EXT.contains(extension)) {
				return SourceDataLineAudioFile.vorbisFile(file);
			}
			if (SUPPORTED_EXTENSIONS.contains(extension)) {
				return SourceDataLineAudioFile.aacFile(file);
			}
            throw new RuntimeException("Unsupported file format " + extension);
        }
	}

}
