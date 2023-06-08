package com.github.nianna.karedi.parser.element;

public record LineBreakElement(Integer position) implements VisitableSongElement {

	@Override
	public void accept(SongElementVisitor visitor) {
		visitor.visit(this);
	}
}
