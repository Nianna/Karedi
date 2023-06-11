package com.github.nianna.karedi.command;

import com.github.nianna.karedi.MockSongCreator;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ToggleLineBreakCommandTest {
	private int noteCount = 6;
	private int lineCount = 2;
	private Song song;
	private SongTrack track;
	private SongLine firstLine;
	private SongLine secondLine;

	@BeforeEach
	public void setUp() {
		song = MockSongCreator.createSong(1, lineCount, noteCount);
		track = song.getTrack(0);
		firstLine = track.getLine(0);
		secondLine = track.getLine(1);
	}

	@Test
	public void splitsLineForNotesThatAreInTheMiddleOfTheLine() {
		int testNoteIndex = noteCount / 2;
		int lineSize = firstLine.size();
		Note testNote = firstLine.get(testNoteIndex);
		assert !testNote.isFirstInLine();
		List<Note> firstPart = new ArrayList<>(firstLine.getNotes().subList(0, testNoteIndex));
		List<Note> secondPart = new ArrayList<>(
				firstLine.getNotes().subList(testNoteIndex, lineSize));
		boolean result = new ToggleLineBreakCommand(testNote).execute();
		assertTrue(result);
		assertTrue(testNote.isFirstInLine());
		assertEquals(lineCount + 1, track.size());
		assertEquals(firstPart, track.getLine(0).getNotes());
		assertEquals(secondPart, track.getLine(1).getNotes());
	}

	@Test
	public void joinsLineWithPreviousForItsFirstNote() {
		Note testNote = secondLine.get(0);
		List<Note> notes = new ArrayList<>(firstLine.getNotes());
		notes.addAll(secondLine.getNotes());
		boolean result = new ToggleLineBreakCommand(testNote).execute();
		assertTrue(result);
		assertFalse(testNote.isFirstInLine());
		assertEquals(lineCount - 1, track.size());
		assertEquals(notes, track.getLine(0).getNotes());
	}

	@Test
	public void doesNothingForFirstNoteOfTheTrack() {
		Note testNote = track.getNotes().get(0);
		boolean result = new ToggleLineBreakCommand(testNote).execute();
		assertFalse(result);
		assertTrue(testNote.isFirstInLine());
	}

	@Test
	public void lineContentIsCorrectlyRestoredAfterUndoForMiddleNotes() {
		Note note = firstLine.get(1);
		List<Note> firstLineNotes = new ArrayList<>(firstLine.getNotes());
		Command cmd = new ToggleLineBreakCommand(note);
		cmd.execute();
		cmd.undo();
		assertEquals(firstLineNotes, track.getLine(0).getNotes());
	}

	@Test
	public void linesContentsAreCorrectlyRestoredAfterUndoForFirstNoteInLine() {
		Note note = secondLine.get(0);
		List<Note> firstLineNotes = new ArrayList<>(firstLine.getNotes());
		List<Note> secondLineNotes = new ArrayList<>(secondLine.getNotes());
		Command cmd = new ToggleLineBreakCommand(note);
		cmd.execute();
		cmd.undo();
		assertEquals(firstLineNotes, track.getLine(0).getNotes());
		assertEquals(secondLineNotes, track.getLine(1).getNotes());
	}
}