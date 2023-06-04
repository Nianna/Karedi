package com.github.nianna.karedi.parser.element;

public class LineBreakElement implements VisitableSongElement {
	private final Integer position;

	public LineBreakElement(Integer position) {
		this.position = position;
	}

	public Integer getPosition() {
		return position;
	}

	@Override
	public void accept(SongElementVisitor visitor) {
		visitor.visit(this);
	}
}
