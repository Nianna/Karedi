package com.github.nianna.karedi.problem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.song.Note.Type;
import com.github.nianna.karedi.song.SongTrack;

public class UncommonGoldenBonusProblem extends Problem {
	public static final int MIN_GOLDEN_BONUS_POINTS = 1000;
	public static final int MAX_GOLDEN_BONUS_POINTS = 1500;

	public static final String TITLE = I18N.get("problem.uncommon_golden_bonus.title");

	private SongTrack track;

	public UncommonGoldenBonusProblem(SongTrack track) {
		super(Severity.WARNING, TITLE);
		setDescription(I18N.get("problem.uncommon_golden_bonus.description", MIN_GOLDEN_BONUS_POINTS, MAX_GOLDEN_BONUS_POINTS));
		this.track = track;
	}

	@Override
	public List<? extends Object> getElements() {
		return Arrays.asList(track);
	}

	@Override
	public Optional<? extends IntBounded> getAffectedBounds() {
		return track.getNotes().stream().filter(note -> note.getType() == Type.GOLDEN).findFirst();
	}

	@Override
	public String toString() {
		return I18N.get("problem.uncommon_golden_bonus.full_title", track.getGoldenBonusPoints());
	}

	@Override
	public Optional<SongTrack> getTrack() {
		return Optional.of(track);
	}

}
