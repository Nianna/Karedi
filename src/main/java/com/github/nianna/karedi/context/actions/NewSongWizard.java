package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.Settings;
import com.github.nianna.karedi.dialog.AddSongInfoDialog;
import com.github.nianna.karedi.dialog.ChooseAudioFileDialog;
import com.github.nianna.karedi.dialog.ChooseDirectoryDialog;
import com.github.nianna.karedi.dialog.EditFilenamesDialog;
import com.github.nianna.karedi.dialog.EditFilenamesDialog.FilenamesEditResult;
import com.github.nianna.karedi.dialog.ModifyBpmDialog;
import com.github.nianna.karedi.dialog.ModifyBpmDialog.BpmEditResult;
import com.github.nianna.karedi.dialog.SetBpmDialog;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.FormatSpecification;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.util.Converter;
import com.github.nianna.karedi.util.Utils;

import java.io.File;
import java.util.List;
import java.util.Optional;

class NewSongWizard {
	private Song song;
	private File audioFile;
	private File outputDir;
	private CreatorResult result;

	Optional<CreatorResult> start() {
		Utils.executeUntilFail(
				this::chooseAudio,
				this::createSong,
				this::chooseDirectory,
				this::setBpm,
				this::addTags,
				this::finish);
		return Optional.ofNullable(result);
	}

	private boolean createSong() {
		EditFilenamesDialog dialog = new EditFilenamesDialog();
		Optional.ofNullable(audioFile)
				.map(File::getName)
				.ifPresent(dialog::initDataFromAudioFilename);
		dialog.setTitle(I18N.get("dialog.creator.title"));
		dialog.hideInstrumental();
		dialog.hideVocals();
		Settings.getDefaultFormatSpecificationVersion().ifPresent(dialog::setFormatVersion);
		Optional<FilenamesEditResult> optResult = dialog.showAndWait();
		if (optResult.isPresent()) {
			FilenamesEditResult result = optResult.get();
			song = new Song();
			result.getFormatSpecification().ifPresent(version -> song.setTagValue(TagKey.VERSION, version.toString()));
			song.setTagValue(TagKey.ARTIST, result.getArtist());
			song.setTagValue(TagKey.TITLE, result.getTitle());
			setTagValueIfSupported(TagKey.AUDIO, result.getAudioFilename());
			setTagValueIfSupported(TagKey.MP3, result.getAudioFilename());
			song.setTagValue(TagKey.COVER, result.getCoverFilename());
			result.getBackgroundFilename().ifPresent(filename -> song.setTagValue(TagKey.BACKGROUND, filename));
			result.getVideoFilename().ifPresent(filename -> song.setTagValue(TagKey.VIDEO, filename));
			result.getInstrumentalFilename().ifPresent(filename -> song.setTagValue(TagKey.INSTRUMENTAL, filename));
			result.getVocalsFilename().ifPresent(filename -> song.setTagValue(TagKey.VOCALS, filename));

			Settings.setDefaultFormatSpecificationVersion(result.getFormatSpecification().orElse(null));
			return true;
		}
		return false;
	}

	private void setTagValueIfSupported(TagKey key, String value) {
		if (FormatSpecification.supports(song.getFormatSpecificationVersion(), key)) {
			song.setTagValue(key, value);
		}
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
		Optional<File> rememberedDir = Settings.getNewSongWizardLibraryDirectory();
		if (rememberedDir.isPresent() && rememberedDir.get().exists()) {
			outputDir = rememberedDir.get();
			return true;
		}
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
		AddSongInfoDialog dialog = new AddSongInfoDialog(song.getFormatSpecificationVersion());
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

	static class CreatorResult {
		private final Song song;
		private final File audioFile;
		private final File outputDir;

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
