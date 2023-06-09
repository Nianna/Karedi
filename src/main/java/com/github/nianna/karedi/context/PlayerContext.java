package com.github.nianna.karedi.context;

import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.List;

public class PlayerContext {

    public final SongPlayer player;

    public PlayerContext(BeatRangeContext beatRangeContext) {
        this.player = new SongPlayer(beatRangeContext.getBeatMillisConverter());
    }

    public ReadOnlyObjectProperty<Player.Status> playerStatusProperty() {
        return player.statusProperty();
    }

    public Player.Status getPlayerStatus() {
        return player.getStatus();
    }

    public void playAllAudible(int fromBeat, int toBeat, Player.Mode mode) {
        player.play(fromBeat, toBeat, mode);
    }

    public void playGivenNotes(int fromBeat, int toBeat, List<Note> notes, Player.Mode mode) {
        player.play(fromBeat, toBeat, notes, mode);
    }

    // Marker
    public ReadOnlyIntegerProperty markerBeatProperty() {
        return player.markerBeatProperty();
    }

    public int getMarkerBeat() {
        return player.getMarkerBeat();
    }

    public void setMarkerBeat(int beat) {
        player.setMarkerBeat(beat);
    }

    public ReadOnlyLongProperty markerTimeProperty() {
        return player.markerTimeProperty();
    }

    public Long getMarkerTime() {
        return player.getMarkerTime();
    }

    public void setMarkerTime(long time) {
        player.setMarkerTime(time);
    }

    public void setSong(Song song) {
        player.setSong(song);
    }

    public void stop() {
        player.stop();
    }

    public void reset() {
        player.reset();
    }

    public ReadOnlyObjectProperty<Player.Status> statusProperty() {
        return player.statusProperty();
    }

    public boolean isTickingEnabled() {
        return player.isTickingEnabled();
    }

    public void setTickingEnabled(boolean enabled) {
        player.setTickingEnabled(enabled);
    }
}
