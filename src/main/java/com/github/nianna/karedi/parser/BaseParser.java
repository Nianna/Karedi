package com.github.nianna.karedi.parser;

import com.github.nianna.karedi.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.parser.elementparser.EndOfSongParser;
import com.github.nianna.karedi.parser.elementparser.LineBreakParser;
import com.github.nianna.karedi.parser.elementparser.NoteParser;
import com.github.nianna.karedi.parser.elementparser.SongElementParser;
import com.github.nianna.karedi.parser.elementparser.TagParser;
import com.github.nianna.karedi.parser.elementparser.TrackParser;

/**
 * Creates appropriate {@link VisitableSongElement}s from their string representations.
 * <p>
 * Uses provided chain of parsers or the default one, which consists of a
 * {@link TagParser}, {@link NoteParser}, {@link LineBreakParser},
 * {@link TrackParser} and {@link EndOfSongParser} (in this order).
 * <p>
 * The default parsers recognize only valid input, otherwise
 * {@link InvalidSongElementException} is thrown.
 */
public class BaseParser implements Parser {

	private SongElementParser parserChain;

	public static SongElementParser getDefaultParserChain() {
		SongElementParser chain = new TagParser();
		chain.addNext(new NoteParser());
		chain.addNext(new LineBreakParser());
		chain.addNext(new TrackParser());
		chain.addNext(new EndOfSongParser());
		return chain;
	}

	public BaseParser(SongElementParser parserChain) {
		this.parserChain = parserChain;
	}

	public BaseParser() {
		this(getDefaultParserChain());
	}

	@Override
	public VisitableSongElement parse(String fileLine) throws InvalidSongElementException {
		return parserChain.parse(fileLine);
	}

	public void setParserChain(SongElementParser parserChain) {
		this.parserChain = parserChain;
	}

	public SongElementParser getParserChain() {
		return parserChain;
	}

}
