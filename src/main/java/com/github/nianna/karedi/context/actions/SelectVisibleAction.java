package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import java.util.List;

class SelectVisibleAction extends ContextfulKarediAction {

        SelectVisibleAction(AppContext appContext) {
            super(appContext);
            setDisabledCondition(appContext.activeTrackIsNull);
        }

        @Override
        protected void onAction(ActionEvent event) {
            List<Note> notes;
            if (appContext.getActiveLine() != null) {
                notes = appContext.getActiveLine().getNotes();
            } else {
                notes = appContext.getActiveTrack().getNotes(appContext.visibleArea.getLowerXBound(),
                        appContext.visibleArea.getUpperXBound());
            }
            appContext.selection.set(notes);
        }

}
