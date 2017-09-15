package main.java.com.github.nianna.karedi.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.region.Bounded;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class JoinNotesCommand extends CommandComposite {
	private List<Note> notes;
	private Bounded<Integer> bounds;
	private Note outcome;

	public JoinNotesCommand(List<Note> args) {
		super(I18N.get("command.join_notes"));
		notes = args.stream().filter(this::isValid).collect(Collectors.toList());
	}

	private boolean isValid(Note note) {
		return note != null && note.getLine() != null;
	}

	@Override
	protected void buildSubCommands() {
		if (notes.size() > 0) {
			outcome = Collections.min(notes);
		}
		bounds = new BoundingBox<>(this.notes);
		if (outcome != null) {
			int by = bounds.getUpperXBound() - outcome.getEnd();
			addSubCommand(new ResizeNotesCommand(Arrays.asList(outcome), Direction.RIGHT, by));

			addSubCommand(new ChangeLyricsCommand(outcome, getJointLyrics()));

			List<Note> otherNotes = notes.stream()
					.filter(note -> note != outcome)
					.collect(Collectors.toList());
			addSubCommand(new DeleteNotesCommand(otherNotes, false));
		}
	}

	private String getJointLyrics() {
		return LyricsHelper.join(notes.stream().map(Note::getLyrics).collect(Collectors.toList()));
	}

}
