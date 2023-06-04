package com.github.nianna.karedi.problem;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.tag.TagKey;

public class InvalidMedleyBeatRangeProblem extends TagProblem {
	public static final String TITLE = I18N.get("problem.medley.invalid_range.title");

	public InvalidMedleyBeatRangeProblem() {
		super(Severity.ERROR, TITLE, TagKey.MEDLEYSTARTBEAT, TagKey.MEDLEYENDBEAT);
		setDescription(I18N.get("problem.medley.invalid_range.description"));
	}

}
