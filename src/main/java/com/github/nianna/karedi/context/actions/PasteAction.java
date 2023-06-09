package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.CommandComposite;
import com.github.nianna.karedi.command.DeleteNotesCommand;
import com.github.nianna.karedi.command.PasteCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;

class PasteAction extends ClipboardAction {

    PasteAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeTrackIsNull);
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song pastedSong = buildSongFromClipboard();
        List<Note> notesToSelect = new ArrayList<>();
        if (pastedSong.size() > 0) {
            notesToSelect.addAll(pastedSong.get(0).getNotes());
        }
        Command cmd = new CommandComposite(I18N.get("common.paste")) {
            @Override
            protected void buildSubCommands() {
                addSubCommand(new DeleteNotesCommand(getSelectedNotes(), false));
                addSubCommand(new PasteCommand(appContext.getActiveTrack(), pastedSong, playerContext.getMarkerBeat()));
            }
        };
        executeCommand(new ChangePostStateCommandDecorator(cmd, c -> setSelection(notesToSelect)));
    }

}
