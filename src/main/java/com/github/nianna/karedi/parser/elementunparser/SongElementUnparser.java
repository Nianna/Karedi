package main.java.com.github.nianna.karedi.parser.elementunparser;

import main.java.com.github.nianna.karedi.parser.Unparser;
import main.java.com.github.nianna.karedi.parser.element.EndOfSongElement;
import main.java.com.github.nianna.karedi.parser.element.LineBreakElement;
import main.java.com.github.nianna.karedi.parser.element.NoteElement;
import main.java.com.github.nianna.karedi.parser.element.SongElementVisitor;
import main.java.com.github.nianna.karedi.parser.element.TagElement;
import main.java.com.github.nianna.karedi.parser.element.TrackElement;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;

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
