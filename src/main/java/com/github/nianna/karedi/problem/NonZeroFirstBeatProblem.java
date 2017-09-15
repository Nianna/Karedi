package main.java.com.github.nianna.karedi.problem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandComposite;
import main.java.com.github.nianna.karedi.command.MoveOneCommand;
import main.java.com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.util.Converter;

public class NonZeroFirstBeatProblem extends IntBoundedProblem {
	public static final String TITLE = I18N.get("problem.non_zero_first_beat.title");
	private SongTrack track;

	public NonZeroFirstBeatProblem(SongTrack track) {
		super(Severity.ERROR, TITLE, track, track.getLine(0).getFirst());
		this.track = track;
		setDescription(I18N.get("problem.non_zero_first_beat.description"));
	}

	@Override
	public Optional<Command> getSolution() {
		int by = Math.abs(track.getLowerXBound());
		Direction direction = track.getLowerXBound() > 0 ? Direction.LEFT : Direction.RIGHT;
		return rename(Optional.of(new CommandComposite("") {

			@Override
			protected void buildSubCommands() {
				Song song = track.getSong();
				song.getTracks().forEach(track -> {
					addSubCommand(new MoveOneCommand<>(track, direction, by));
				});
				long newGap = song.beatToMillis(track.getLowerXBound());
				addSubCommand(
						new ChangeTagValueCommand(song, TagKey.GAP, Converter.toString(newGap)));
			}

		}), false);

	}

	@Override
	public List<? extends Object> getElements() {
		return Arrays.asList(track);
	}

}
