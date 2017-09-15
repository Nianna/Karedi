package main.java.com.github.nianna.karedi.parser;

import main.java.com.github.nianna.karedi.parser.element.EndOfSongElement;
import main.java.com.github.nianna.karedi.parser.element.LineBreakElement;
import main.java.com.github.nianna.karedi.parser.element.NoteElement;
import main.java.com.github.nianna.karedi.parser.element.SongElementVisitor;
import main.java.com.github.nianna.karedi.parser.element.TagElement;
import main.java.com.github.nianna.karedi.parser.element.TrackElement;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;
import main.java.com.github.nianna.karedi.parser.elementunparser.EndOfSongElementUnparser;
import main.java.com.github.nianna.karedi.parser.elementunparser.LineBreakElementUnparser;
import main.java.com.github.nianna.karedi.parser.elementunparser.NoteElementUnparser;
import main.java.com.github.nianna.karedi.parser.elementunparser.TagElementUnparser;
import main.java.com.github.nianna.karedi.parser.elementunparser.TrackElementUnparser;

/**
 * Generates String representations of {@link VisitableSongElement}s.
 * <p>
 * Uses default unparsers, but all of them can be substituted.
 */
public class BaseUnparser implements Unparser, SongElementVisitor {
	protected String result;
	private Unparser tagElementUnparser = new TagElementUnparser();
	private Unparser noteElementUnparser = new NoteElementUnparser();
	private Unparser lineBreakElementUnparser = new LineBreakElementUnparser();
	private Unparser endOfSongElementUnparser = new EndOfSongElementUnparser();
	private Unparser trackElementUnparser = new TrackElementUnparser();

	public Unparser getTagElementUnparser() {
		return tagElementUnparser;
	}

	public void setTagElementUnparser(Unparser tagElementUnparser) {
		this.tagElementUnparser = tagElementUnparser;
	}

	public Unparser getNoteElementUnparser() {
		return noteElementUnparser;
	}

	public void setNoteElementUnparser(Unparser noteElementUnparser) {
		this.noteElementUnparser = noteElementUnparser;
	}

	public Unparser getLineBreakElementUnparser() {
		return lineBreakElementUnparser;
	}

	public void setLineBreakElementUnparser(Unparser lineBreakElementUnparser) {
		this.lineBreakElementUnparser = lineBreakElementUnparser;
	}

	public Unparser getEndOfSongElementUnparser() {
		return endOfSongElementUnparser;
	}

	public void setEndOfSongElementUnparser(Unparser endOfSongElementUnparser) {
		this.endOfSongElementUnparser = endOfSongElementUnparser;
	}

	public Unparser getTrackElementUnparser() {
		return trackElementUnparser;
	}

	public void setTrackElementUnparser(Unparser trackElementUnparser) {
		this.trackElementUnparser = trackElementUnparser;
	}

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
