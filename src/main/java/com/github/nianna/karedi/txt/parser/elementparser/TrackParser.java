package com.github.nianna.karedi.txt.parser.elementparser;

import com.github.nianna.karedi.txt.parser.element.TrackElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;

import java.util.regex.Matcher;

/**
 * The default parser of the {@link TrackElement}.
 * <p>
 * This element is represented by a single line which consists of a letter
 * {@code 'P'} followed by a number of the player, e.g. {@code "P 2"}.
 */
public class TrackParser extends SongElementParser {

	private static final String TRACK_PATTERN = "P ([0-9]+)";

	public TrackParser() {
		super(TRACK_PATTERN);
	}

	@Override
	public VisitableSongElement createElement(Matcher matcher) {
		int number = Integer.parseInt(matcher.group(1));
		return new TrackElement(number);
	}

}
