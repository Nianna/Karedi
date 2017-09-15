package test.java.com.github.nianna.karedi;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongTrack;

public class MockSongCreatorTest {
	private static final int TRACKS_COUNT = 2;
	private static final int LINES_IN_TRACK = 3;
	private static final int NOTES_IN_LINE = 4;
	private static Song song;

	@BeforeClass
	public static void setUpClass() {
		song = MockSongCreator.createSong(TRACKS_COUNT, LINES_IN_TRACK, NOTES_IN_LINE);
	}

	@Test
	public void checkTrackCount() {
		Assert.assertEquals(TRACKS_COUNT, song.size());
	}

	@Test
	public void checkLinesCount() {
		for (int i = 0; i < TRACKS_COUNT; ++i) {
			Assert.assertEquals("Wrong number of lines for track " + i, LINES_IN_TRACK,
					song.get(i).size());
		}
	}

	public void checkNotesCountForFirstTrack() {
		SongTrack track = song.getTrack(0);
		for (int i = 0; i < LINES_IN_TRACK; ++i) {
			Assert.assertEquals("Wrong number of notes for line " + i, NOTES_IN_LINE,
					track.getLine(i).size());
		}
	}

}
