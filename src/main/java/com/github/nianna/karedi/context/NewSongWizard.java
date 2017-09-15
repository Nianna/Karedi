package main.java.com.github.nianna.karedi.context;

import java.io.File;
import java.util.List;
import java.util.Optional;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.dialog.AddSongInfoDialog;
import main.java.com.github.nianna.karedi.dialog.ChooseAudioFileDialog;
import main.java.com.github.nianna.karedi.dialog.ChooseDirectoryDialog;
import main.java.com.github.nianna.karedi.dialog.EditFilenamesDialog;
import main.java.com.github.nianna.karedi.dialog.EditFilenamesDialog.FilenamesEditResult;
import main.java.com.github.nianna.karedi.dialog.ModifyBpmDialog;
import main.java.com.github.nianna.karedi.dialog.ModifyBpmDialog.BpmEditResult;
import main.java.com.github.nianna.karedi.dialog.SetBpmDialog;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.tag.Tag;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.util.Converter;
import main.java.com.github.nianna.karedi.util.Utils;

class NewSongWizard {
	private Song song;
	private File audioFile;
	private File outputDir;
	private CreatorResult result;

	Optional<CreatorResult> start() {
		Utils.executeUntilFail(
				this::createSong,
				this::chooseAudio,
				this::chooseDirectory,
				this::setBpm,
				this::addTags,
				this::finish);
		return Optional.ofNullable(result);
	}

	private boolean createSong() {
		EditFilenamesDialog dialog = new EditFilenamesDialog();
		dialog.setTitle(I18N.get("dialog.creator.title"));
		Optional<FilenamesEditResult> optResult = dialog.showAndWait();
		if (optResult.isPresent()) {
			FilenamesEditResult result = optResult.get();
			song = new Song();
			song.setTagValue(TagKey.ARTIST, result.getArtist());
			song.setTagValue(TagKey.TITLE, result.getTitle());
			song.setTagValue(TagKey.MP3, result.getAudioFilename());
			song.setTagValue(TagKey.COVER, result.getCoverFilename());
			result.getBackgroundFilename().ifPresent(filename -> {
				song.setTagValue(TagKey.BACKGROUND, filename);
			});
			result.getVideoFilename().ifPresent(filename -> {
				song.setTagValue(TagKey.VIDEO, filename);
			});
			return true;
		}
		;
		return false;
	}

	private boolean chooseAudio() {
		ChooseAudioFileDialog dialog = new ChooseAudioFileDialog();
		Optional<Optional<File>> result = dialog.showAndWait();
		if (result.isPresent()) {
			audioFile = result.get().orElse(null);
			return true;
		}
		return false;
	}

	private boolean chooseDirectory() {
		ChooseDirectoryDialog dialog = new ChooseDirectoryDialog();
		Optional<Optional<File>> result = dialog.showAndWait();
		if (result.isPresent()) {
			outputDir = result.get().orElse(null);
			return true;
		}
		return false;
	}

	private boolean setBpm() {
		ModifyBpmDialog dialog = new SetBpmDialog();
		Optional<BpmEditResult> result = dialog.showAndWait();
		if (result.isPresent()) {
			song.setTagValue(TagKey.BPM, Converter.toString(result.get().getBpm()));
			return true;
		}
		return false;
	}

	private boolean addTags() {
		AddSongInfoDialog dialog = new AddSongInfoDialog();
		Optional<List<Tag>> result = dialog.showAndWait();
		if (result.isPresent()) {
			result.get().forEach(song::addTag);
			return true;
		}
		return false;
	}

	private boolean finish() {
		result = new CreatorResult(song, audioFile, outputDir);
		return true;
	}

	class CreatorResult {
		private Song song;
		private File audioFile;
		private File outputDir;

		CreatorResult(Song song, File audioFile, File outputDir) {
			this.song = song;
			this.audioFile = audioFile;
			this.outputDir = outputDir;
		}

		Song getSong() {
			return song;
		}

		File getAudioFile() {
			return audioFile;
		}

		File getOutputDir() {
			return outputDir;
		}
	}
}
