package main.java.com.github.nianna.karedi.parser.element;

public class TagElement implements VisitableSongElement {
	private final String key;
	private final String value;

	public TagElement(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

	@Override
	public void accept(SongElementVisitor visitor) {
		visitor.visit(this);
	}

}
