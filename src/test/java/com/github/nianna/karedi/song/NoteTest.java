package com.github.nianna.karedi.song;

import com.github.nianna.karedi.region.Direction;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoteTest {

	@Test
	public void testLowerXBound() {
		int expected = 20;
		Note note = new Note(expected, 0);
		assertEquals(expected, note.lowerXBoundProperty().get().intValue());
	}

	@Test
	public void testUpperXBound() {
		int expected = 10;
		Note note = new Note(0, expected);
		assertEquals(expected, note.upperXBoundProperty().get().intValue());
	}

	@Test
	public void testLowerYBound() {
		int expected = -5;
		Note note = new Note(0, 0, expected);
		assertEquals(expected, note.lowerYBoundProperty().get().intValue());
	}

	@Test
	public void testUpperYBound() {
		int expected = -5;
		Note note = new Note(0, 0, expected);
		assertEquals(expected, note.upperYBoundProperty().get().intValue());
	}

	@Test
	public void testMoveX() {
		Random generator = new Random();
		int by = generator.nextInt(10) + 1;
		Note note = new Note(0, 0);
		note.move(Direction.RIGHT, by);
		assertEquals(by, note.getStart());
		note.move(Direction.LEFT, by);
		assertEquals(0, note.getStart());
	}

	@Test
	public void testMoveY() {
		Random generator = new Random();
		int by = generator.nextInt(10) + 1;
		Note note = new Note(0, 0);
		note.move(Direction.UP, by);
		assertEquals(by, note.getTone());
	}

}
