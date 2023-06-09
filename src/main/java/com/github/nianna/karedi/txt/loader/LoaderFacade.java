package com.github.nianna.karedi.txt.loader;

import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.txt.clipboard.ClipboardHelper;
import com.github.nianna.karedi.txt.parser.Parser;

import java.io.File;

public class LoaderFacade {

    private final SongLoader songLoader;

    private final ClipboardHelper clipboardHelper;

    public LoaderFacade(Parser parser, ClipboardHelper clipboardHelper) {
        this.songLoader = new SongLoader(parser);
        this.clipboardHelper = clipboardHelper;
    }

    public Song loadSongFromFile(File file) {
        return songLoader.load(file);
    }

    public Song loadSongFromClipboard() {
        return songLoader.buildSong(clipboardHelper.readLines().toList());
    }

}
