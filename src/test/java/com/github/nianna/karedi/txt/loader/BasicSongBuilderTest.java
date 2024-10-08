package com.github.nianna.karedi.txt.loader;

import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;
import com.github.nianna.karedi.txt.parser.element.LineBreakElement;
import com.github.nianna.karedi.txt.parser.element.NoteElement;
import com.github.nianna.karedi.txt.parser.element.NoteElementType;
import com.github.nianna.karedi.txt.parser.element.TagElement;
import com.github.nianna.karedi.txt.parser.element.TrackElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BasicSongBuilderTest {

    private final SongBuilder songBuilder = new BasicSongBuilder();

    @Test
    public void shouldBuildEmptySongWithFirstPlayerTrack() {
        Song song = songBuilder.getResult();

        assertEquals(1, song.getTrackCount());
        assertEquals(1, song.getTrack(0).getPlayer().intValue());
    }

    @Test
    public void shouldBuildNotesOfAllTypes() {
        NoteElement freestyleNote = new NoteElement(NoteElementType.FREESTYLE, 0, 10, -3, " feel");
        NoteElement rapNote = new NoteElement(NoteElementType.RAP, 12, 5, 8, " good");
        NoteElement normalNote = new NoteElement(NoteElementType.NORMAL, 23, 2, -8, " about");
        NoteElement goldenRapNote = new NoteElement(NoteElementType.GOLDEN_RAP, 27, 20, 0, " your");
        NoteElement goldenNote = new NoteElement(NoteElementType.GOLDEN, 50, 6, 1, "self");

        List<NoteElement> noteElements = List.of(freestyleNote, rapNote, normalNote, goldenRapNote, goldenNote);
        noteElements.forEach(songBuilder::buildPart);
        Song song = songBuilder.getResult();

        assertEquals(1, song.getTrackCount());
        SongTrack track = song.getTrack(0);
        assertEquals(1, track.getLines().size());
        assertLineNotesValidity(noteElements, track.getLine(0).getNotes());
    }

    @Test
    public void shouldBuildLines() {
        List<VisitableSongElement> elements = List.of(
                new NoteElement(NoteElementType.NORMAL, 0, 10, -3, " stay"),
                new LineBreakElement(13),
                new NoteElement(NoteElementType.NORMAL, 15, 5, 8, " weird"),
                new NoteElement(NoteElementType.NORMAL, 17, 8, -3, "~")
        );

        elements.forEach(songBuilder::buildPart);
        Song song = songBuilder.getResult();

        assertEquals(1, song.getTrackCount());
        SongTrack track = song.getTrack(0);

        assertEquals(2, track.getLines().size());
        assertEquals(0, track.getLine(0).getLineBreak());
        assertEquals(1, track.getLine(0).getNotes().size());
        assertEquals(13, track.getLine(1).getLineBreak());
        assertEquals(2, track.getLine(1).getNotes().size());
    }

    @Test
    public void shouldBuildTracks() {
        List<VisitableSongElement> elements = List.of(
                new NoteElement(NoteElementType.RAP, 0, 10, -3, " tell"),
                new TrackElement(3),
                new NoteElement(NoteElementType.RAP, 15, 5, 8, " me"),
                new NoteElement(NoteElementType.RAP, 17, 8, -3, "why")
        );

        elements.forEach(songBuilder::buildPart);
        Song song = songBuilder.getResult();

        assertEquals(2, song.getTrackCount());
        assertEquals(1, song.getTrack(0).getPlayer().intValue());
        assertEquals(3, song.getTrack(1).getPlayer().intValue());

        List<SongLine> firstTrackLines = song.getTrack(0).getLines();
        assertEquals(1, firstTrackLines.size());
        assertEquals(1, firstTrackLines.get(0).getNotes().size());

        List<SongLine> secondTrackLines = song.getTrack(1).getLines();
        assertEquals(1, secondTrackLines.size());
        assertEquals(2, secondTrackLines.get(0).getNotes().size());
    }


    @Test
    public void shouldBuildTags() {
        TagElement fooTag = new TagElement("FOO", "FOO_VALUE");
        TagElement barTag = new TagElement("BAR", "BAR_VALUE");

        List.of(fooTag, barTag).forEach(songBuilder::buildPart);
        Song song = songBuilder.getResult();

        assertEquals(2, song.getTags().size());
        assertTrue(song.hasTag(fooTag.key()));
        assertTrue(song.hasTag(barTag.key()));
        assertEquals(fooTag.value(), song.getTagValue(fooTag.key()).orElseThrow());
        assertEquals(barTag.value(), song.getTagValue(barTag.key()).orElseThrow());

    }

    @Test
    public void shouldIgnoreElementsAfterEndSongElement() {
        NoteElement note = new NoteElement(NoteElementType.RAP, 0, 10, -3, " used");
        List<VisitableSongElement> elements = List.of(
                note,
                new EndOfSongElement(),
                new NoteElement(NoteElementType.RAP, 15, 5, 8, " ignored")
        );

        elements.forEach(songBuilder::buildPart);
        Song song = songBuilder.getResult();

        assertEquals(1, song.getTrackCount());
        ObservableList<SongLine> firstTrackLines = song.getTrack(0).getLines();
        assertEquals(1, firstTrackLines.size());
        assertLineNotesValidity(List.of(note), firstTrackLines.get(0).getNotes());
    }

    @Test
    public void shouldAddSpaceBeforeFirstNoteInLine() {
        List<VisitableSongElement> elements = List.of(
                new NoteElement(NoteElementType.RAP, 0, 10, -3, "no_space"),
                new LineBreakElement(12),
                new NoteElement(NoteElementType.RAP, 15, 5, 8, "same_here")
        );

        elements.forEach(songBuilder::buildPart);
        Song song = songBuilder.getResult();

        assertEquals(1, song.getTrackCount());
        ObservableList<SongLine> firstTrackLines = song.getTrack(0).getLines();
        assertEquals(2, firstTrackLines.size());
        assertEquals(" no_space", firstTrackLines.get(0).getNotes().get(0).getLyrics());
        assertEquals(" same_here", firstTrackLines.get(1).getNotes().get(0).getLyrics());
    }

    @Test
    public void shouldNormalizeNoteLyrics() {
        songBuilder.buildPart(new NoteElement(NoteElementType.RAP, 0, 10, -3, ""));
        Song song = songBuilder.getResult();

        assertEquals(1, song.getTrackCount());
        assertEquals(" ~", song.getTrack(0).getLine(0).getNotes().get(0).getLyrics());
    }

    @Test
    public void shouldMoveTrailingSpaceToNextNoteInLine() {
        List<VisitableSongElement> elements = List.of(
                new NoteElement(NoteElementType.RAP, 0, 10, -3, "lorem "),
                new NoteElement(NoteElementType.RAP, 4, 10, -3, "ipsum "),
                new LineBreakElement(12),
                new NoteElement(NoteElementType.RAP, 15, 5, 8, "dolor "),
                new NoteElement(NoteElementType.RAP, 17, 5, 8, " sit"),
                new NoteElement(NoteElementType.RAP, 20, 5, 8, "amet "),
                new NoteElement(NoteElementType.RAP, 24, 5, 8, "consectetuer")
        );

        elements.forEach(songBuilder::buildPart);
        Song song = songBuilder.getResult();

        assertEquals(1, song.getTrackCount());
        ObservableList<SongLine> firstTrackLines = song.getTrack(0).getLines();
        assertEquals(2, firstTrackLines.size());
        assertEquals(" lorem", firstTrackLines.get(0).getNotes().get(0).getLyrics());
        assertEquals(" ipsum", firstTrackLines.get(0).getNotes().get(1).getLyrics());
        assertEquals(" dolor", firstTrackLines.get(1).getNotes().get(0).getLyrics());
        assertEquals(" sit", firstTrackLines.get(1).getNotes().get(1).getLyrics());
        assertEquals("amet", firstTrackLines.get(1).getNotes().get(2).getLyrics());
        assertEquals(" consectetuer", firstTrackLines.get(1).getNotes().get(3).getLyrics());
    }

    private void assertLineNotesValidity(List<NoteElement> noteElements, List<Note> notes) {
        assertEquals(noteElements.size(), notes.size());
        IntStream.range(0, noteElements.size())
                .forEach(i -> assertNoteParameters(noteElements.get(i), notes.get(i)));
    }

    private void assertNoteParameters(NoteElement noteElement, Note note) {
        assertEquals(noteElement.type().name(), note.getType().name());
        assertEquals(noteElement.startsAt(), note.getStart());
        assertEquals(noteElement.length(), note.getLength());
        assertEquals(noteElement.tone(), note.getTone());
        assertEquals(noteElement.lyrics(), note.getLyrics());
    }

}