package main.java.com.github.nianna.karedi.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.github.nianna.karedi.parser.element.EndOfSongElement;
import main.java.com.github.nianna.karedi.parser.element.LineBreakElement;
import main.java.com.github.nianna.karedi.parser.element.NoteElement;
import main.java.com.github.nianna.karedi.parser.element.NoteElement.Type;
import main.java.com.github.nianna.karedi.parser.element.SongElementVisitor;
import main.java.com.github.nianna.karedi.parser.element.TagElement;
import main.java.com.github.nianna.karedi.parser.element.TrackElement;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class BasicSongBuilder implements SongBuilder, SongElementVisitor {
	public static final Integer DEFAULT_TRACK = 1;
	private Song song;
	private Map<Integer, SongTrack> tracks = new HashMap<>();
	private List<Note> lineNotes = new ArrayList<>();

	private SongTrack currentTrack;
	private Integer lineStartBeat;

	private boolean buildFinished;

	private boolean firstNoteInLine = true;

	public BasicSongBuilder() {
		reset();
	}

	@Override
	public Song getResult() {
		if (lineNotes.size() > 0) {
			currentTrack.addLine(buildLine());
		}
		song.setTracks(tracks.values());
		return song;
	}

	@Override
	public void visit(LineBreakElement lineBreakElement) {
		currentTrack.addLine(buildLine());
		lineStartBeat = lineBreakElement.getPosition();
		firstNoteInLine = true;
	}

	@Override
	public void visit(NoteElement noteElement) {
		Note note = getNote(noteElement);
		if (note != null) {
			if (firstNoteInLine) {
				note.setLyrics(LyricsHelper.normalize(" " + note.getLyrics()));
				firstNoteInLine = false;
			} else {
				note.setLyrics(LyricsHelper.normalize(note.getLyrics()));
			}
			lineNotes.add(note);
		}
	}

	@Override
	public void visit(TagElement tagElement) {
		song.setTagValue(tagElement.getKey(), tagElement.getValue());
	}

	@Override
	public void visit(TrackElement trackElement) {
		currentTrack.addLine(buildLine());
		Integer trackNumber = trackElement.getNumber();
		currentTrack = tracks.getOrDefault(trackNumber, new SongTrack(trackNumber));
		tracks.put(trackNumber, currentTrack);
	}

	@Override
	public void visit(EndOfSongElement endOfSongElement) {
		currentTrack.addLine(buildLine());
		buildFinished = true;
	}

	@Override
	public void buildPart(VisitableSongElement element) {
		if (!buildFinished) {
			element.accept(this);
		}
	}

	@Override
	public void reset() {
		tracks.clear();
		lineNotes.clear();
		lineStartBeat = 0;
		currentTrack = new SongTrack(DEFAULT_TRACK);
		tracks.put(DEFAULT_TRACK, currentTrack);
		buildFinished = false;
		song = new Song();
	}

	private SongLine buildLine() {
		SongLine line = new SongLine(lineStartBeat, lineNotes);
		lineNotes.clear();
		lineStartBeat = 0;
		return line;
	}

	private Note getNote(NoteElement noteElement) {
		Type type = noteElement.getType();
		Integer startsAt = noteElement.getStartsAt();
		Integer tone = noteElement.getTone();
		Integer length = noteElement.getLength();
		String lyrics = noteElement.getLyrics();

		Note note = new Note(startsAt, length, tone, lyrics);
		if (type == Type.GOLDEN) {
			note.setType(Note.Type.GOLDEN);
		}
		if (type == Type.FREESTYLE) {
			note.setType(Note.Type.FREESTYLE);
		}
		return note;
	}
}
