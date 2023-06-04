package com.github.nianna.karedi.parser;

import com.github.nianna.karedi.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.parser.element.VisitableSongElement;

/**
 * Creates abstract song elements from correctly formatted Strings.
 * <p>
 * Dual to {@link Unparser}.
 */
public interface Parser {
	public VisitableSongElement parse(String fileLine) throws InvalidSongElementException;
}
