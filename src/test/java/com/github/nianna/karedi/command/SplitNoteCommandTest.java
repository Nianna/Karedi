package com.github.nianna.karedi.command;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.SplitNoteCommand;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Note.Type;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.util.LyricsHelper;

public class SplitNoteCommandTest {
	private static int LENGTH = 10;
	private static int START_BEAT = 10;
	private static int TONE = 10;
	private static int LEGAL_SPLIT_POINT = LENGTH / 2;
	private static String LYRICS = "Test lyrics";
	private static Type TYPE = Type.GOLDEN;
	private Note note;
	private SongLine line;

	@Before
	public void setUp() {
		note = new Note(START_BEAT, LENGTH, TONE, LYRICS, TYPE);
		line = new SongLine(0, Arrays.asList(note));
	}

	@Test
	public void doesNothingIfSplitPointIsNegative() {
		boolean result = new SplitNoteCommand(note, -1).execute();
		Assert.assertEquals("Wrong value returned", result, false);
		Assert.assertEquals("Length has changed", LENGTH, note.getLength());
	}

	@Test
	public void doesNothingIfSplitPointIsBiggerThanNotesLength() {
		boolean result = new SplitNoteCommand(note, LENGTH + 1).execute();
		Assert.assertEquals("Wrong value returned", result, false);
		Assert.assertEquals("Length has changed", LENGTH, note.getLength());
	}

	@Test
	public void doesNothingIfSplitPointIsEqualToNotesLength() {
		boolean result = new SplitNoteCommand(note, LENGTH).execute();
		Assert.assertEquals("Wrong value returned", result, false);
		Assert.assertEquals("Length has changed", LENGTH, note.getLength());
	}

	@Test
	public void doesNothingIfSplitPointIsLocatedAtTheNotesBeginning() {
		boolean result = new SplitNoteCommand(note, 0).execute();
		Assert.assertEquals("Wrong value returned", result, false);
		Assert.assertEquals("Length has changed", LENGTH, note.getLength());
	}

	@Test
	public void doesNothingIfSplitPointIsEqualToOne() {
		boolean result = new SplitNoteCommand(note, 1).execute();
		Assert.assertEquals("Wrong value returned", result, false);
		Assert.assertEquals("Length has changed", LENGTH, note.getLength());
	}

	@Test
	public void returnsTrueForValidSplitPoint() {
		boolean result = new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		Assert.assertEquals("Wrong value returned", result, true);
	}

	@Test
	public void dividesNoteIntoTwoForCorrectSplitPoints() {
		new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		Assert.assertEquals("New note should've been added to the line", 2, line.size());
	}

	@Test
	public void secondNoteStartsAtTheSplitPoint() {
		new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		Assert.assertEquals("Wrong start beat for the second note", START_BEAT + LEGAL_SPLIT_POINT,
				line.getNotes().get(1).getStart());
	}

	@Test
	public void secondNoteEndsInTheSameBeatThatTheTestedNote() {
		new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		Assert.assertEquals("Second note ends in the wrong beat", START_BEAT + LENGTH,
				line.getNotes().get(1).getEnd());
	}

	@Test
	public void firstNoteStartsInTheSameBeatThatTheTestedNote() {
		new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		Assert.assertEquals("First note starts in the wrong beat", START_BEAT,
				line.getNotes().get(0).getStart());
	}

	@Test
	public void firstNoteEndsOneBeatBeforeTheSplitPoint() {
		new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		Assert.assertEquals("First note ends in the wrong beat", START_BEAT + LEGAL_SPLIT_POINT - 1,
				line.getNotes().get(0).getEnd());
	}

	@Test
	public void firstNoteHasTheSameTone() {
		new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		Assert.assertEquals("First note has wrong tone", TONE, line.getNotes().get(0).getTone());
	}

	@Test
	public void secondNoteHasTheSameTone() {
		new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		Assert.assertEquals("First note has wrong tone", TONE, line.getNotes().get(1).getTone());
	}

	@Test
	public void firstNoteHasTheSameType() {
		new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		Assert.assertEquals("First note has wrong tone", TYPE, line.getNotes().get(0).getType());
	}

	@Test
	public void secondNoteHasTheSameType() {
		new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		Assert.assertEquals("First note has wrong tone", TYPE, line.getNotes().get(1).getType());
	}

	@Test
	public void lyricsWereSplitCorrectly() {
		new SplitNoteCommand(note, LEGAL_SPLIT_POINT).execute();
		String firstPart = LyricsHelper.split(LYRICS).getKey();
		String secondPart = LyricsHelper.split(LYRICS).getValue();
		Assert.assertEquals("First note has wrong lyrics", firstPart,
				line.getNotes().get(0).getLyrics());
		Assert.assertEquals("Second note has wrong lyrics", secondPart,
				line.getNotes().get(1).getLyrics());
	}

	@Test
	public void removesOneNoteAfterUndo() {
		Command cmd = new SplitNoteCommand(note, LEGAL_SPLIT_POINT);
		cmd.execute();
		cmd.undo();
		Assert.assertEquals("Wrong line size", 1, line.size());
	}

	@Test
	public void restoresLengthAfterUndo() {
		Command cmd = new SplitNoteCommand(note, LEGAL_SPLIT_POINT);
		cmd.execute();
		cmd.undo();
		Assert.assertEquals("Length has changed", LENGTH, note.getLength());
	}

	@Test
	public void restoresLyricsAfterUndo() {
		Command cmd = new SplitNoteCommand(note, LEGAL_SPLIT_POINT);
		cmd.execute();
		cmd.undo();
		Assert.assertEquals("Lyrics has changed", LYRICS, note.getLyrics());
	}

}
