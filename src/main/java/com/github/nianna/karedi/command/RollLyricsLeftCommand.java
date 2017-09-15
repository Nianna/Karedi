package main.java.com.github.nianna.karedi.command;

import java.util.List;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class RollLyricsLeftCommand extends CommandComposite {
	private List<Note> notes;
	private int by;

	public RollLyricsLeftCommand(List<Note> notes, int by) {
		super(I18N.get("command.roll_left"));
		this.notes = notes;
		this.by = by;
	}

	public RollLyricsLeftCommand(Note note, int by) {
		this(note.getLine().getTrack().getNotes(note), by);
	}

	public RollLyricsLeftCommand(List<Note> notes) {
		this(notes, 1);
	}

	public RollLyricsLeftCommand(Note note) {
		this(note, 1);
	}

	@Override
	protected void buildSubCommands() {
		if (notes.size() > by) {
			String newFirstNoteLyrics = notes.get(0).getLyrics();
			for (int i = 1; i <= by; ++i) {
				newFirstNoteLyrics = LyricsHelper.join(newFirstNoteLyrics,
						notes.get(i).getLyrics());
			}
			addSubCommand(new ChangeLyricsCommand(notes.get(0), newFirstNoteLyrics));
			for (int i = 1; i < notes.size(); ++i) {
				if (i < notes.size() - by) {
					addSubCommand(
							new ChangeLyricsCommand(notes.get(i), notes.get(i + by).getLyrics()));
				} else {
					addSubCommand(new ChangeLyricsCommand(notes.get(i),
							String.valueOf(LyricsHelper.EMPTY_LYRICS)));
				}
			}
		}
	}

}
