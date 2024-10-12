package com.github.nianna.karedi.problem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.TagKey;

public abstract class TagProblem extends Problem {
	private String[] keys;

	public TagProblem(Severity severity, String title, TagKey... keys) {
		super(severity, title);
		this.keys = Stream.of(keys).map(TagKey::toString).toArray(String[]::new);
	}

	public TagProblem(Severity severity, String title, String... keys) {
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
		return getAffectedKeys();
	}

	public List<String> getAffectedKeys() {
		return Arrays.asList(keys);
	}

}
