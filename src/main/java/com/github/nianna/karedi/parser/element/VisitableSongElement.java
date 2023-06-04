package com.github.nianna.karedi.parser.element;

public interface VisitableSongElement {
	public void accept(SongElementVisitor visitor);
}
