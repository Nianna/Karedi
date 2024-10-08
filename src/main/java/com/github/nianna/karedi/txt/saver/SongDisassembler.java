package com.github.nianna.karedi.txt.saver;

import com.github.nianna.karedi.Settings;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;
import com.github.nianna.karedi.txt.parser.element.LineBreakElement;
import com.github.nianna.karedi.txt.parser.element.NoteElement;
import com.github.nianna.karedi.txt.parser.element.NoteElementType;
import com.github.nianna.karedi.txt.parser.element.TagElement;
import com.github.nianna.karedi.txt.parser.element.TrackElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.util.LyricsHelper;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SongDisassembler {

	public List<VisitableSongElement> disassemble(List<Tag> tags, List<SongTrack> tracks) {
		return Stream.of(
						disassembleTags(tags),
						disassembleTracks(tracks),
						endSong()
				)
				.flatMap(Function.identity())
				.collect(Collectors.toList());
	}

	private Stream<VisitableSongElement> disassembleTags(List<Tag> tags) {
		return tags.stream()
				.map(this::disassemble);
	}

	private Stream<VisitableSongElement> disassembleTracks(List<SongTrack> tracks) {
		boolean shouldSkipFirstPlayerTag = tracks.size() == 1;
		return tracks.stream()
				.flatMap(track -> disassemble(track, tracks.indexOf(track)))
				.skip(shouldSkipFirstPlayerTag ? 1 : 0);
	}

	private Stream<VisitableSongElement> disassemble(SongTrack track, int trackIndex) {
		return Stream.concat(
				Stream.of(new TrackElement(trackIndex + 1)),
				disassembleLines(track.getLines())
		);
	}

	private Stream<VisitableSongElement> disassembleLines(List<SongLine> lines) {
		return lines.stream()
				.flatMap(this::disassembleLine)
				.skip(1);
	}

	private Stream<VisitableSongElement> disassembleLine(SongLine line) {
		return Stream.concat(
				Stream.of(new LineBreakElement(line.getLineBreak())),
				disassembleNotes(line.getNotes())
		);
	}

	private Stream<VisitableSongElement> disassembleNotes(List<Note> notes) {
		return notes
				.stream()
				.map(this::disassemble);
	}

	public NoteElement disassemble(Note note) {
		String lyrics = note.getLyrics();
		if (note.isFirstInLine() || Settings.isPlaceSpacesAfterWords()) {
			lyrics = lyrics.trim();
		}
		boolean shouldMoveSpaceFromNextNote = Settings.isPlaceSpacesAfterWords()
				&& note.getNextInLine().map(Note::getLyrics).filter(LyricsHelper::startsNewWord).isPresent();
		if (shouldMoveSpaceFromNextNote) {
			lyrics = lyrics + LyricsHelper.WORD_SEPARATOR;
		}
		return new NoteElement(
				noteElementType(note.getType()),
				note.getStart(),
				note.getLength(),
				note.getTone(),
				lyrics
		);
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
			case NORMAL -> NoteElementType.NORMAL;
		};
	}

	private Stream<VisitableSongElement> endSong() {
		return Stream.of(new EndOfSongElement());
	}

}
