package main.java.com.github.nianna.karedi.problem;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;

public class OverlappingNotesProblem extends IntBoundedProblem {
	public static final String TITLE = I18N.get("problem.note.overlapping.title");
	
	public OverlappingNotesProblem(Note first, Note second) {
		super(Severity.ERROR, TITLE, first.getTrack(),
				new NoteIntervalProblemSolver(first, second, 1), first, second);
		setDescription(I18N.get("problem.note.overlapping.description"));
	}

}
