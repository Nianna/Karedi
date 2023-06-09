package com.github.nianna.karedi.txt.saver;

import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.txt.parser.ParsingFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class SongSaverTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private final SongDisassembler songDisassembler = new SongDisassembler();

    private final SongSaver songSaver = new SongSaver(ParsingFactory.createUnparser(), songDisassembler);

    @Test
    public void shouldSaveEmptySong() throws IOException {
        File outputFile = tmpFolder.newFile("output.txt");

        boolean saved = songSaver.saveSongToFile(outputFile, new Song());

        Assert.assertTrue(saved);
        Assert.assertEquals(List.of("E"), Files.readAllLines(outputFile.toPath()));
    }

    @Test
    public void shouldFixMultiplayerTagsBeforeSaving() throws IOException {
        File outputFile = tmpFolder.newFile("output.txt");

        boolean saved = songSaver.saveSongToFile(outputFile, prepareSongWithTagsInconsistentWithTrackNames());

        Assert.assertTrue(saved);
        Assert.assertEquals(expectedMultiplayerFileLines(), Files.readAllLines(outputFile.toPath()));
    }

    @Test
    public void shouldFixMultiplayerTagsBeforeExporting() throws IOException {
        File outputFile = tmpFolder.newFile("output.txt");

        Song song = prepareSongWithTagsInconsistentWithTrackNames();
        boolean exported = songSaver.exportToFile(outputFile, song.getTags(), song.getTracks());

        Assert.assertTrue(exported);
        Assert.assertEquals(expectedMultiplayerFileLines(), Files.readAllLines(outputFile.toPath()));
    }

    @Test
    public void shouldReturnFalseIfFileIsNullWhileSaving() {
        boolean saved = songSaver.saveSongToFile(null, new Song());

        Assert.assertFalse(saved);
    }

    @Test
    public void shouldReturnFalseIfFileIsNullWhileExporting() {
        boolean exported = songSaver.exportToFile(null, List.of(), List.of());

        Assert.assertFalse(exported);
    }

    @Test
    public void shouldReturnFalseIfFileNotFoundWhileSaving() {
        File fileThatCanNotBeCreated = new File("not-existing-parent", "outputFile");

        boolean saved = songSaver.saveSongToFile(fileThatCanNotBeCreated, new Song());

        Assert.assertFalse(saved);
    }

    @Test
    public void shouldReturnFalseIfFileNotFoundWhileExporting() {
        File fileThatCanNotBeCreated = new File("not-existing-parent", "outputFile");

        boolean exported = songSaver.exportToFile(fileThatCanNotBeCreated, List.of(), List.of());

        Assert.assertFalse(exported);
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
}