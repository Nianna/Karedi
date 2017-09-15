package main.java.com.github.nianna.karedi.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.parser.Parser;
import main.java.com.github.nianna.karedi.parser.element.InvalidSongElementException;
import main.java.com.github.nianna.karedi.song.Song;

public class SongLoader {
	private static final String UTF8_BOM = "\uFEFF";
	private static final Logger LOGGER = Logger.getLogger(SongLoader.class.getName());

	private Parser parser;
	private SongBuilder songBuilder;

	private List<String> invalidLines;

	public SongLoader(Parser parser, SongBuilder songBuilder) {
		this.parser = parser;
		this.songBuilder = songBuilder;
	}

	private Song decodeWithCharset(Path path, Charset charset, CodingErrorAction action)
			throws IOException {
		songBuilder.reset();
		invalidLines = new LinkedList<>();

		CharsetDecoder dec = charset.newDecoder().onMalformedInput(action)
				.onUnmappableCharacter(action);

		try (Reader r = Channels.newReader(FileChannel.open(path), dec, -1);
				BufferedReader br = new BufferedReader(r)) {
			br.lines().forEach(line -> {
				line = preprocess(line);
				if (!line.isEmpty()) {
					try {
						songBuilder.buildPart(parser.parse(line.trim()));
					} catch (InvalidSongElementException e) {
						invalidLines.add(e.getLine());
					}
				}
			});
		} catch (UncheckedIOException e) {
			// decoding with provided charset failed
			return null;
		}
		return songBuilder.getResult();
	}

	public Song load(File file) {
		Song song = null;
		Path path = file.toPath();

		try {
			song = decodeWithCharset(path, StandardCharsets.UTF_8, CodingErrorAction.REPORT);
			if (song == null) {
				song = decodeWithCharset(path, StandardCharsets.ISO_8859_1,
						CodingErrorAction.REPORT);
			}
			if (song == null) {
				song = decodeWithCharset(path, StandardCharsets.UTF_8, CodingErrorAction.IGNORE);
				if (song != null) {
					LOGGER.warning(I18N.get("loader.invalid_encoding"));
				}
			}
		} catch (IOException e) {
			LOGGER.severe(I18N.get("loader.open_file.fail"));
		}
		if (song != null) {
			logInvalidLines();
		}
		return song;
	}

	private void logInvalidLines() {
		invalidLines.forEach(line -> {
			LOGGER.warning(I18N.get("loader.invalid_line", line));
		});
	}

	protected String preprocess(String line) {
		return line.replace(UTF8_BOM, "");
	}
}
