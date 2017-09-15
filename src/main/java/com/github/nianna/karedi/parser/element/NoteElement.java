package main.java.com.github.nianna.karedi.parser.element;

public class NoteElement implements VisitableSongElement {
	public enum Type {
		FREESTYLE,
		GOLDEN,
		NORMAL
	}

	private final Type type;
	private final Integer startsAt;
	private final Integer tone;
	private final Integer length;
	private final String lyrics;

	public NoteElement(Type type, Integer startsAt, Integer length, Integer tone, String lyrics) {
		this.type = type;
		this.startsAt = startsAt;
		this.length = length;
		this.tone = tone;
		this.lyrics = lyrics;
	}

	public Type getType() {
		return type;
	}

	public Integer getStartsAt() {
		return startsAt;
	}

	public Integer getTone() {
		return tone;
	}

	public Integer getLength() {
		return length;
	}

	public String getLyrics() {
		return lyrics;
	}

	@Override
	public void accept(SongElementVisitor visitor) {
		visitor.visit(this);
	}
}
