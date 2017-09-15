package test.java.com.github.nianna.karedi.command;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.ToggleLineBreakCommand;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import test.java.com.github.nianna.karedi.MockSongCreator;

public class ToggleLineBreakCommandTest {
	private int noteCount = 6;
	private int lineCount = 2;
	private Song song;
	private SongTrack track;
	private SongLine firstLine;
	private SongLine secondLine;

	@Before
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
		Assert.assertEquals("Wrong value returned", result, true);
		Assert.assertTrue("Tested note should now start a new line", testNote.isFirstInLine());
		Assert.assertEquals("Track size should've increased by 1", lineCount + 1, track.size());
		Assert.assertEquals("All notes that were before the tested note should now constitute the"
				+ "first line", firstPart, track.getLine(0).getNotes());
		Assert.assertEquals("All notes, starting from the tested note, should now constitute the"
				+ "second line", secondPart, track.getLine(1).getNotes());
	}

	@Test
	public void joinsLineWithPreviousForItsFirstNote() {
		Note testNote = secondLine.get(0);
		List<Note> notes = new ArrayList<>(firstLine.getNotes());
		notes.addAll(secondLine.getNotes());
		boolean result = new ToggleLineBreakCommand(testNote).execute();
		Assert.assertEquals("Wrong value returned", result, true);
		Assert.assertFalse("Tested note shouldn't break the line now", testNote.isFirstInLine());
		Assert.assertEquals("Track size should've decreased by 1", lineCount - 1, track.size());
		Assert.assertEquals("All notes from these two lines should now constitute the first line",
				notes, track.getLine(0).getNotes());
	}

	@Test
	public void doesNothingForFirstNoteOfTheTrack() {
		Note testNote = track.getNotes().get(0);
		boolean result = new ToggleLineBreakCommand(testNote).execute();
		Assert.assertEquals("Wrong value returned", false, result);
		Assert.assertTrue("The note should still break line", testNote.isFirstInLine());
	}

	@Test
	public void lineContentIsCorrectlyRestoredAfterUndoForMiddleNotes() {
		Note note = firstLine.get(1);
		List<Note> firstLineNotes = new ArrayList<>(firstLine.getNotes());
		Command cmd = new ToggleLineBreakCommand(note);
		cmd.execute();
		cmd.undo();
		Assert.assertEquals("Content has changed", firstLineNotes, track.getLine(0).getNotes());
	}

	@Test
	public void linesContentsAreCorrectlyRestoredAfterUndoForFirstNoteInLine() {
		Note note = secondLine.get(0);
		List<Note> firstLineNotes = new ArrayList<>(firstLine.getNotes());
		List<Note> secondLineNotes = new ArrayList<>(secondLine.getNotes());
		Command cmd = new ToggleLineBreakCommand(note);
		cmd.execute();
		cmd.undo();
		Assert.assertEquals("First line's content has changed", firstLineNotes,
				track.getLine(0).getNotes());
		Assert.assertEquals("Second line's content has changed", secondLineNotes,
				track.getLine(1).getNotes());
	}
}