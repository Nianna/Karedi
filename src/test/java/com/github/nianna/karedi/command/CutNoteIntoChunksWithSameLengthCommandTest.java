package com.github.nianna.karedi.command;

import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Note.Type;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.util.LyricsHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CutNoteIntoChunksWithSameLengthCommandTest {
	private static int LENGTH = 10;
	private static int START_BEAT = 10;
	private static int TONE = 10;
	private static int HALF_LENGTH = LENGTH / 2;
	private static String LYRICS = "Lorem ipsum dolor sit";
	private static Type TYPE = Type.FREESTYLE;
	private Note note;
	private SongLine line;

	@BeforeEach
	public void setUp() {
		note = new Note(START_BEAT, LENGTH, TONE, LYRICS, TYPE);
		line = new SongLine(0, Arrays.asList(note));
	}

	@Test
	public void doesNothingIfSplitPointIsNegative() {
		boolean result = new CutNoteIntoChunksWithSameLengthCommand(note, -1).execute();
		assertFalse(result);
		assertEquals(LENGTH, note.getLength());
	}

	@Test
	public void doesNothingIfSplitPointIsBiggerThanNotesLength() {
		boolean result = new CutNoteIntoChunksWithSameLengthCommand(note, LENGTH + 1).execute();
		assertFalse(result);
		assertEquals(LENGTH, note.getLength());
	}

	@Test
	public void doesNothingIfSplitPointIsEqualToNotesLength() {
		boolean result = new CutNoteIntoChunksWithSameLengthCommand(note, LENGTH).execute();
		assertFalse(result);
		assertEquals(LENGTH, note.getLength());
	}

	@Test
	public void doesNothingIfSplitPointIsLocatedAtTheNotesBeginning() {
		boolean result = new CutNoteIntoChunksWithSameLengthCommand(note, 0).execute();
		assertFalse(result);
		assertEquals(LENGTH, note.getLength());
	}

	@Test
	public void allowsChunksWithLengthOne() {
		boolean result = new CutNoteIntoChunksWithSameLengthCommand(note, 1).execute();
		assertTrue(result);
		assertEquals(10, line.size());
	}


	@Test
	public void createsExpectedNumberOfNotesForDivisibleLength() {
		new CutNoteIntoChunksWithSameLengthCommand(note, HALF_LENGTH).execute();
		assertEquals(2, line.size());
		assertEquals(HALF_LENGTH, line.getNotes().get(0).getLength());
		assertEquals(HALF_LENGTH, line.getNotes().get(1).getLength());
	}

	@Test
	public void lastNoteShorterIfLengthNotDivisible() {
		int chunkLength = 3;
		new CutNoteIntoChunksWithSameLengthCommand(note, chunkLength).execute();
		assertEquals(4, line.size());
		assertEquals(chunkLength, line.getNotes().get(0).getLength());
		assertEquals(chunkLength, line.getNotes().get(1).getLength());
		assertEquals(chunkLength, line.getNotes().get(2).getLength());
		assertEquals(1, line.getNotes().get(3).getLength());
	}

	@Test
	public void allNotesStartBeatsDifferByChunkLength() {
		int chunkLength = 4;
		new CutNoteIntoChunksWithSameLengthCommand(note, chunkLength).execute();
		assertEquals(START_BEAT, line.getNotes().get(0).getStart());
		assertEquals(START_BEAT + chunkLength, line.getNotes().get(1).getStart());
		assertEquals(START_BEAT + 2 * chunkLength, line.getNotes().get(2).getStart());
	}

	@Test
	public void allNotesEndCorrectly() {
		int chunkLength = 4;
		new CutNoteIntoChunksWithSameLengthCommand(note, chunkLength).execute();
		assertEquals(START_BEAT + chunkLength, line.getNotes().get(0).getEnd());
		assertEquals(START_BEAT + 2 * chunkLength, line.getNotes().get(1).getEnd());
		assertEquals(START_BEAT + LENGTH, line.getNotes().get(2).getEnd());
	}

	@Test
	public void allNotesHaveTheSameTone() {
		new CutNoteIntoChunksWithSameLengthCommand(note, 4).execute();
		assertEquals(TONE, line.getNotes().get(0).getTone());
		assertEquals(TONE, line.getNotes().get(1).getTone());
		assertEquals(TONE, line.getNotes().get(2).getTone());
	}

	@Test
	public void allNotesHaveTheSameType() {
		new CutNoteIntoChunksWithSameLengthCommand(note, 4).execute();
		assertEquals(TYPE, line.getNotes().get(0).getType());
		assertEquals(TYPE, line.getNotes().get(1).getType());
		assertEquals(TYPE, line.getNotes().get(2).getType());
	}

	@Test
	public void lyricsAreSplitCorrectlyOverTooFewChunks() {
		new CutNoteIntoChunksWithSameLengthCommand(note, 4).execute();
		assertEquals("Lorem ipsum", line.getNotes().get(0).getLyrics());
		assertEquals(" dolor", line.getNotes().get(1).getLyrics());
		assertEquals(" sit", line.getNotes().get(2).getLyrics());
	}

	@Test
	public void lyricsAreSplitCorrectlyOverTooManyChunks() {
		new CutNoteIntoChunksWithSameLengthCommand(note, 2).execute();
		assertEquals(5, line.getNotes().size());
		assertEquals("Lorem", line.getNotes().get(0).getLyrics());
		assertEquals(" ipsum", line.getNotes().get(1).getLyrics());
		assertEquals(" dolor", line.getNotes().get(2).getLyrics());
		assertEquals(" sit", line.getNotes().get(3).getLyrics());
		assertEquals("~", line.getNotes().get(4).getLyrics());
	}

	@Test
	public void emptyLyricsAreDistributedCorrectly() {
		note.setLyrics(LyricsHelper.defaultLyrics());
		new CutNoteIntoChunksWithSameLengthCommand(note, 4).execute();
		assertEquals("~", line.getNotes().get(0).getLyrics());
		assertEquals("~", line.getNotes().get(1).getLyrics());
		assertEquals("~", line.getNotes().get(2).getLyrics());
	}

	@Test
	public void removesOneNoteAfterUndo() {
		Command cmd = new CutNoteIntoChunksWithSameLengthCommand(note, HALF_LENGTH);
		cmd.execute();
		cmd.undo();
		assertEquals(1, line.size());
	}

	@Test
	public void restoresLengthAfterUndo() {
		Command cmd = new CutNoteIntoChunksWithSameLengthCommand(note, HALF_LENGTH);
		cmd.execute();
		cmd.undo();
		assertEquals(LENGTH, note.getLength());
	}

	@Test
	public void restoresLyricsAfterUndo() {
		Command cmd = new CutNoteIntoChunksWithSameLengthCommand(note, HALF_LENGTH);
		cmd.execute();
		cmd.undo();
		assertEquals(LYRICS, note.getLyrics());
	}

}
