package com.github.nianna.karedi.txt.saver;

import com.github.nianna.karedi.Settings;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;
import com.github.nianna.karedi.txt.parser.element.LineBreakElement;
import com.github.nianna.karedi.txt.parser.element.NoteElement;
import com.github.nianna.karedi.txt.parser.element.NoteElementType;
import com.github.nianna.karedi.txt.parser.element.TagElement;
import com.github.nianna.karedi.txt.parser.element.TrackElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SongDisassemblerTest {

    private final SongDisassembler songDisassembler = new SongDisassembler();

    @Test
    public void shouldDisassembleSinglePlayerSong() {
        List<Tag> tags = List.of(new Tag("key1", "value1"), new Tag("bpm", "240.00"));
        List<SongTrack> songTracks = List.of(createSongTrack(1));

        List<VisitableSongElement> result = songDisassembler.disassemble(tags, songTracks);

        List<VisitableSongElement> expectedResult = List.of(
                new TagElement("KEY1", "value1"),
                new TagElement("BPM", "240.00"),
                new NoteElement(NoteElementType.NORMAL, 0, 10, 3, "first"),
                new NoteElement(NoteElementType.GOLDEN, 11, 5, -5, "second"),
                new LineBreakElement(18),
                new NoteElement(NoteElementType.FREESTYLE, 20, 1, 7, "why-"),
                new NoteElement(NoteElementType.RAP, 30, 3, -40, "~"),
                new NoteElement(NoteElementType.GOLDEN_RAP, 40, 5, 40, " try"),
                new EndOfSongElement()
        );
        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldDisassemblePlacingSpacesAfterWordsInLyrics() {
        List<Tag> tags = List.of(new Tag("key1", "value1"), new Tag("bpm", "240.00"));
        List<SongTrack> songTracks = List.of(createSongTrack(1));

        Settings.setPlaceSpacesAfterWords(true);
        List<VisitableSongElement> result = songDisassembler.disassemble(tags, songTracks);

        List<VisitableSongElement> expectedResult = List.of(
                new TagElement("KEY1", "value1"),
                new TagElement("BPM", "240.00"),
                new NoteElement(NoteElementType.NORMAL, 0, 10, 3, "first"),
                new NoteElement(NoteElementType.GOLDEN, 11, 5, -5, "second"),
                new LineBreakElement(18),
                new NoteElement(NoteElementType.FREESTYLE, 20, 1, 7, "why-"),
                new NoteElement(NoteElementType.RAP, 30, 3, -40, "~ "),
                new NoteElement(NoteElementType.GOLDEN_RAP, 40, 5, 40, "try"),
                new EndOfSongElement()
        );
        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldDisassembleDuetSong() {
        List<Tag> tags = List.of(new Tag("foo", "bar"), new Tag("foo2", "bar2"));
        List<SongTrack> songTracks = List.of(createSongTrack(1), createSongTrack(3));

        List<VisitableSongElement> result = songDisassembler.disassemble(tags, songTracks);

        List<VisitableSongElement> expectedResult = List.of(
                new TagElement("FOO", "bar"),
                new TagElement("FOO2", "bar2"),
                new TrackElement(1),
                new NoteElement(NoteElementType.NORMAL, 0, 10, 3, "first"),
                new NoteElement(NoteElementType.GOLDEN, 11, 5, -5, "second"),
                new LineBreakElement(18),
                new NoteElement(NoteElementType.FREESTYLE, 20, 1, 7, "why-"),
                new NoteElement(NoteElementType.RAP, 30, 3, -40, "~"),
                new NoteElement(NoteElementType.GOLDEN_RAP, 40, 5, 40, " try"),
                new TrackElement(2),
                new NoteElement(NoteElementType.NORMAL, 0, 10, 3, "first"),
                new NoteElement(NoteElementType.GOLDEN, 11, 5, -5, "second"),
                new LineBreakElement(18),
                new NoteElement(NoteElementType.FREESTYLE, 20, 1, 7, "why-"),
                new NoteElement(NoteElementType.RAP, 30, 3, -40, "~"),
                new NoteElement(NoteElementType.GOLDEN_RAP, 40, 5, 40, " try"),
                new EndOfSongElement()
        );
        assertEquals(expectedResult, result);
    }

    private static SongTrack createSongTrack(int playerNumber) {
        SongTrack songTrack = new SongTrack(playerNumber);
        List<Note> firstLineNotes = List.of(
                new Note(0, 10, 3, "first "),
                new Note(11, 5, -5, "second", Note.Type.GOLDEN)
        );
        SongLine firstSongLine = new SongLine(0, firstLineNotes);
        List<Note> secondLineNotes = List.of(
                new Note(20, 1, 7, "why-", Note.Type.FREESTYLE),
                new Note(30, 3, -40, "~", Note.Type.RAP),
                new Note(40, 5, 40, " try", Note.Type.GOLDEN_RAP)
        );
        SongLine secondSongLine = new SongLine(18, secondLineNotes);
        songTrack.addLine(firstSongLine);
        songTrack.addLine(secondSongLine);
        return songTrack;
    }

    @AfterEach
    public void cleanUp() {
        Settings.setPlaceSpacesAfterWords(false);
    }

}