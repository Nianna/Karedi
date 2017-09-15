package main.java.com.github.nianna.karedi.parser.element;

public class EndOfSongElement implements VisitableSongElement {

	@Override
	public void accept(SongElementVisitor visitor) {
		visitor.visit(this);
	}

}
