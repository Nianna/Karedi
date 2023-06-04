package com.github.nianna.karedi.parser.element;

public class TrackElement implements VisitableSongElement {
	private final Integer number;

	public TrackElement(Integer number) {
		this.number = number;
	}

	public Integer getNumber() {
		return number;
	}

	@Override
	public void accept(SongElementVisitor visitor) {
		visitor.visit(this);
	}

}
