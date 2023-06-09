package com.github.nianna.karedi.loader;

import com.github.nianna.karedi.parser.element.EndOfSongElement;
import com.github.nianna.karedi.parser.element.LineBreakElement;
import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.NoteElementType;
import com.github.nianna.karedi.parser.element.TagElement;
import com.github.nianna.karedi.parser.element.TrackElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;

public class BasicSongBuilderTest {

    private final SongBuilder songBuilder = new BasicSongBuilder();

    @Test
    public void shouldBuildEmptySongWithFirstPlayerTrack() {
        Song song = songBuilder.getResult();

        Assert.assertEquals(1, song.getTrackCount());
        Assert.assertEquals(1, song.getTrack(0).getPlayer().intValue());
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

        Assert.assertEquals(1, song.getTrackCount());
        SongTrack track = song.getTrack(0);
        Assert.assertEquals(1, track.getLines().size());
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

        Assert.assertEquals(1, song.getTrackCount());
        SongTrack track = song.getTrack(0);

        Assert.assertEquals(2, track.getLines().size());
        Assert.assertEquals(0, track.getLine(0).getLineBreak());
        Assert.assertEquals(1, track.getLine(0).getNotes().size());
        Assert.assertEquals(13, track.getLine(1).getLineBreak());
        Assert.assertEquals(2, track.getLine(1).getNotes().size());
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

        Assert.assertEquals(2, song.getTrackCount());
        Assert.assertEquals(1, song.getTrack(0).getPlayer().intValue());
        Assert.assertEquals(3, song.getTrack(1).getPlayer().intValue());

        List<SongLine> firstTrackLines = song.getTrack(0).getLines();
        Assert.assertEquals(1, firstTrackLines.size());
        Assert.assertEquals(1, firstTrackLines.get(0).getNotes().size());

        List<SongLine> secondTrackLines = song.getTrack(1).getLines();
        Assert.assertEquals(1, secondTrackLines.size());
        Assert.assertEquals(2, secondTrackLines.get(0).getNotes().size());
    }


    @Test
    public void shouldBuildTags() {
        TagElement fooTag = new TagElement("FOO", "FOO_VALUE");
        TagElement barTag = new TagElement("BAR", "BAR_VALUE");

        List.of(fooTag, barTag).forEach(songBuilder::buildPart);
        Song song = songBuilder.getResult();

        Assert.assertEquals(2, song.getTags().size());
        Assert.assertTrue(song.hasTag(fooTag.key()));
        Assert.assertTrue(song.hasTag(barTag.key()));
        Assert.assertEquals(fooTag.value(), song.getTagValue(fooTag.key()).orElseThrow());
        Assert.assertEquals(barTag.value(), song.getTagValue(barTag.key()).orElseThrow());

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

        Assert.assertEquals(1, song.getTrackCount());
        ObservableList<SongLine> firstTrackLines = song.getTrack(0).getLines();
        Assert.assertEquals(1, firstTrackLines.size());
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

        Assert.assertEquals(1, song.getTrackCount());
        ObservableList<SongLine> firstTrackLines = song.getTrack(0).getLines();
        Assert.assertEquals(2, firstTrackLines.size());
        Assert.assertEquals(" no_space", firstTrackLines.get(0).getNotes().get(0).getLyrics());
        Assert.assertEquals(" same_here", firstTrackLines.get(1).getNotes().get(0).getLyrics());
    }

    @Test
    public void shouldNormalizeNoteLyrics() {
        songBuilder.buildPart(new NoteElement(NoteElementType.RAP, 0, 10, -3, ""));
        Song song = songBuilder.getResult();

        Assert.assertEquals(1, song.getTrackCount());
        Assert.assertEquals(" ~", song.getTrack(0).getLine(0).getNotes().get(0).getLyrics());
    }

    private void assertLineNotesValidity(List<NoteElement> noteElements, List<Note> notes) {
        Assert.assertEquals(noteElements.size(), notes.size());
        IntStream.range(0, noteElements.size())
                .forEach(i -> assertNoteParameters(noteElements.get(i), notes.get(i)));
    }

    private void assertNoteParameters(NoteElement noteElement, Note note) {
        Assert.assertEquals(noteElement.type().name(), note.getType().name());
        Assert.assertEquals(noteElement.startsAt(), note.getStart());
        Assert.assertEquals(noteElement.length(), note.getLength());
        Assert.assertEquals(noteElement.tone(), note.getTone());
        Assert.assertEquals(noteElement.lyrics(), note.getLyrics());
    }

}