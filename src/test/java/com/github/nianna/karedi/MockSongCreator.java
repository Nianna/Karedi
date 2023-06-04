package test.java.com.github.nianna.karedi;

import java.util.ArrayList;
import java.util.List;

import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;

public class MockSongCreator {
	private static final int NOTE_LENGTH = 2;
	private static final int NOTE_INTERVAL = 4;

	public static Song createSong(int tracks, int linesInTrack, int notesInLine) {
		Song song = new Song();
		for (int trackNr = 1; trackNr <= tracks; ++trackNr) {
			song.addTrack(createTrack(trackNr, linesInTrack, notesInLine));
		}
		return song;
	}

	public static SongTrack createTrack(int trackNr, int linesInTrack, int notesInLine) {
		SongTrack track = new SongTrack(trackNr);
		for (int lineNr = 0; lineNr < linesInTrack; ++lineNr) {
			track.addLine(createLine(lineNr, notesInLine));
		}
		return track;
	}

	public static SongLine createLine(int lineNr, int notesInLine) {
		List<Note> list = new ArrayList<>();
		int lineLength = NOTE_INTERVAL * notesInLine;
		int offset = lineLength * lineNr;
		for (int noteNr = 0; noteNr < notesInLine; ++noteNr) {
			list.add(new Note(offset + NOTE_INTERVAL * noteNr, NOTE_LENGTH));
		}
		return new SongLine(offset, list);
	}

}
