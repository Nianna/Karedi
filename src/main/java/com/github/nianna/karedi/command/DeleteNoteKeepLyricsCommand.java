package main.java.com.github.nianna.karedi.command;

import java.util.Optional;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class DeleteNoteKeepLyricsCommand extends CommandComposite {
	private Note note;

	public DeleteNoteKeepLyricsCommand(Note note) {
		super(I18N.get("command.delete_note_keep_lyrics"));
		this.note = note;
	}

	@Override
	protected void buildSubCommands() {
		Optional<Note> prevNote = note.getPrevious();
		Optional<Note> nextNote = note.getNext();

		if (!nextNote.isPresent() && !prevNote.isPresent()) {
			// This note is the last one, do nothing
			return;
		}

		if (nextNote.isPresent() && (!prevNote.isPresent() || joinWithNext(nextNote.get()))) {
			addSubCommand(new ChangeLyricsCommand(nextNote.get(),
					LyricsHelper.join(note.getLyrics(), nextNote.get().getLyrics())));
		} else {
			addSubCommand(new ChangeLyricsCommand(prevNote.get(),
					LyricsHelper.join(prevNote.get().getLyrics(), note.getLyrics())));
		}
		addSubCommand(new DeleteNoteCommand(note));
	}

	private boolean joinWithNext(Note nextNote) {
		return nextNote.getLine() == note.getLine() && LyricsHelper.startsNewWord(note.getLyrics());
	}
}
