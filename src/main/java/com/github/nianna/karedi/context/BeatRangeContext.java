package com.github.nianna.karedi.context;

import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.util.BeatMillisConverter;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyIntegerProperty;

import static java.util.Objects.nonNull;

public class BeatRangeContext {

    private final InvalidationListener beatMillisConverterInvalidationListener = obs -> onBeatMillisConverterInvalidated();

    private final BeatMillisConverter beatMillisConverter = new BeatMillisConverter(Song.DEFAULT_GAP, Song.DEFAULT_BPM);

    private final BeatRange beatRange = new BeatRange(beatMillisConverter);

    private final AppContext appContext;

    public BeatRangeContext(AppContext appContext) {
        this.appContext = appContext;
    }

    // Beat range
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

    public void onSongDeactivated(Song oldSong) {
        oldSong.getBeatMillisConverter()
                .removeListener(beatMillisConverterInvalidationListener);
        onBeatMillisConverterInvalidated();
    }

    public void onSongActivated(Song song) {
        beatRange.setBounds(song);
        if (nonNull(song)) {
            song.getBeatMillisConverter().addListener(beatMillisConverterInvalidationListener);
        }
        onBeatMillisConverterInvalidated();
    }

    public BeatMillisConverter getBeatMillisConverter() {
        return beatMillisConverter;
    }

    public long beatToMillis(int beat) {
        return beatMillisConverter.beatToMillis(beat);
    }

    private void onBeatMillisConverterInvalidated() {
        if (appContext.getSong() == null) {
            beatMillisConverter.setBpm(Song.DEFAULT_BPM);
            beatMillisConverter.setGap(Song.DEFAULT_GAP);
        } else {
            beatMillisConverter.setBpm(appContext.getSong().getBpm());
            beatMillisConverter.setGap(appContext.getSong().getGap());
        }
    }
}
