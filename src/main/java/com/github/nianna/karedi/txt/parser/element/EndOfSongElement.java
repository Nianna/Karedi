package com.github.nianna.karedi.txt.parser.element;

public record EndOfSongElement() implements VisitableSongElement {

	@Override
	public void accept(SongElementVisitor visitor) {
		visitor.visit(this);
	}

}
