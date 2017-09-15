package main.java.com.github.nianna.karedi.problem;

import java.util.Optional;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.ChangeLyricsCommand;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandComposite;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class NotNormalizedNoteLyricsProblem extends IntBoundedProblem {
	public static final String TITLE = I18N.get("problem.note.not_normalized_lyrics.title");
	private Note note;

	public NotNormalizedNoteLyricsProblem(Note note) {
		super(Severity.WARNING, TITLE, note.getTrack(), note);
		this.note = note;
		setDescription(I18N.get("problem.note.not_normalized_lyrics.description"));
	}

	@Override
	public Optional<Command> getSolution() {
		String lyrics = note.getLyrics();
		if (lyrics.trim().equals(LyricsHelper.normalize(lyrics).trim())) {
			return rename(getInvasiveSolution(), false);
		}
		return super.getSolution();
	}

	@Override
	public Optional<Command> getInvasiveSolution() {
		return rename(Optional.of(new CommandComposite("") {

			@Override
			protected void buildSubCommands() {
				addSubCommand(
						new ChangeLyricsCommand(note, LyricsHelper.normalize(note.getLyrics())));
				if (note.getLyrics().endsWith(LyricsHelper.WORD_SEPARATOR + "")) {
					note.getNextInLine()
							.filter(nextNote -> !LyricsHelper.startsNewWord(nextNote.getLyrics()))
							.ifPresent(nextNote -> {
								addSubCommand(new ChangeLyricsCommand(nextNote,
										LyricsHelper.WORD_SEPARATOR + nextNote.getLyrics()));
							});
				}
			}
		}), true);
	}

}
