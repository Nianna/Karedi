package com.github.nianna.karedi.loader;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.parser.Parser;
import com.github.nianna.karedi.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.song.Song;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
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
