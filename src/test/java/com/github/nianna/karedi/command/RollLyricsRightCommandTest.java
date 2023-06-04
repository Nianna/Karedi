package test.java.com.github.nianna.karedi.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javafx.util.Pair;
import com.github.nianna.karedi.command.RollLyricsRightCommand;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.util.LyricsHelper;
import test.java.com.github.nianna.karedi.MockSongCreator;

public class RollLyricsRightCommandTest {
	private List<Note> notes = new ArrayList<>();

	@Before
	public void setUp() {
		notes.clear();
		notes.addAll(MockSongCreator.createLine(0, 4).getNotes());
	}

	@Test
	public void resetsFirstNoteLyricsToDefaultIfTheyAreNotSplittable() {
		notes.get(0).setLyrics("test");
		execute(1);
		Assert.assertEquals(LyricsHelper.defaultLyrics(), notes.get(0).getLyrics());
	}

	@Test
	public void splitsFirstNotesLyricsIfPossible() {
		String lyrics = "Foo bar dummy";
		notes.get(0).setLyrics(lyrics);
		execute(1);
		Pair<String, String> splitted = LyricsHelper.split(lyrics);
		Assert.assertEquals("Invalid first note's lyrics", splitted.getKey(),
				notes.get(0).getLyrics());
		Assert.assertEquals("Invalid second note's lyrics", splitted.getValue(),
				notes.get(1).getLyrics());
	}

	@Test
	public void simplyMovesLyricsOfFurtherNotesByTheSpecifiedAmount() {
		String lyrics = "Foo bar";
		notes.get(1).setLyrics(lyrics);
		execute(2);
		Assert.assertEquals(lyrics, notes.get(3).getLyrics());
	}

	@Test
	public void joinsLyricsThatDoNotFitAndStoresThemInTheLastNote() {
		String lyrics = "Foo";
		String lastLyrics = "bar";
		notes.get(notes.size() - 2).setLyrics(lyrics);
		notes.get(notes.size() - 1).setLyrics(lastLyrics);
		execute(1);
		Assert.assertEquals(LyricsHelper.join(lyrics, lastLyrics),
				notes.get(notes.size() - 1).getLyrics());
	}

	@Test
	public void changesLastNotesLyricsToDefaultIfAskedToRollThem() {
		Note lastNote = notes.get(notes.size() - 1);
		notes.clear();
		notes.add(lastNote);
		execute(1);
		Assert.assertEquals(LyricsHelper.defaultLyrics(), lastNote.getLyrics());
	}

	@Test
	public void storesLyricsOnLastNoteIfByIsGreaterThanOrEqualToListSize() {
		String jointLyrics = assignUniqueLyrics();
		execute(notes.size());
		Assert.assertEquals(jointLyrics, notes.get(notes.size() - 1).getLyrics());
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
		Assert.assertEquals(lastNoteLyrics, notes.get(notes.size() - 1).getLyrics());
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
