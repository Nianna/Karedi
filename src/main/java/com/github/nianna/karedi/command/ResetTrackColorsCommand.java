package main.java.com.github.nianna.karedi.command;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.Settings;
import main.java.com.github.nianna.karedi.command.track.ChangeTrackColorCommand;
import main.java.com.github.nianna.karedi.command.track.ChangeTrackFontColorCommand;
import main.java.com.github.nianna.karedi.song.SongTrack;

public class ResetTrackColorsCommand extends CommandComposite {

    private final SongTrack songTrack;

    public ResetTrackColorsCommand(SongTrack songTrack) {
        super(I18N.get("command.reset_track_colors"));
        this.songTrack = songTrack;
    }

    @Override
    protected void buildSubCommands() {
        addSubCommand(new ChangeTrackColorCommand(songTrack, Settings.getDefaultTrackColor(songTrack.getPlayer())));
        addSubCommand(new ChangeTrackFontColorCommand(songTrack, Settings.getDefaultTrackFontColor(songTrack.getPlayer())));
    }
}
