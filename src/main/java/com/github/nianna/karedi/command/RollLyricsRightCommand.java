package main.java.com.github.nianna.karedi.command;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class RollLyricsRightCommand extends CommandComposite {
	private List<Note> notes;
	private int by;

	public RollLyricsRightCommand(List<Note> notes, int by) {
		super(I18N.get("command.roll_right"));
		this.notes = notes;
		this.by = by;
	}

	public RollLyricsRightCommand(Note note, int by) {
		this(note.getLine().getTrack().getNotes(note), by);
	}

	public RollLyricsRightCommand(List<Note> notes) {
		this(notes, 1);
	}

	public RollLyricsRightCommand(Note note) {
		this(note, 1);
	}

	@Override
	protected void buildSubCommands() {
		if (by > 0) {
			addSubCommand(
					new ChangeLyricsCommand(notes.get(notes.size() - 1), computeLastNoteLyrics()));

			int i;
			for (i = notes.size() - 2; i > by; --i) {
				addSubCommand(new ChangeLyricsCommand(notes.get(i), notes.get(i - by).getLyrics()));
			}

			String leftoverLyrics;
			if (i < by) {
				leftoverLyrics = String.valueOf(LyricsHelper.EMPTY_LYRICS);
			} else {
				leftoverLyrics = notes.get(0).getLyrics();
			}

			for (; i > 0; --i) {
				if (LyricsHelper.isSplittable(leftoverLyrics)) {
					Pair<String, String> newLyrics = LyricsHelper.split(leftoverLyrics);
					addSubCommand(new ChangeLyricsCommand(notes.get(i), newLyrics.getValue()));
					leftoverLyrics = newLyrics.getKey();
				} else {
					addSubCommand(new ChangeLyricsCommand(notes.get(i), leftoverLyrics));
					leftoverLyrics = String.valueOf(LyricsHelper.EMPTY_LYRICS);
				}
			}
			addSubCommand(new ChangeLyricsCommand(notes.get(0), leftoverLyrics));
		}
	}

	private String computeLastNoteLyrics() {
		List<String> lyrics = new ArrayList<>();
		for (int i = Math.max(0, notes.size() - 1 - by); i >= 0 && i < notes.size(); ++i) {
			lyrics.add(notes.get(i).getLyrics());
		}
		return LyricsHelper.join(lyrics);
	}

}
