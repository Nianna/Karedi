package com.github.nianna.karedi.context;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.audio.AudioFileLoader;
import com.github.nianna.karedi.audio.CachedAudioFile;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.logging.Logger;

public class AudioContext {

    private static final Logger LOGGER = Logger.getLogger(AudioContext.class.getName());

    private final SongPlayer songPlayer;

    private final AppContext appContext;

    private final BooleanBinding activeAudioIsNull;

    private final BeatRangeContext beatRangeContext;

    AudioContext(AppContext appContext, SongPlayer songPlayer) {
        this.appContext = appContext;
        this.beatRangeContext = appContext.beatRangeContext;
        this.songPlayer = songPlayer;
        activeAudioIsNull = songPlayer.activeAudioFileProperty().isNull();
    }

    public CachedAudioFile getActiveAudioFile() {
        return songPlayer.getActiveAudioFile();
    }

    public void removeAudioFile(CachedAudioFile file) {
        songPlayer.removeAudioFile(file);
    }

    public void loadAudioFile(File file) {
        AudioFileLoader.loadMp3File(file, (newAudio -> {
            if (newAudio.isPresent()) {
                songPlayer.addAudioFile(newAudio.get());
                setActiveAudioFile(newAudio.get());
                LOGGER.info(I18N.get("import.audio.success"));
            } else {
                LOGGER.severe(I18N.get("import.audio.fail"));
            }
        }));
    }

    public void setActiveAudioFile(CachedAudioFile file) {
        if (file != getActiveAudioFile()) {
            appContext.actionContext.execute(KarediActions.STOP_PLAYBACK);
            songPlayer.setActiveAudioFile(file);
            beatRangeContext.setMaxTime(file == null ? null : file.getDuration());
        }
    }

    public ReadOnlyObjectProperty<CachedAudioFile> activeAudioFileProperty() {
        return songPlayer.activeAudioFileProperty();
    }

    public ObservableList<CachedAudioFile> getAudioFiles() {
        return songPlayer.getAudioFiles();
    }

    public BooleanBinding getActiveAudioIsNull() {
        return activeAudioIsNull;
    }
}
