package com.github.nianna.karedi;


import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MockSongCreatorTest {
	private static final int TRACKS_COUNT = 2;
	private static final int LINES_IN_TRACK = 3;
	private static final int NOTES_IN_LINE = 4;
	private static Song song;

	@BeforeAll
	public static void setUpClass() {
		song = MockSongCreator.createSong(TRACKS_COUNT, LINES_IN_TRACK, NOTES_IN_LINE);
	}

	@Test
	public void checkTrackCount() {
		assertEquals(TRACKS_COUNT, song.size());
	}

	@Test
	public void checkLinesCount() {
		for (int i = 0; i < TRACKS_COUNT; ++i) {
			assertEquals(LINES_IN_TRACK, song.get(i).size());
		}
	}

	@Test
	public void checkNotesCountForFirstTrack() {
		SongTrack track = song.getTrack(0);
		for (int i = 0; i < LINES_IN_TRACK; ++i) {
			assertEquals(NOTES_IN_LINE, track.getLine(i).size());
		}
	}

}
