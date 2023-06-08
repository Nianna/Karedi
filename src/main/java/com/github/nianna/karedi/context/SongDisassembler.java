package com.github.nianna.karedi.context;

import com.github.nianna.karedi.parser.element.EndOfSongElement;
import com.github.nianna.karedi.parser.element.LineBreakElement;
import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.NoteElementType;
import com.github.nianna.karedi.parser.element.TagElement;
import com.github.nianna.karedi.parser.element.TrackElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SongDisassembler {

	public List<VisitableSongElement> disassemble(Song song) {
		return disassemble(song.getTags(), song.getTracks());
	}

	public List<VisitableSongElement> disassemble(List<Tag> tags, List<SongTrack> tracks) {
		List<VisitableSongElement> elts = disassembleTags(tags);
		elts.addAll(disassembleTracks(tracks));
		elts.add(new EndOfSongElement());
		return elts;
	}

	public List<VisitableSongElement> disassembleTags(List<Tag> tags) {
		return tags.stream().map(this::disassemble).collect(Collectors.toList());
	}

	public List<VisitableSongElement> disassembleTracks(List<SongTrack> tracks) {
		List<VisitableSongElement> elts = new ArrayList<>();
		int trackNumber = 1;
		for (SongTrack track : tracks) {
			if (trackNumber > 1 || tracks.size() > 1) {
				elts.add(new TrackElement(trackNumber++));
			}
			elts.addAll(disassemble(track));
		}
		return elts;
	}

	public List<VisitableSongElement> disassembleLines(List<SongLine> lines) {
		return lines.stream().flatMap(line -> {
			List<VisitableSongElement> elts = disassemble(line);
			line.getPrevious().ifPresent(prevLine -> {
				elts.add(0, new LineBreakElement(line.getLineBreak()));
			});
			return elts.stream();
		}).collect(Collectors.toList());
	}

	public List<VisitableSongElement> disassemble(SongTrack track) {
		return disassembleLines(track.getLines());
	}

	public List<VisitableSongElement> disassemble(SongLine line) {
		return line.getNotes().stream().map(this::disassemble).collect(Collectors.toList());
	}

	public NoteElement disassemble(Note note) {
		String lyrics = note.getLyrics();
		if (note.isFirstInLine()) {
			lyrics = lyrics.trim();
		}
		return new NoteElement(noteElementType(note.getType()), note.getStart(), note.getLength(),
				note.getTone(), lyrics);
	}

	private TagElement disassemble(Tag tag) {
		return new TagElement(tag.getKey(), tag.getValue());
	}

	private NoteElementType noteElementType(Note.Type type) {
		return switch (type) {
			case GOLDEN -> NoteElementType.GOLDEN;
			case FREESTYLE -> NoteElementType.FREESTYLE;
			case RAP -> NoteElementType.RAP;
			case GOLDEN_RAP -> NoteElementType.GOLDEN_RAP;
			default -> NoteElementType.NORMAL;
		};
	}
}
