package com.github.nianna.karedi.txt.parser.element;

public record TagElement(String key, String value) implements VisitableSongElement {

	@Override
	public void accept(SongElementVisitor visitor) {
		visitor.visit(this);
	}

}
