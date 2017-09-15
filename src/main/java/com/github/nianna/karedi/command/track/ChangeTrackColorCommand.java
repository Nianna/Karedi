package main.java.com.github.nianna.karedi.command.track;

import javafx.scene.paint.Color;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.ChangePropertyCommand;
import main.java.com.github.nianna.karedi.song.SongTrack;

public class ChangeTrackColorCommand extends ChangePropertyCommand<Color> {

	public ChangeTrackColorCommand(SongTrack track, Color newValue) {
		super(I18N.get("command.change_track_color"), track::getColor, track::setColor, newValue);
	}

}
