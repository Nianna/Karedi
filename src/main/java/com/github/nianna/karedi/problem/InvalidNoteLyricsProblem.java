package main.java.com.github.nianna.karedi.problem;

import java.util.Optional;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.ChangeLyricsCommand;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class InvalidNoteLyricsProblem extends IntBoundedProblem {
	public static final String TITLE = I18N.get("problem.note.invalid_lyrics.title");
	private final Note note;

	public InvalidNoteLyricsProblem(Note note) {
		super(Severity.ERROR, TITLE, note.getTrack(), note);
		setDescription(I18N.get("problem.note.invalid_lyrics.description"));
		this.note = note;
	}

	@Override
	public Optional<Command> getInvasiveSolution() {
		return rename(
				Optional.of(
						new ChangeLyricsCommand(note, String.valueOf(LyricsHelper.EMPTY_LYRICS))),
				true);
	}
}
