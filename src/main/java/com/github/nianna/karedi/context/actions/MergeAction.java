package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.command.MergeNotesCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Song;
import javafx.event.ActionEvent;

class MergeAction extends ClipboardAction {

    private final MergeNotesCommand.MergeMode mode;

    MergeAction(AppContext appContext, MergeNotesCommand.MergeMode mode) {
        super(appContext);
        disableWhenSelectionEmpty();
        this.mode = mode;
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song pastedSong = buildSongFromClipboard();
        if (pastedSong != null && pastedSong.size() > 0) {
            appContext.execute(new MergeNotesCommand(getSelectedNotes(), pastedSong.get(0).getNotes(), mode));
        }
    }
}
