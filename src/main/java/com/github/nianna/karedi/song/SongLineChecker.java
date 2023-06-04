package com.github.nianna.karedi.song;

import javafx.collections.ObservableList;
import com.github.nianna.karedi.problem.ConnectedNotesProblem;
import com.github.nianna.karedi.problem.OverlappingNotesProblem;
import com.github.nianna.karedi.problem.Problem;
import com.github.nianna.karedi.problem.Problematic;
import com.github.nianna.karedi.problem.ProblemsCombiner;
import com.github.nianna.karedi.util.ListenersUtils;

public class SongLineChecker implements Problematic {
	private ProblemsCombiner combiner;

	public SongLineChecker(SongLine line) {
		line.getNotes().addListener(ListenersUtils.createListChangeListener(this::refreshNote,
				this::refreshNote, this::refreshNote, this::onNoteRemoved));
		combiner = new ProblemsCombiner(line.getNotes());
	}

	private void removeNoteProblems(Note note) {
		combiner.removeIf(problem -> {
			return problem.getElements().contains(note);
		});
	}

	private void onNoteRemoved(Note note) {
		removeNoteProblems(note);
		note.getPreviousInLine().ifPresent(prevNote -> findProblems(prevNote));
	}

	private void refreshNote(Note note) {
		removeNoteProblems(note);
		findProblems(note);
		note.getPreviousInLine().ifPresent(prevNote -> findProblems(prevNote));
	}

	private void findProblems(Note note) {
		note.getNextInLine().ifPresent(nextNote -> {
			int noteEndBeat = note.getUpperXBound();
			int nextNoteStartBeat = nextNote.getLowerXBound();
			int beatInterval = nextNoteStartBeat - noteEndBeat;
			if (beatInterval == 0) {
				combiner.add(new ConnectedNotesProblem(note, nextNote));
			}
			if (beatInterval < 0) {
				combiner.add(new OverlappingNotesProblem(note, nextNote));
			}
		});
	}

	@Override
	public ObservableList<Problem> getProblems() {
		return combiner.getProblems();
	}

}
