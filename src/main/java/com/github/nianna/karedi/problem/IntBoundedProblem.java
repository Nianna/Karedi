package main.java.com.github.nianna.karedi.problem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.SongTrack;

public abstract class IntBoundedProblem extends Problem {
	private List<IntBounded> children;
	private SongTrack track;

	public IntBoundedProblem(Severity severity, String title, SongTrack track,
			IntBounded... bounded) {
		super(severity, title);
		this.children = Arrays.asList(bounded);
		this.track = track;
	}

	public IntBoundedProblem(Severity severity, String title, SongTrack track, Solvable solver,
			IntBounded... bounded) {
		this(severity, title, track, bounded);
		setSolver(solver);
	}

	@Override
	public Optional<? extends IntBounded> getAffectedBounds() {
		return Optional.of(new BoundingBox<>(children));
	}

	@Override
	public List<? extends Object> getElements() {
		return children;
	}

	@Override
	public Optional<SongTrack> getTrack() {
		return Optional.ofNullable(track);
	}
}
