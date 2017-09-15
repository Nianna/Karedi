package main.java.com.github.nianna.karedi.problem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.song.tag.TagKey;

public abstract class TagProblem extends Problem {
	private TagKey[] keys;

	public TagProblem(Severity severity, String title, TagKey... keys) {
		super(severity, title);
		this.keys = keys;
	}

	@Override
	public Optional<? extends IntBounded> getAffectedBounds() {
		return Optional.empty();
	}

	@Override
	public Optional<SongTrack> getTrack() {
		return Optional.empty();
	}

	@Override
	public List<? extends Object> getElements() {
		return Arrays.asList(keys);
	}

}
