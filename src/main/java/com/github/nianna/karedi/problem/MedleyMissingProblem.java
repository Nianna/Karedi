package com.github.nianna.karedi.problem;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.tag.TagKey;

public class MedleyMissingProblem extends TagProblem {
	public static final String TITLE = I18N.get("problem.medley.missing.title");

	public MedleyMissingProblem() {
		super(Severity.WARNING, TITLE, TagKey.MEDLEYSTARTBEAT, TagKey.MEDLEYENDBEAT);
		setDescription(I18N.get("problem.medley.missing.description"));
	}

}
