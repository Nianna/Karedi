package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class ReloadSongAction extends ContextfulKarediAction {
    private Integer trackNumber;
    private Integer lineNumber;

    ReloadSongAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(txtContext.activeFileIsNullBinding().or(activeSongContext.activeSongIsNullBinding()));
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (KarediApp.getInstance().saveChangesIfUserWantsTo()) {
            backupTrackAndLine();
            executeAction(KarediActions.RESET_SEQUENCER);
            txtContext.loadSongFile(txtContext.getActiveFile(), false);

            if (activeSongContext.getSong() != null) {
                restoreTrackAndLine();
            }
        }
    }

    private void backupTrackAndLine() {
        trackNumber = null;
        lineNumber = null;
        if (activeSongContext.getActiveTrack() != null) {
            trackNumber = activeSongContext.getSong().indexOf(activeSongContext.getActiveTrack());
            if (activeSongContext.getActiveLine() != null) {
                lineNumber = activeSongContext.getActiveTrack().indexOf(activeSongContext.getActiveLine());
            }
        }
    }

    private void restoreTrackAndLine() {
        if (trackNumber != null && activeSongContext.getSong().size() > trackNumber) {
            activeSongContext.setActiveTrack(activeSongContext.getSong().get(trackNumber));
            if (lineNumber != null && activeSongContext.getActiveTrack().size() > lineNumber) {
                activeSongContext.setActiveLine(activeSongContext.getActiveTrack().get(lineNumber));
            }
        }
    }
}
