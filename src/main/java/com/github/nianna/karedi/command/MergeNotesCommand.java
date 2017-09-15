package main.java.com.github.nianna.karedi.command;

import java.util.List;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;

public class MergeNotesCommand extends CommandComposite {
	public static enum MergeMode {
		TONES,
		SYNCHRO,
		LYRICS,
		TONES_SYNCHRO,
		TONES_LYRICS,
		SYNCHRO_LYRICS,
		TONES_SYNCHRO_LYRICS;

		private static boolean setSynchro(MergeMode mode) {
			switch (mode) {
			case SYNCHRO:
			case TONES_SYNCHRO:
			case SYNCHRO_LYRICS:
			case TONES_SYNCHRO_LYRICS:
				return true;
			default:
				return false;
			}
		}

		private static boolean setTones(MergeMode mode) {
			switch (mode) {
			case TONES:
			case TONES_SYNCHRO:
			case TONES_LYRICS:
			case TONES_SYNCHRO_LYRICS:
				return true;
			default:
				return false;
			}
		}

		private static boolean setLyrics(MergeMode mode) {
			switch (mode) {
			case LYRICS:
			case SYNCHRO_LYRICS:
			case TONES_LYRICS:
			case TONES_SYNCHRO_LYRICS:
				return true;
			default:
				return false;
			}
		}
	}

	private List<? extends Note> sourceNotes;
	private List<Note> targetNotes;
	private MergeMode mode;

	public MergeNotesCommand(List<Note> targetNotes, List<? extends Note> sourceNotes,
			MergeMode mode) {
		super(I18N.get("command.merge"));
		this.targetNotes = targetNotes;
		this.sourceNotes = sourceNotes;
		this.mode = mode;
	}

	@Override
	protected void buildSubCommands() {
		int toMerge = Math.min(targetNotes.size(), sourceNotes.size());
		if (toMerge > 0) {
			int sourceStartBeat = sourceNotes.get(0).getStart();
			int targetStartBeat = targetNotes.get(0).getStart();
			for (int i = 0; i < toMerge; ++i) {
				Note tNote = targetNotes.get(i);
				Note sNote = sourceNotes.get(i);
				if (MergeMode.setSynchro(mode)) {
					int beatOffset = sNote.getStart() - sourceStartBeat;
					addSubCommand(new ChangeStartBeatCommand(tNote, targetStartBeat + beatOffset));
					addSubCommand(new ChangeLengthCommand(tNote, sNote.getLength()));
				}
				if (MergeMode.setTones(mode)) {
					addSubCommand(new ChangeToneCommand(tNote, sNote.getTone()));
				}
				if (MergeMode.setLyrics(mode)) {
					addSubCommand(new ChangeLyricsCommand(tNote, sNote.getLyrics()));
				}
			}
		}
	}

}
