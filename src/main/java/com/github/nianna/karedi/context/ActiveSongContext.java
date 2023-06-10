package com.github.nianna.karedi.context;

import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.util.ListenersUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;

public class ActiveSongContext {

    private final ReadOnlyObjectWrapper<Song> activeSong = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyObjectWrapper<SongTrack> activeTrack = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyObjectWrapper<SongLine> activeLine = new ReadOnlyObjectWrapper<>();

    private final SongNormalizer songNormalizer = new SongNormalizer();

    private final ListChangeListener<? super SongLine> lineListChangeListener = ListenersUtils
            .createListContentChangeListener(ListenersUtils::pass, this::onLineRemoved);

    private final BooleanBinding activeSongIsNull = activeSongProperty().isNull();

    private final BooleanBinding activeTrackIsNull = activeTrackProperty().isNull();

    private final IntegerProperty activeSongTrackCount = new SimpleIntegerProperty();

    private final BooleanBinding activeSongHasOneOrZeroTracks = activeSongTrackCount
            .lessThanOrEqualTo(1);

    public ReadOnlyObjectProperty<Song> activeSongProperty() {
        return activeSong.getReadOnlyProperty();
    }

    public final Song getSong() {
        return activeSongProperty().get();
    }

    public final void setSong(Song song) {
        Song oldSong = getSong();
        songNormalizer.normalize(song);
        // The song has at least one track now
        if (song != oldSong) {
            activeSong.set(song);
            if (oldSong != null) {
                activeSongTrackCount.unbind();
            }
            if (song == null) {
                setActiveTrack(null);
                activeSongTrackCount.set(0);
            } else {
                activeSongTrackCount.bind(song.trackCount());
                setActiveTrack(song.getDefaultTrack().orElse(null));
            }
        }
    }

    public ReadOnlyObjectProperty<SongTrack> activeTrackProperty() {
        return activeTrack.getReadOnlyProperty();
    }

    public final SongTrack getActiveTrack() {
        return activeTrack.get();
    }

    public final void setActiveTrack(SongTrack track) {
        SongTrack oldTrack = getActiveTrack();
        if (track != oldTrack) {
            activeTrack.set(track);
            setActiveLine(null);
            if (oldTrack != null) {
                oldTrack.removeLineListListener(lineListChangeListener);
            }
            if (track != null) {
                track.addLineListListener(lineListChangeListener);
                track.setVisible(true);
                track.setMuted(false);
                if (oldTrack == null) {
                    setActiveLine(track.getDefaultLine());
                }
            } else {
                assert (getSong() == null);
            }
        }
    }

    public ReadOnlyObjectProperty<SongLine> activeLineProperty() {
        return activeLine.getReadOnlyProperty();
    }

    public final SongLine getActiveLine() {
        return activeLine.get();
    }

    public final void setActiveLine(SongLine line) {
        SongLine oldLine = getActiveLine();
        if (line != oldLine) {
//			visibleAreaContext.onLineDeactivated(oldLine);
            activeLine.set(line);
//			visibleAreaContext.onLineActivated(line);
        }
    }

    private void onLineRemoved(SongLine line) {
        if (line == getActiveLine()) {
            setActiveLine(null);
        }
    }

    public BooleanBinding activeSongIsNullBinding() {
        return activeSongIsNull;
    }

    public BooleanBinding activeTrackIsNullBinding() {
        return activeTrackIsNull;
    }

    public BooleanBinding activeSongHasOneOrZeroTracks() {
        return activeSongHasOneOrZeroTracks;
    }
}
