package main.java.com.github.nianna.karedi.command;

import java.util.stream.Collectors;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class DeleteLineKeepLyricsCommand extends CommandComposite {

	private SongLine line;

	public DeleteLineKeepLyricsCommand(SongLine line) {
		super(I18N.get("command.delete_line_keep_lyrics"));
		this.line = line;
	}

	@Override
	protected void buildSubCommands() {
		boolean hasPrevious = line.getPrevious().isPresent();
		boolean hasNext = line.getNext().isPresent();
		String lineLyrics = LyricsHelper
				.join(line.getNotes().stream().map(Note::getLyrics).collect(Collectors.toList()));
		if (!hasNext && !hasPrevious) {
			addSubCommand(new ChangeLyricsCommand(line.getFirst(), lineLyrics));
			for (int i = 1; i < line.size(); ++i) {
				addSubCommand(new DeleteNoteCommand(line.get(i)));
			}
		} else {
			if (hasPrevious) {
				Note lastNote = line.getPrevious().get().getLast();
				addSubCommand(new ChangeLyricsCommand(lastNote,
						LyricsHelper.join(lastNote.getLyrics(), lineLyrics)));
			} else {
				Note firstNote = line.getNext().get().getFirst();
				addSubCommand(new ChangeLyricsCommand(firstNote,
						LyricsHelper.join(lineLyrics, firstNote.getLyrics())));
			}
			addSubCommand(new DeleteLineCommand(line));
		}
	}

}
