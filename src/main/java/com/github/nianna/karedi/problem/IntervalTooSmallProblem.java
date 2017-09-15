package main.java.com.github.nianna.karedi.problem;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.SongLine;

public class IntervalTooSmallProblem extends IntBoundedProblem {
	public static final String TITLE = I18N.get("problem.line.interval_too_small.title");
	public static final int MIN_INTERVAL = 3;

	public IntervalTooSmallProblem(SongLine first, SongLine second) {
		super(Severity.WARNING, TITLE, first.getTrack(), new NoteIntervalProblemSolver(
				first.getLatestEndingNote(), second.getFirst(), MIN_INTERVAL), first, second);
		setDescription(I18N.get("problem.line.interval_too_small.description", MIN_INTERVAL));
	}

}
