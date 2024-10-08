package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.dialog.ChooseTracksDialog;
import com.github.nianna.karedi.dialog.ExportWithErrorsAlert;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.MultiplayerTags;
import com.github.nianna.karedi.song.tag.Tag;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ExportTracksAction extends ContextfulKarediAction {

    private int trackCount;

    ExportTracksAction(AppContext appContext, int trackCount) {
        super(appContext);
        this.trackCount = trackCount;
        activeSongContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal != null) {
                setDisabledCondition(newVal.trackCount().lessThan(trackCount));
            } else {
                setDisabledCondition(true);
            }
        });
        setDisabledCondition(true);
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (!activeSongContext.getSong().getProblems().isEmpty()) {
            new ExportWithErrorsAlert().showAndWait().filter(result -> result == ButtonType.OK)
                    .ifPresent(ok -> export());
        } else {
            export();
        }
    }

    private void export() {
        List<SongTrack> tracks = activeSongContext.getSong().getTracks();
        if (tracks.size() != trackCount) {
            ChooseTracksDialog dialog = new ChooseTracksDialog(tracks, trackCount);
            dialog.select(activeSongContext.getActiveTrack());
            Optional<List<SongTrack>> result = dialog.showAndWait();
            if (result.isPresent()) {
                tracks = result.get();
            } else {
                return;
            }
        }

        File file = KarediApp.getInstance().getTxtFileToSave(getInitialFileName());
        txtContext.exportSongToFile(file, prepareTags(tracks), tracks);
    }

    private List<Tag> prepareTags(List<SongTrack> tracks) {
        Song song = activeSongContext.getSong();
        List<Tag> tags = nonMultiplayerTags(song);
        if (tracks.size() > 1) {
            tags.addAll(recreatedMultiplayerTags(song, tracks));
        }
        return tags;
    }

    private static List<Tag> nonMultiplayerTags(Song song) {
        return song.getTags().stream()
                .filter(tag -> !MultiplayerTags.isANameTag(tag))
                .collect(Collectors.toList());
    }

    private static List<Tag> recreatedMultiplayerTags(Song song, List<SongTrack> tracks) {
        return IntStream.range(0, tracks.size())
                .mapToObj(i ->
                        new Tag(
                                MultiplayerTags.getTagKeyForTrackNumber(i, song.getFormatSpecificationVersion()),
                                tracks.get(i).getName()
                        )
                )
                .toList();
    }

    private String getInitialFileName() {
        File file = txtContext.getActiveFile();
        return file == null ? "" : file.getName();
    }

}
