package com.github.nianna.karedi.problem;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.TagKey;

public class NotesAfterEndProblem extends BoundaryTagProblem {
	public static final String TITLE = I18N.get("problem.note.after_end.title");

	public NotesAfterEndProblem(Song song, long endMillis) {
		super(Severity.ERROR, TITLE, TagKey.END, song, song.millisToBeat(endMillis),
				song.getUpperXBound());
		setDescription(I18N.get("problem.note.after_end.description"));

	}

}
