package com.github.nianna.karedi.txt.saver;

import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.txt.parser.ParsingFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SongSaverTest {

    @TempDir
    public Path tmpFolder;

    private final SongDisassembler songDisassembler = new SongDisassembler();

    private final SongSaver songSaver = new SongSaver(ParsingFactory.createUnparser(), songDisassembler);

    @Test
    public void shouldSaveEmptySong() throws IOException {
        File outputFile = newTmpFile("output.txt");

        boolean saved = songSaver.saveSongToFile(outputFile, new Song());

        assertTrue(saved);
        assertEquals(List.of("E"), Files.readAllLines(outputFile.toPath()));
    }

    @Test
    public void shouldFixMultiplayerTagsBeforeSaving() throws IOException {
        File outputFile = newTmpFile("output.txt");

        boolean saved = songSaver.saveSongToFile(outputFile, prepareSongWithTagsInconsistentWithTrackNames());

        assertTrue(saved);
        assertEquals(expectedMultiplayerFileLines(), Files.readAllLines(outputFile.toPath()));
    }

    @Test
    public void shouldFixMultiplayerTagsBeforeExporting() throws IOException {
        File outputFile = newTmpFile("output.txt");

        Song song = prepareSongWithTagsInconsistentWithTrackNames();
        boolean exported = songSaver.exportToFile(outputFile, song.getTags(), song.getTracks());

        assertTrue(exported);
        assertEquals(expectedMultiplayerFileLines(), Files.readAllLines(outputFile.toPath()));
    }

    @Test
    public void shouldReturnFalseIfFileIsNullWhileSaving() {
        boolean saved = songSaver.saveSongToFile(null, new Song());

        Assertions.assertFalse(saved);
    }

    @Test
    public void shouldReturnFalseIfFileIsNullWhileExporting() {
        boolean exported = songSaver.exportToFile(null, List.of(), List.of());

        Assertions.assertFalse(exported);
    }

    @Test
    public void shouldReturnFalseIfFileNotFoundWhileSaving() {
        File fileThatCanNotBeCreated = new File("not-existing-parent", "outputFile");

        boolean saved = songSaver.saveSongToFile(fileThatCanNotBeCreated, new Song());

        Assertions.assertFalse(saved);
    }

    @Test
    public void shouldReturnFalseIfFileNotFoundWhileExporting() {
        File fileThatCanNotBeCreated = new File("not-existing-parent", "outputFile");

        boolean exported = songSaver.exportToFile(fileThatCanNotBeCreated, List.of(), List.of());

        Assertions.assertFalse(exported);
    }

    private Song prepareSongWithTagsInconsistentWithTrackNames() {
        Song song = new Song();
        song.addTag(new Tag("DUETSINGERP1", "Not Elvis"));
        SongTrack firstTrack = new SongTrack(1);
        firstTrack.setName("Elvis Presley");
        SongTrack secondTrack = new SongTrack(2);
        secondTrack.setName("Freddie Mercury");
        song.addTrack(firstTrack);
        song.addTrack(secondTrack);
        return song;
    }

    private List<String> expectedMultiplayerFileLines() {
        return List.of(
                "#DUETSINGERP1:Elvis Presley",
                "#DUETSINGERP2:Freddie Mercury",
                "P 1",
                "P 2",
                "E"
        );
    }

    private File newTmpFile(String fileName) throws IOException {
        Path inputFile = Files.createFile(tmpFolder.resolve(fileName));
        return inputFile.toFile();
    }
}