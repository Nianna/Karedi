package com.github.nianna.karedi.problem;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;

public class ConnectedNotesProblem extends IntBoundedProblem {
	public static final String TITLE = I18N.get("problem.note.connected.title");

	public ConnectedNotesProblem(Note first, Note second) {
		super(Severity.ERROR, TITLE, first.getTrack(),
				new NoteIntervalProblemSolver(first, second, 1), first, second);
		setDescription(I18N.get("problem.note.connected.description"));
	}

}
