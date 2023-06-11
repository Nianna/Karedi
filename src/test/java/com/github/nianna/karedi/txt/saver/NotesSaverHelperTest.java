package com.github.nianna.karedi.txt.saver;

import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.txt.parser.ParsingFactory;
import com.github.nianna.karedi.txt.parser.Unparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotesSaverHelperTest {

    Unparser unparser = ParsingFactory.createUnparser();

    SongDisassembler songDisassembler = new SongDisassembler();

    private final NotesSaverHelper helper = new NotesSaverHelper(unparser, songDisassembler);

    @Test
    public void shouldPrepareEmptyResultForNoNotes() {
        Stream<String> result = helper.toUnparsedRepresentation(List.of());

        assertEquals(0, result.count());
    }

    @Test
    public void shouldUnparseSingleLineNotesWithNoLineBreaks() {
        List<Note> firstLineNotes = List.of(
                new Note(0, 10),
                new Note(10, 5),
                new Note(20, 3)
        );
        SongLine firstLine = new SongLine(0, firstLineNotes);
        firstLine.setTrack(new SongTrack(1));

        Stream<String> result = helper.toUnparsedRepresentation(firstLine.getNotes());

        List<String> expectedResult = List.of(
                ": 0 10 0 ~",
                ": 10 5 0 ~",
                ": 20 3 0 ~"
        );
        assertEquals(expectedResult, result.toList());
    }

    @Test
    public void shouldUnparseMultilineNotes() {
        List<Note> notes = List.of(
                new Note(0, 10),
                new Note(10, 5),
                new Note(20, 3)
        );
        SongLine firstLine = new SongLine(0, notes.subList(0, 2));
        SongLine secondLine = new SongLine(17, notes.subList(2, notes.size()));
        SongTrack track = new SongTrack(1);
        firstLine.setTrack(track);
        secondLine.setTrack(track);

        Stream<String> result = helper.toUnparsedRepresentation(notes);

        List<String> expectedResult = List.of(
                ": 0 10 0 ~",
                ": 10 5 0 ~",
                "- 17",
                ": 20 3 0 ~"
        );
        assertEquals(expectedResult, result.toList());
    }

}