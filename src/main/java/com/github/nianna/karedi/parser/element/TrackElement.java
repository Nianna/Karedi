package com.github.nianna.karedi.parser.element;

public record TrackElement(int number) implements VisitableSongElement {

	@Override
	public void accept(SongElementVisitor visitor) {
		visitor.visit(this);
	}

}
