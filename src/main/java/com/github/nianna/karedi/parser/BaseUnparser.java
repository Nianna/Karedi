package com.github.nianna.karedi.parser;

import com.github.nianna.karedi.parser.element.EndOfSongElement;
import com.github.nianna.karedi.parser.element.LineBreakElement;
import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.SongElementVisitor;
import com.github.nianna.karedi.parser.element.TagElement;
import com.github.nianna.karedi.parser.element.TrackElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.parser.elementunparser.EndOfSongElementUnparser;
import com.github.nianna.karedi.parser.elementunparser.LineBreakElementUnparser;
import com.github.nianna.karedi.parser.elementunparser.NoteElementUnparser;
import com.github.nianna.karedi.parser.elementunparser.TagElementUnparser;
import com.github.nianna.karedi.parser.elementunparser.TrackElementUnparser;

/**
 * Generates String representations of {@link VisitableSongElement}s.
 * <p>
 * Uses default unparsers, but all of them can be substituted.
 */
class BaseUnparser implements Unparser, SongElementVisitor {

	protected String result;

	private final Unparser tagElementUnparser = new TagElementUnparser();

	private final Unparser noteElementUnparser = new NoteElementUnparser();

	private final Unparser lineBreakElementUnparser = new LineBreakElementUnparser();

	private final Unparser endOfSongElementUnparser = new EndOfSongElementUnparser();

	private final Unparser trackElementUnparser = new TrackElementUnparser();

	@Override
	public String unparse(VisitableSongElement element) {
		result = null;
		element.accept(this);
		return result;
	}

	@Override
	public void visit(LineBreakElement element) {
		result = lineBreakElementUnparser.unparse(element);
	}

	@Override
	public void visit(NoteElement element) {
		result = noteElementUnparser.unparse(element);
	}

	@Override
	public void visit(TagElement element) {
		result = tagElementUnparser.unparse(element);
	}

	@Override
	public void visit(TrackElement element) {
		result = trackElementUnparser.unparse(element);
	}

	@Override
	public void visit(EndOfSongElement element) {
		result = endOfSongElementUnparser.unparse(element);
	}

}
