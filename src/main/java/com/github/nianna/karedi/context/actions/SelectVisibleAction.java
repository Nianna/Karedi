package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import java.util.List;

class SelectVisibleAction extends ContextfulKarediAction {

        SelectVisibleAction(AppContext appContext) {
            super(appContext);
            disableWhenActiveTrackIsNull();
        }

        @Override
        protected void onAction(ActionEvent event) {
            List<Note> notes;
            if (activeSongContext.getActiveLine() != null) {
                notes = activeSongContext.getActiveLine().getNotes();
            } else {
                notes = activeSongContext.getActiveTrack()
                        .getNotes(visibleAreaContext.getLowerXBound(), visibleAreaContext.getUpperXBound());
            }
            setSelection(notes);
        }

}
