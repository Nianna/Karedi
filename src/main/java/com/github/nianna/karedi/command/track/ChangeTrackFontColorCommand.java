package com.github.nianna.karedi.command.track;

import javafx.scene.paint.Color;
import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.Settings;
import com.github.nianna.karedi.command.ChangePropertyCommand;
import com.github.nianna.karedi.song.SongTrack;


/**
 * @author Naucé López González
 */
public class ChangeTrackFontColorCommand extends ChangePropertyCommand<Color> {

    private final int trackNumber;

    public ChangeTrackFontColorCommand(SongTrack track, Color newValue) {
        super(I18N.get("command.change_track_font_color"), track::getFontColor, track::setFontColor, newValue);
        this.trackNumber = track.getPlayer();
    }

    @Override
    public boolean execute() {
        Settings.saveTrackFontColor(newValue, trackNumber);
        return super.execute();
    }

    @Override
    public void undo() {
        Settings.saveTrackColor(oldValue, trackNumber);
        super.undo();
    }

    @Override
    public boolean requiresSave() {
        return false;
    }
}
