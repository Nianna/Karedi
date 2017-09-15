package main.java.com.github.nianna.karedi.problem;

import java.util.Arrays;
import java.util.Optional;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.ResizeNotesCommand;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Note;

public class InvalidNoteLengthProblem extends IntBoundedProblem {
	public static final String TITLE = I18N.get("problem.note.invalid_length.title");
	public static final int MIN_LENGTH = 1;
	private Note note;

	public InvalidNoteLengthProblem(Note note) {
		super(Severity.ERROR, TITLE, note.getTrack(), note);
		setDescription(I18N.get("problem.note.invalid_length.description"));
		this.note = note;
	}

	@Override
	public Optional<Command> getSolution() {
		return rename(Optional.of(new ResizeNotesCommand(Arrays.asList(note), Direction.RIGHT, 1)),
				false);
	}

	@Override
	public Optional<? extends IntBounded> getAffectedBounds() {
		return Optional.of(BoundingBox.boundsFrom(note.getStart(), note.getStart() + 1,
				note.getTone(), note.getTone()));
	}

}
