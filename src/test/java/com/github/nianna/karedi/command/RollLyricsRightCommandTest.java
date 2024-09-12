package com.github.nianna.karedi.command;

import com.github.nianna.karedi.MockSongCreator;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.util.LyricsHelper;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RollLyricsRightCommandTest {
	private List<Note> notes = new ArrayList<>();

	@BeforeEach
	public void setUp() {
		notes.clear();
		notes.addAll(MockSongCreator.createLine(0, 4).getNotes());
	}

	@Test
	public void resetsFirstNoteLyricsToDefaultIfTheyAreNotSplittable() {
		notes.get(0).setLyrics("test");
		execute(1);
		assertEquals(LyricsHelper.defaultLyrics(), notes.get(0).getLyrics());
	}

	@Test
	public void splitsFirstNotesLyricsIfPossible() {
		String lyrics = "Foo bar dummy";
		notes.get(0).setLyrics(lyrics);
		execute(1);
		Pair<String, String> splitted = LyricsHelper.split(lyrics);
		assertEquals(splitted.getKey(), notes.get(0).getLyrics(), "Invalid first note's lyrics");
		assertEquals(splitted.getValue(), notes.get(1).getLyrics(), "Invalid second note's lyrics");
	}

	@Test
	public void simplyMovesLyricsOfFurtherNotesByTheSpecifiedAmount() {
		String lyrics = "Foo bar";
		notes.get(1).setLyrics(lyrics);
		execute(2);
		assertEquals(lyrics, notes.get(3).getLyrics());
	}

	@Test
	public void joinsLyricsThatDoNotFitAndStoresThemInTheLastNote() {
		String lyrics = "Foo";
		String lastLyrics = "bar";
		notes.get(notes.size() - 2).setLyrics(lyrics);
		notes.get(notes.size() - 1).setLyrics(lastLyrics);
		execute(1);
		assertEquals(LyricsHelper.join(lyrics, lastLyrics),
				notes.get(notes.size() - 1).getLyrics());
	}

	@Test
	public void changesLastNotesLyricsToDefaultIfAskedToRollThem() {
		Note lastNote = notes.get(notes.size() - 1);
		notes.clear();
		notes.add(lastNote);
		execute(1);
		assertEquals(LyricsHelper.defaultLyrics(), lastNote.getLyrics());
	}

	@Test
	public void storesLyricsOnLastNoteIfByIsGreaterThanOrEqualToListSize() {
		String jointLyrics = assignUniqueLyrics();
		execute(notes.size());
		assertEquals(jointLyrics, notes.get(notes.size() - 1).getLyrics());
	}

	@Test
	public void rollingByOneNTimesShouldHaveTheSameEffectAsRollingOnceWithN() {
		int by = notes.size();
		assignUniqueLyrics();
		for (int i = 0; i < by; ++i) {
			execute(1);
		}
		String lastNoteLyrics = notes.get(notes.size() - 1).getLyrics();
		assignUniqueLyrics();
		execute(by);
		assertEquals(lastNoteLyrics, notes.get(notes.size() - 1).getLyrics());
	}

	private String assignUniqueLyrics() {
		for (int i = 0; i < notes.size(); ++i) {
			notes.get(i).setLyrics("test" + i);
		}
		return LyricsHelper.join(notes.stream().map(Note::getLyrics).collect(Collectors.toList()));
	}

	private boolean execute(int by) {
		return new RollLyricsRightCommand(notes, by).execute();
	}
}
