package main.java.com.github.nianna.karedi.problem;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.SongLine;

public class OverlappingLinesProblem extends IntBoundedProblem {
	public static final String TITLE = I18N.get("problem.line.overlapping.title");

	public OverlappingLinesProblem(SongLine first, SongLine second) {
		super(Severity.ERROR, TITLE, first.getTrack(),
				new NoteIntervalProblemSolver(first.getLatestEndingNote(), second.getFirst(), 1),
				first, second);
		setDescription(I18N.get("problem.line.overlapping.description"));
	}

}