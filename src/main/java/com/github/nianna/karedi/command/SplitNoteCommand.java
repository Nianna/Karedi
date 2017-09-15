package main.java.com.github.nianna.karedi.command;

import javafx.util.Pair;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class SplitNoteCommand extends CommandComposite {
	private Note note;
	private int splitPoint;

	public SplitNoteCommand(Note note, int splitPoint) {
		super(I18N.get("command.split_note"));
		this.note = note;
		this.splitPoint = splitPoint;
	}

	@Override
	protected void buildSubCommands() {
		if (canExecute(note, splitPoint)) {
			Pair<String, String> newLyrics = LyricsHelper.split(note.getLyrics());
			Note newNote = new Note(note.getStart() + splitPoint, note.getLength() - splitPoint,
					note.getTone(), newLyrics.getValue(), note.getType());
			addSubCommand(new ChangeLengthCommand(note, splitPoint - 1));
			addSubCommand(new ChangeLyricsCommand(note, newLyrics.getKey()));
			addSubCommand(new AddNoteCommand(newNote, note.getLine()));
		}
	}

	public static boolean canExecute(Note note, int splitPoint) {
		boolean noteValid = note != null && note.getLine() != null;
		if (noteValid) {
			boolean splitPointValid = splitPoint > 1 && splitPoint < note.getLength();
			return splitPointValid;
		}
		return false;
	}

}
