package com.github.nianna.karedi.context;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.txt.TxtFacade;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class TxtContext {

    private static final Logger LOGGER = Logger.getLogger(TxtContext.class.getName());

    private final ReadOnlyObjectWrapper<File> activeFile = new ReadOnlyObjectWrapper<>();

    private final TxtFacade txtFacade = new TxtFacade();

    private final BooleanBinding activeFileIsNull = activeFile.isNull();

    private final ActiveSongContext activeSongContext;

    private final AudioContext audioContext;

    private final CommandContext commandContext;

    public TxtContext(ActiveSongContext activeSongContext, AudioContext audioContext, CommandContext commandContext) {
        this.activeSongContext = activeSongContext;
        this.audioContext = audioContext;
        this.commandContext = commandContext;
    }

    public File getActiveFile() {
        return activeFile.get();
    }

    public void setActiveFile(File file) {
        this.activeFile.set(file);
        KarediApp.getInstance().updatePrimaryStageTitle(file);
    }

    public void loadSongFile(File file) {
        loadSongFile(file, true);
    }

    public void loadSongFile(File file, boolean resetPlayer) {
        if (file == null) {
            return;
        }

        if (file.exists()) {
            reset(resetPlayer);
            setActiveFile(file);
            Song song = txtFacade.loadFromTxtFile(file);
            song.getMainAudioTagValue().ifPresent(filename -> loadAudioFileWithName(file, filename, true));
            Stream.of(song.getTagValue(TagKey.INSTRUMENTAL), song.getTagValue(TagKey.VOCALS))
                    .flatMap(Optional::stream)
                    .forEach(filename -> loadAudioFileWithName(file, filename, false));
            activeSongContext.setSong(song);
            LOGGER.info(I18N.get("load.success"));
        } else {
            LOGGER.severe(I18N.get("load.fail"));
        }
    }

    private void loadAudioFileWithName(File txtFile, String filename, boolean setAsDefault) {
        audioContext.loadAudioFile(new File(txtFile.getParent(), filename), setAsDefault);
    }

    public boolean saveSongToFile(File file) {
        if (file != null) {
            if (txtFacade.saveSongToFile(file, activeSongContext.getSong())) {
                commandContext.updateLastSavedCommand();
                return true;
            }
        }
        return false;
    }

    public void exportSongToFile(File file, ObservableList<Tag> tags, List<SongTrack> tracks) {
        txtFacade.exportSongToFile(file, tags, tracks);
    }

    public Song loadFromClipboard() {
        return txtFacade.loadFromClipboard();
    }

    public void saveToClipboard(List<Note> selectedNotes) {
        txtFacade.saveToClipboard(selectedNotes);
    }

    public boolean needsSaving() {
        return activeSongContext.getSong() != null && commandContext.hasUnsavedCommands();
    }

    public BooleanBinding activeFileIsNullBinding() {
        return activeFileIsNull;
    }

    public void reset(boolean resetPlayer) {
        activeSongContext.setSong(null);
        if (resetPlayer) {
            audioContext.reset();
        }
    }
}
