package com.github.nianna.karedi.parser.element;

public interface VisitableSongElement {
	void accept(SongElementVisitor visitor);
}
