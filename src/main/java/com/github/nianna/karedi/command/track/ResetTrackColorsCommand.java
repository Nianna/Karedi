package com.github.nianna.karedi.command.track;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.Settings;
import com.github.nianna.karedi.command.CommandComposite;
import com.github.nianna.karedi.song.SongTrack;

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
