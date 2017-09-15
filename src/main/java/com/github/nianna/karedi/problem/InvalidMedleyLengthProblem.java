package main.java.com.github.nianna.karedi.problem;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.tag.TagKey;

public class InvalidMedleyLengthProblem extends TagProblem {
	public static final String TITLE = I18N.get("problem.medley.invalid_length.title");
	public static final int MIN_LENGTH = 1;
	public static final int MAX_LENGTH = 60;

	private double length;

	public InvalidMedleyLengthProblem(double length) {
		super(Severity.ERROR, TITLE, TagKey.MEDLEYSTARTBEAT, TagKey.MEDLEYENDBEAT);
		setDescription(
				I18N.get("problem.medley.invalid_length.description", MIN_LENGTH, MAX_LENGTH));
		this.length = length;
	}

	@Override
	public String toString() {
		return I18N.get("problem.medley.invalid_length.full_title", length);
	}
}
