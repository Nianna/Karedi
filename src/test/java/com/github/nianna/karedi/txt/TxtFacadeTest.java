package com.github.nianna.karedi.txt;

import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.txt.clipboard.DummyClipboardHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TxtFacadeTest {

    @TempDir
    public Path tmpFolder;

    private final TxtFacade txtFacade = new TxtFacade(new DummyClipboardHelper());

    @Test
    public void shouldSaveSameFileAsWasLoadedIfItIsValid() throws IOException {
        File inputFile = prepareFile("validSongFile.txt");
        File outputFile = newTmpFile("output.txt");

        Song song = txtFacade.loadFromTxtFile(inputFile);
        txtFacade.saveSongToFile(outputFile, song);

        assertEquals(Files.readAllLines(inputFile.toPath()), Files.readAllLines(outputFile.toPath()));
    }

    @Test
    public void shouldSaveValidCp1250FileAsUtf8File() throws IOException {
        File cp1250File = prepareFile("polishSongFile_cp1250.txt");
        File expectedUtf8File = prepareFile("polishSongFile_utf8.txt");
        File outputFile = newTmpFile("output.txt");

        Song song = txtFacade.loadFromTxtFile(cp1250File);
        txtFacade.saveSongToFile(outputFile, song);

        assertEquals(Files.readAllLines(expectedUtf8File.toPath()), Files.readAllLines(outputFile.toPath()));
    }

    @Test
    public void shouldReadSameNotesAsWereSavedInTheClipboard() throws IOException {
        File inputFile = prepareFile("polishSongFile_utf8.txt");

        Song song = txtFacade.loadFromTxtFile(inputFile);
        txtFacade.saveToClipboard(song.getTrack(0).getNotes());
        Song readSong = txtFacade.loadFromClipboard();

        assertSameTracks(song, readSong);
    }

    private void assertSameTracks(Song expectedSong, Song actualSong) {
        assertEquals(expectedSong.size(), actualSong.size());
        IntStream.range(0, expectedSong.size())
                .forEach(index -> assertSameTrack(expectedSong.getTrack(index), actualSong.getTrack(index)));
    }

    private void assertSameTrack(SongTrack expectedTrack, SongTrack actualTrack) {
        assertEquals(expectedTrack.size(), actualTrack.size());
        IntStream.range(0, expectedTrack.size())
                .forEach(index -> assertSameLine(expectedTrack.getLine(index), actualTrack.getLine(index)));
    }

    private void assertSameLine(SongLine expectedLine, SongLine actualLine) {
        assertEquals(expectedLine.getLineBreak(), actualLine.getLineBreak());
        assertEquals(expectedLine.size(), actualLine.size());
        IntStream.range(0, expectedLine.size())
                .forEach(index -> assertSameNote(expectedLine.getNotes().get(index), expectedLine.getNotes().get(index)));
    }

    private void assertSameNote(Note expectedNote, Note actualNote) {
        assertEquals(expectedNote.getStart(), actualNote.getStart());
        assertEquals(expectedNote.getLength(), actualNote.getLength());
        assertEquals(expectedNote.getTone(), actualNote.getTone());
        assertEquals(expectedNote.getLyrics(), actualNote.getLyrics());
        assertEquals(expectedNote.getType(), actualNote.getType());
    }

    private File prepareFile(String resourceFileName) throws IOException {
        File inputFile = newTmpFile(resourceFileName);
        byte[] bytes = getClass().getResourceAsStream(resourceFileName).readAllBytes();
        Files.write(inputFile.toPath(), bytes);
        return inputFile;
    }

    private File newTmpFile(String fileName) throws IOException {
        Path inputFile = Files.createFile(tmpFolder.resolve(fileName));
        return inputFile.toFile();
    }

}