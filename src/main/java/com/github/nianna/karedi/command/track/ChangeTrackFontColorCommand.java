package main.java.com.github.nianna.karedi.command.track;

import javafx.scene.paint.Color;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.ChangePropertyCommand;
import main.java.com.github.nianna.karedi.song.SongTrack;


/**
 * @author Naucé López González
 */
public class ChangeTrackFontColorCommand extends ChangePropertyCommand<Color> {

    public ChangeTrackFontColorCommand(SongTrack track, Color newValue) {
        super(I18N.get("command.change_track_font_color"), track::getFontColor,
                track::setFontColor, newValue);
    }

}
