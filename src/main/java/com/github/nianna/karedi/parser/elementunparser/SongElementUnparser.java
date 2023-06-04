package com.github.nianna.karedi.parser.elementunparser;

import com.github.nianna.karedi.parser.Unparser;
import com.github.nianna.karedi.parser.element.EndOfSongElement;
import com.github.nianna.karedi.parser.element.LineBreakElement;
import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.SongElementVisitor;
import com.github.nianna.karedi.parser.element.TagElement;
import com.github.nianna.karedi.parser.element.TrackElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;

public abstract class SongElementUnparser implements Unparser, SongElementVisitor {
	protected String result;

	@Override
	public void visit(LineBreakElement lineBreakElement) {
		result = null;
	}

	@Override
	public void visit(NoteElement noteElement) {
		result = null;
	}

	@Override
	public void visit(TagElement tagElement) {
		result = null;
	}

	@Override
	public void visit(TrackElement trackElement) {
		result = null;
	}

	@Override
	public void visit(EndOfSongElement endOfSongElement) {
		result = null;
	}

	@Override
	public String unparse(VisitableSongElement element) {
		element.accept(this);
		return result;
	}

}
