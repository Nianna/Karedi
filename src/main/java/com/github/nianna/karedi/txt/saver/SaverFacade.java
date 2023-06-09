package com.github.nianna.karedi.txt.saver;

import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.txt.parser.Unparser;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class SaverFacade {

    private final SongSaver songSaver;

    private final NotesSaverHelper notesSaverHelper;

    public SaverFacade(Unparser unparser) {
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
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        String result = notesSaverHelper.toUnparsedRepresentation(notes)
                .collect(Collectors.joining(System.lineSeparator()));
        content.putString(result);
        clipboard.setContent(content);
    }

}
