package main.java.com.github.nianna.karedi.parser.elementparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.com.github.nianna.karedi.parser.Parser;
import main.java.com.github.nianna.karedi.parser.element.InvalidSongElementException;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;

/**
 * The base class for all parsers that are responsible for recognizing a
 * particular song element based on the appropriate {@link Pattern}.
 * <p>
 * They can be linked to each other to form a chain.
 */
public abstract class SongElementParser implements Parser {
	private SongElementParser nextParser;
	private String pattern;

	public SongElementParser(String pattern) {
		this.pattern = pattern;
	}

	public final void addNext(SongElementParser parser) {
		if (nextParser == null) {
			nextParser = parser;
		} else {
			nextParser.addNext(parser);
		}
	}

	@Override
	public final VisitableSongElement parse(String line) throws InvalidSongElementException {
		Pattern tagPattern = Pattern.compile(pattern);
		Matcher m = tagPattern.matcher(line);
		if (m.matches()) {
			return createElement(m);
		} else {
			if (nextParser == null) {
				throw new InvalidSongElementException(line);
			} else {
				return nextParser.parse(line);
			}
		}
	}

	public abstract VisitableSongElement createElement(Matcher m);

}
