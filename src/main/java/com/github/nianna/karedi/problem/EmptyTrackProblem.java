package main.java.com.github.nianna.karedi.problem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.SongTrack;

//TODO is it currently not used?
public class EmptyTrackProblem extends Problem {
	public static final String TITLE = I18N.get("problem.track.empty.title");
	private SongTrack track;

	public EmptyTrackProblem(SongTrack track) {
		super(Severity.WARNING, TITLE);
		setDescription(I18N.get("problem.track.empty.description", track.getName()));
		this.track = track;
	}

	@Override
	public List<? extends Object> getElements() {
		return Arrays.asList(track);
	}

	@Override
	public Optional<? extends IntBounded> getAffectedBounds() {
		return Optional.of(track.getBounds());
	}

	@Override
	public Optional<SongTrack> getTrack() {
		return Optional.ofNullable(track);
	}

}
