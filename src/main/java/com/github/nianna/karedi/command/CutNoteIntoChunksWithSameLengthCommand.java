package com.github.nianna.karedi.command;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.util.LyricsHelper;

import java.util.List;

public class CutNoteIntoChunksWithSameLengthCommand extends CommandComposite {
    private final Note note;
    private final int length;

    public CutNoteIntoChunksWithSameLengthCommand(Note note, int length) {
        super(I18N.get("command.cut_note_into_chunks", String.valueOf(length)));
        this.note = note;
        this.length = length;
    }

    @Override
    protected void buildSubCommands() {
        if (canExecute(note, length)) {
            int targetNoteCount = (int) Math.ceil((double) note.getLength() / length);
            int lastNoteLength = note.getLength() - (targetNoteCount - 1) * length;
            List<String> newNotesLyrics = LyricsHelper.split(note.getLyrics(), targetNoteCount);
            for (int i = 1; i < targetNoteCount; i++) {
                Note newNote = new Note(note.getStart() + i * (length),
                        i == targetNoteCount - 1 ? lastNoteLength : length,
                        note.getTone(),
                        newNotesLyrics.get(i),
                        note.getType()
                );
                addSubCommand(new AddNoteCommand(newNote, note.getLine()));
            }

            addSubCommand(new ChangeLyricsCommand(note, newNotesLyrics.get(0)));
            addSubCommand(new ChangeLengthCommand(note, length));
        }
    }

    public static boolean canExecute(Note note, int splitPoint) {
        boolean noteValid = note != null && note.getLine() != null;
        if (noteValid) {
            return isSplitPointValid(note, splitPoint);
        }
        return false;
    }

    private static boolean isSplitPointValid(Note note, int splitPoint) {
        return splitPoint > 0 && splitPoint < note.getLength();
    }

}
