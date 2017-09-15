package main.java.com.github.nianna.karedi.problem;

import java.util.Optional;

import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.song.tag.TagKey;

public abstract class BoundaryTagProblem extends TagProblem {
	private IntBounded affectedBounds;
	private SongTrack affectedTrack;

	public BoundaryTagProblem(Severity severity, String title, TagKey key, Song song, int minBeat,
			int maxBeat) {
		super(severity, title, key);
		affectedBounds = BoundingBox.boundsFrom(minBeat, maxBeat, 0, 0);
		affectedTrack = song.getTracks().stream()
				.filter(track -> track.inBoundsX(minBeat) || track.inBoundsX(maxBeat)).findFirst()
				.orElse(null);
	}

	@Override
	public Optional<? extends IntBounded> getAffectedBounds() {
		return Optional.of(affectedBounds);
	}

	@Override
	public Optional<SongTrack> getTrack() {
		return Optional.ofNullable(affectedTrack);
	}
}
