package com.github.nianna.karedi.txt.loader;

import com.github.nianna.karedi.txt.parser.Parser;
import com.github.nianna.karedi.song.Song;
import javafx.scene.input.Clipboard;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class LoaderFacade {

    private final SongLoader songLoader;

    public LoaderFacade(Parser parser) {
        this.songLoader = new SongLoader(parser);
    }

    public Song loadSongFromFile(File file) {
        return songLoader.load(file);
    }

    public Song loadSongFromClipboard() {
        return songLoader.buildSong(getLinesFromClipboard());
    }

    private List<String> getLinesFromClipboard() {
        return Stream.ofNullable(Clipboard.getSystemClipboard())
                .map(Clipboard::getString)
                .map(content -> content.split("\\R"))
                .flatMap(Stream::of)
                .toList();
    }

}
