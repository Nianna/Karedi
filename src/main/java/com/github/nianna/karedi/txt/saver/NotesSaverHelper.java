package com.github.nianna.karedi.txt.saver;

import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.txt.parser.Unparser;
import com.github.nianna.karedi.txt.parser.element.LineBreakElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class NotesSaverHelper {

    private final Unparser unparser;

    private final SongDisassembler songDisassembler;

    NotesSaverHelper(Unparser unparser, SongDisassembler songDisassembler) {
        this.unparser = unparser;
        this.songDisassembler = songDisassembler;
    }

    public Stream<String> toUnparsedRepresentation(List<Note> notes) {
        return groupNotesByLines(notes)
                .flatMap(this::disassembleLinePart)
                .skip(1) // skip first line break
                .map(unparser::unparse);
    }

    private Stream<Map.Entry<SongLine, List<Note>>> groupNotesByLines(List<Note> notes) {
        return notes.stream()
                .collect(Collectors.groupingBy(Note::getLine, TreeMap::new, Collectors.toList()))
                .entrySet().stream();
    }

    private Stream<VisitableSongElement> disassembleLinePart(Map.Entry<SongLine, List<Note>> lineNotes) {
        return Stream.concat(
                Stream.of(new LineBreakElement(lineNotes.getKey().getLineBreak())),
                disassembleNotes(lineNotes.getValue())
        );
    }

    private Stream<VisitableSongElement> disassembleNotes(List<Note> notes) {
        return notes.stream()
                .map(songDisassembler::disassemble);
    }
}
