package main.java.com.github.nianna.karedi.parser.element;

public interface SongElementVisitor {
	public void visit(LineBreakElement lineBreakElement);
	public void visit(NoteElement noteElement);
	public void visit(TagElement tagElement);
	public void visit(TrackElement trackElement);
	public void visit(EndOfSongElement endOfSongElement);
}
