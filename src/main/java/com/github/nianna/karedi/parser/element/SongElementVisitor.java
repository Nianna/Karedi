package com.github.nianna.karedi.parser.element;

public interface SongElementVisitor {

	void visit(LineBreakElement lineBreakElement);

	void visit(NoteElement noteElement);

	void visit(TagElement tagElement);

	void visit(TrackElement trackElement);

	void visit(EndOfSongElement endOfSongElement);

}
