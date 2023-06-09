package com.github.nianna.karedi.context;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.audio.AudioFileLoader;
import com.github.nianna.karedi.audio.CachedAudioFile;
import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class AudioContext {

    private static final Logger LOGGER = Logger.getLogger(AudioContext.class.getName());

    private final SongPlayer player;

    private final BeatRangeContext beatRangeContext;

    private final BooleanBinding activeAudioIsNull;

    public AudioContext(BeatRangeContext beatRangeContext) {
        this.player = new SongPlayer(beatRangeContext.getBeatMillisConverter());
        this.beatRangeContext = beatRangeContext;
        activeAudioIsNull = player.activeAudioFileProperty().isNull();
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

    public boolean isTickingEnabled() {
        return player.isTickingEnabled();
    }

    public void setTickingEnabled(boolean enabled) {
        player.setTickingEnabled(enabled);
    }

    public CachedAudioFile getActiveAudioFile() {
        return player.getActiveAudioFile();
    }

    public void removeAudioFile(CachedAudioFile file) {
        player.removeAudioFile(file);
    }

    public void loadAudioFile(File file) {
        AudioFileLoader.loadMp3File(file, (newAudio -> {
            if (newAudio.isPresent()) {
                player.addAudioFile(newAudio.get());
                setActiveAudioFile(newAudio.get());
                LOGGER.info(I18N.get("import.audio.success"));
            } else {
                LOGGER.severe(I18N.get("import.audio.fail"));
            }
        }));
    }

    public void setActiveAudioFile(CachedAudioFile file) {
        if (file != getActiveAudioFile()) {
            stop();
            player.setActiveAudioFile(file);
            beatRangeContext.setMaxTime(file == null ? null : file.getDuration());
        }
    }

    public ReadOnlyObjectProperty<CachedAudioFile> activeAudioFileProperty() {
        return player.activeAudioFileProperty();
    }

    public ObservableList<CachedAudioFile> getAudioFiles() {
        return player.getAudioFiles();
    }

    public BooleanBinding getActiveAudioIsNull() {
        return activeAudioIsNull;
    }
}
