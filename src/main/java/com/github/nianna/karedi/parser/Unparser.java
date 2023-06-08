package com.github.nianna.karedi.parser;

import com.github.nianna.karedi.parser.element.VisitableSongElement;

/**
 * Creates string representations of song's elements.
 * <p>
 * Dual to {@link Parser}.
 */
public interface Unparser {

	String unparse(VisitableSongElement element);

}
