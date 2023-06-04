package com.github.nianna.karedi.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.nianna.karedi.parser.element.EndOfSongElement;
import com.github.nianna.karedi.parser.element.LineBreakElement;
import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.NoteElement.Type;
import com.github.nianna.karedi.parser.element.SongElementVisitor;
import com.github.nianna.karedi.parser.element.TagElement;
import com.github.nianna.karedi.parser.element.TrackElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.util.LyricsHelper;

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
		note.setType(convertType(type));

		return note;
	}
	
	private Note.Type convertType(Type type) {
		switch (type) {
			case GOLDEN:
				return Note.Type.GOLDEN;
			case FREESTYLE:
				return Note.Type.FREESTYLE;
			case RAP:
				return Note.Type.RAP;
			case GOLDEN_RAP:
				return Note.Type.GOLDEN_RAP;
			default:
				return Note.Type.NORMAL;
		}
	}
		
}
