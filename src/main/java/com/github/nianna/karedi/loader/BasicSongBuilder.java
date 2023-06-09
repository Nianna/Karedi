package com.github.nianna.karedi.loader;

import com.github.nianna.karedi.parser.element.EndOfSongElement;
import com.github.nianna.karedi.parser.element.LineBreakElement;
import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.NoteElementType;
import com.github.nianna.karedi.parser.element.SongElementVisitor;
import com.github.nianna.karedi.parser.element.TagElement;
import com.github.nianna.karedi.parser.element.TrackElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.util.LyricsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BasicSongBuilder implements SongBuilder, SongElementVisitor {

	public static final Integer DEFAULT_TRACK = 1;

	private final Song song = new Song();

	private final Map<Integer, SongTrack> tracks = new HashMap<>();

	private final List<Note> lineNotes = new ArrayList<>();

	private SongTrack currentTrack = new SongTrack(DEFAULT_TRACK);

	private Integer lineStartBeat = 0;

	private boolean buildFinished;

	private boolean firstNoteInLine = true;

	BasicSongBuilder() {
		tracks.put(DEFAULT_TRACK, currentTrack);
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
		lineStartBeat = lineBreakElement.position();
		firstNoteInLine = true;
	}

	@Override
	public void visit(NoteElement noteElement) {
		Note note = getNote(noteElement);
		if (firstNoteInLine) {
			note.setLyrics(LyricsHelper.normalize(" " + note.getLyrics()));
			firstNoteInLine = false;
		} else {
			note.setLyrics(LyricsHelper.normalize(note.getLyrics()));
		}
		lineNotes.add(note);
	}

	@Override
	public void visit(TagElement tagElement) {
		song.setTagValue(tagElement.key(), tagElement.value());
	}

	@Override
	public void visit(TrackElement trackElement) {
		currentTrack.addLine(buildLine());
		Integer trackNumber = trackElement.number();
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

	private SongLine buildLine() {
		SongLine line = new SongLine(lineStartBeat, lineNotes);
		lineNotes.clear();
		lineStartBeat = 0;
		return line;
	}

	private Note getNote(NoteElement noteElement) {
		return new Note(
				noteElement.startsAt(),
				noteElement.length(),
				noteElement.tone(),
				noteElement.lyrics(),
				convertType(noteElement.type())
		);
	}
	
	private Note.Type convertType(NoteElementType type) {
		return switch (type) {
			case GOLDEN -> Note.Type.GOLDEN;
			case FREESTYLE -> Note.Type.FREESTYLE;
			case RAP -> Note.Type.RAP;
			case GOLDEN_RAP -> Note.Type.GOLDEN_RAP;
			case NORMAL -> Note.Type.NORMAL;
		};
	}
		
}
