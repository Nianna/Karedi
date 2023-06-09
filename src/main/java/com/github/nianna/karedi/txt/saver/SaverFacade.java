package com.github.nianna.karedi.txt.saver;

import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.txt.clipboard.ClipboardHelper;
import com.github.nianna.karedi.txt.parser.Unparser;

import java.io.File;
import java.util.List;

public class SaverFacade {

    private final ClipboardHelper clipboardHelper;

    private final SongSaver songSaver;

    private final NotesSaverHelper notesSaverHelper;

    public SaverFacade(Unparser unparser, ClipboardHelper clipboardHelper) {
        this.clipboardHelper = clipboardHelper;
        SongDisassembler songDisassembler = new SongDisassembler();
        songSaver = new SongSaver(unparser, songDisassembler);
        notesSaverHelper = new NotesSaverHelper(unparser, songDisassembler);
    }

    public boolean saveSongToFile(File file, Song song) {
        return songSaver.saveSongToFile(file, song);
    }

    public boolean exportSongToFile(File file, List<Tag> tags, List<SongTrack> tracks) {
        return songSaver.exportToFile(file, tags, tracks);
    }

    public void saveToClipboard(List<Note> notes) {
        clipboardHelper.writeLines(notesSaverHelper.toUnparsedRepresentation(notes));
    }

}
