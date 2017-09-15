package main.java.com.github.nianna.karedi.parser.elementparser;

import java.util.regex.Matcher;

import main.java.com.github.nianna.karedi.parser.element.TrackElement;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;

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
	public VisitableSongElement createElement(Matcher m) {
		int number = Integer.parseInt(m.group(1));
		return new TrackElement(number);
	}

}
