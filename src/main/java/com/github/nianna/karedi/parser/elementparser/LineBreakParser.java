package main.java.com.github.nianna.karedi.parser.elementparser;

import java.util.regex.Matcher;

import main.java.com.github.nianna.karedi.parser.element.LineBreakElement;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;

/**
 * The default parser of the {@link LineBreakElement}.
 * <p>
 * This element is represented by a single line which consists of a hyphen
 * followed by a number, e.g. {@code "- 24"}. The number denotes the beat in
 * which the line associated with this line break should be changed to the next
 * one (in the game).
 */
public class LineBreakParser extends SongElementParser {
	private static final String LINE_BREAK_PATTERN = "- (-*[0-9]+)";

	public LineBreakParser() {
		super(LINE_BREAK_PATTERN);
	}

	@Override
	public VisitableSongElement createElement(Matcher m) {
		int position = Integer.parseInt(m.group(1));
		return new LineBreakElement(position);
	}

}
