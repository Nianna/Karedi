package main.java.com.github.nianna.karedi.problem;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.tag.TagKey;

public class NotesBeforeStartProblem extends BoundaryTagProblem {
	public static final String TITLE = I18N.get("problem.note.before_start.title");

	public NotesBeforeStartProblem(Song song, long startMillis) {
		super(Severity.ERROR, TITLE, TagKey.START, song, song.getLowerXBound(),
				song.millisToBeat(startMillis));
		setDescription(I18N.get("problem.note.before_start.description"));
	}

}
