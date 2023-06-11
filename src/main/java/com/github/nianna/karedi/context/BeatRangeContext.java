package com.github.nianna.karedi.context;

import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.util.BeatMillisConverter;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class BeatRangeContext {

    private final InvalidationListener beatMillisConverterInvalidationListener = obs -> onBeatMillisConverterInvalidated();

    private final BeatMillisConverter beatMillisConverter = new BeatMillisConverter(Song.DEFAULT_GAP, Song.DEFAULT_BPM);

    private final BeatRange beatRange = new BeatRange(beatMillisConverter);

    private ReadOnlyObjectProperty<Song> activeSongProperty;

    public BeatRangeContext(ActiveSongContext activeSongContext) {
        this.activeSongProperty = activeSongContext.activeSongProperty();
        ChangeListener<Song> songListener = (property, oldSong, newSong) -> onSongChanged(oldSong, newSong);
        activeSongProperty.addListener(songListener);
    }

    public Integer getMinBeat() {
        return beatRange.getMinBeat();
    }

    public ReadOnlyIntegerProperty minBeatProperty() {
        return beatRange.minBeatProperty();
    }

    public Integer getMaxBeat() {
        return beatRange.getMaxBeat();
    }

    public ReadOnlyIntegerProperty maxBeatProperty() {
        return beatRange.maxBeatProperty();
    }

    public void setMaxTime(Long maxTime) {
        beatRange.setMaxTime(maxTime);
    }

    public BeatMillisConverter getBeatMillisConverter() {
        return beatMillisConverter;
    }

    public long beatToMillis(int beat) {
        return beatMillisConverter.beatToMillis(beat);
    }

    private void onBeatMillisConverterInvalidated() {
        if (isNull(activeSongProperty.get())) {
            beatMillisConverter.setBpm(Song.DEFAULT_BPM);
            beatMillisConverter.setGap(Song.DEFAULT_GAP);
        } else {
            beatMillisConverter.setBpm(activeSongProperty.get().getBpm());
            beatMillisConverter.setGap(activeSongProperty.get().getGap());
        }
    }

    private void onSongChanged(Song oldSong, Song newSong) {
        if (nonNull(oldSong)) {
            oldSong.getBeatMillisConverter().removeListener(beatMillisConverterInvalidationListener);
        }
        if (nonNull(newSong)) {
            newSong.getBeatMillisConverter().addListener(beatMillisConverterInvalidationListener);
        }
        beatRange.setBounds(newSong);
        onBeatMillisConverterInvalidated();
    }
}
