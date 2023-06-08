package com.github.nianna.karedi.parser.elementparser;

import java.util.regex.Matcher;

import com.github.nianna.karedi.parser.element.EndOfSongElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;

/**
 * The default parser of the {@link EndOfSongElement}.
 * <p>
 * This element is represented by a single line with the letter {@code "E"}.
 */
public class EndOfSongParser extends SongElementParser {

	private static final String END_OF_SONG_PATTERN = "E";

	public EndOfSongParser() {
		super(END_OF_SONG_PATTERN);
	}

	@Override
	public VisitableSongElement createElement(Matcher matcher) {
		return new EndOfSongElement();
	}

}
