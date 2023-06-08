package com.github.nianna.karedi.parser.elementparser;

import com.github.nianna.karedi.parser.Parser;
import com.github.nianna.karedi.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.parser.element.VisitableSongElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The base class for all parsers that are responsible for recognizing a
 * particular song element based on the appropriate {@link Pattern}.
 * <p>
 * They can be linked to each other to form a chain.
 */
public abstract class SongElementParser implements Parser {

	private SongElementParser nextParser;

	private final String pattern;

	protected SongElementParser(String pattern) {
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
		Matcher matcher = tagPattern.matcher(line);
		if (matcher.matches()) {
			return createElement(matcher);
		} else {
			if (nextParser == null) {
				throw new InvalidSongElementException(line);
			} else {
				return nextParser.parse(line);
			}
		}
	}

	public abstract VisitableSongElement createElement(Matcher matcher);

}
