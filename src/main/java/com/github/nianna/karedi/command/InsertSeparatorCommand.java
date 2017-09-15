package main.java.com.github.nianna.karedi.command;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;

public abstract class InsertSeparatorCommand extends CommandComposite {
	private Note note;
	private int divisionPoint;
	private String lyrics;
	private String separator;

	public InsertSeparatorCommand(Note note, int divisionPoint, String separator) {
		super(I18N.get("command.insert_separator"));
		assert (note != null);
		this.note = note;
		this.divisionPoint = divisionPoint;
		this.separator = separator;
	}

	@Override
	protected void buildSubCommands() {
		lyrics = note.getLyrics();
		if (divisionPoint > 0 && divisionPoint <= lyrics.length()) {
			if (divisionPoint == lyrics.length()) {
				toggleSeparator();
			} else {
				// insertPoint < note.getLyrics().length()
				splitIntoSyllables();
			}
		}
	}

	protected abstract boolean needsToggling(String text);

	private void toggleSeparator() {
		note.getNext().ifPresent(nextNote -> {
			if (needsToggling(nextNote.getLyrics())) {
				addSubCommand(
						new ChangeLyricsCommand(nextNote, separator + nextNote.getLyrics().trim()));
			}
		});
	}

	private void splitIntoSyllables() {
		String firstPart = lyrics.substring(0, divisionPoint);
		String secondPart = separator + lyrics.substring(divisionPoint).trim();
		if (firstPart.trim().length() > 0 && secondPart.trim().length() > 0) {
			note.getNext().ifPresent(nextNote -> {
				addSubCommand(new RollLyricsRightCommand(nextNote));
				addSubCommand(new ChangeLyricsCommand(nextNote, secondPart));
			});
			addSubCommand(new ChangeLyricsCommand(note, firstPart));
		}
	}

}
