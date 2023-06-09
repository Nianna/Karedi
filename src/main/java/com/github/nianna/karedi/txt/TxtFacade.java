package com.github.nianna.karedi.txt;

import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.txt.clipboard.ClipboardHelper;
import com.github.nianna.karedi.txt.loader.LoaderFacade;
import com.github.nianna.karedi.txt.parser.ParsingFactory;
import com.github.nianna.karedi.txt.saver.SaverFacade;

import java.io.File;
import java.util.List;

public class TxtFacade {

    private final LoaderFacade loaderFacade;

    private final SaverFacade saverFacade;

    // for testing purposes only
    TxtFacade(ClipboardHelper clipboardHelper) {
        loaderFacade = new LoaderFacade(ParsingFactory.createParser(), clipboardHelper);
        saverFacade = new SaverFacade(ParsingFactory.createUnparser(), clipboardHelper);
    }

    public TxtFacade() {
        this(new ClipboardHelper());
    }

    public Song loadFromTxtFile(File file) {
        return loaderFacade.loadSongFromFile(file);
    }

    public Song loadFromClipboard() {
        return loaderFacade.loadSongFromClipboard();
    }

    public boolean saveSongToFile(File file, Song song) {
        return saverFacade.saveSongToFile(file, song);
    }

    public boolean exportSongToFile(File file, List<Tag> tags, List<SongTrack> tracks) {
        return saverFacade.exportSongToFile(file, tags, tracks);
    }

    public void saveToClipboard(List<Note> notes) {
        saverFacade.saveToClipboard(notes);
    }

}
