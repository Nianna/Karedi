package com.github.nianna.karedi.txt.loader;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.txt.parser.Parser;
import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.util.MultiplayerTags;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

class SongLoader {

	private static final Logger LOGGER = Logger.getLogger(SongLoader.class.getName());

	private final TxtFileLoader txtFileLoader = new TxtFileLoader(new TxtFileEncodingExtractor());

	private final Parser parser;

	SongLoader(Parser parser) {
		this.parser = parser;
	}

	Song load(File file) {
		Path path = file.toPath();

		return txtFileLoader.loadFileLines(path)
				.map(this::buildSong)
				.orElse(null);
	}

	Song buildSong(List<String> lines) {
		SongBuilder songBuilder = new BasicSongBuilder();
		lines.stream()
				.map(String::strip)
				.filter(line -> !line.isBlank())
				.map(this::parseLine)
				.filter(Objects::nonNull)
				.forEach(songBuilder::buildPart);
		Song song = songBuilder.getResult();
		extractTrackNamesFromTags(song);
		return song;
	}

	private VisitableSongElement parseLine(String line) {
		try {
			return parser.parse(line);
		} catch (InvalidSongElementException e) {
			LOGGER.warning(I18N.get("loader.invalid_line", line));
			return null;
		}
	}

	private void extractTrackNamesFromTags(Song song) {
		List<Tag> trackNameTags = song.getTags().stream()
				.filter(MultiplayerTags::isANameTag)
				.toList();
		trackNameTags.forEach(tag -> renameTrack(song, tag));
	}

	private static void renameTrack(Song song, Tag tag) {
		MultiplayerTags.getTrackNumber(tag).ifPresent(number -> {
			song.removeTag(tag);
			song.renameTrack(number, tag.getValue());
		});
	}
}
