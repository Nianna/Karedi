package com.github.nianna.karedi.parser.element;

public record NoteElement(NoteElementType type,
						  int startsAt,
						  int length,
						  int tone,
						  String lyrics) implements VisitableSongElement {

	@Override
	public void accept(SongElementVisitor visitor) {
		visitor.visit(this);
	}
}
