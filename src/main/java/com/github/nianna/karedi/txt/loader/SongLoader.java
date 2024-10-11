package com.github.nianna.karedi.txt.loader;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.txt.parser.Parser;
import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;

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
				.map(String::stripLeading)
				.filter(line -> !line.isBlank())
				.map(this::parseLine)
				.filter(Objects::nonNull)
				.forEach(songBuilder::buildPart);
		return songBuilder.getResult();
	}

	private VisitableSongElement parseLine(String line) {
		try {
			return parser.parse(line);
		} catch (InvalidSongElementException e) {
			LOGGER.warning(I18N.get("loader.invalid_line", line));
			return null;
		}
	}

}
