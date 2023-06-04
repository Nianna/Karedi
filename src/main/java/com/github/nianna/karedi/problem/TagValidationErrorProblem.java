package com.github.nianna.karedi.problem;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.tag.TagKey;

public class TagValidationErrorProblem extends TagProblem {
	public static final String TITLE = I18N.get("problem.tag.validation_fail.title");
	private TagKey key;

	public TagValidationErrorProblem(TagKey key, Severity severity, String description) {
		super(severity, TITLE, key);
		this.key = key;
		setDescription(description);
	}

	@Override
	public String toString() {
		return I18N.get("problem.tag.validation_fail.full_title", key);
	}

}
