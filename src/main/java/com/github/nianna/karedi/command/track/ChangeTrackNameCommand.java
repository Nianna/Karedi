package main.java.com.github.nianna.karedi.command.track;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.ChangePropertyCommand;
import main.java.com.github.nianna.karedi.song.SongTrack;

public class ChangeTrackNameCommand extends ChangePropertyCommand<String> {

	public ChangeTrackNameCommand(SongTrack track, String newValue) {
		super(I18N.get("command.change_track_name"), track::getName, track::setName, newValue);
	}

}
