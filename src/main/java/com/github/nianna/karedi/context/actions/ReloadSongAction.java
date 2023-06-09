package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class ReloadSongAction extends ContextfulKarediAction {
    private Integer trackNumber;
    private Integer lineNumber;

    ReloadSongAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeFileIsNull.or(appContext.activeSongIsNull));
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (KarediApp.getInstance().saveChangesIfUserWantsTo()) {
            backupTrackAndLine();
            appContext.loadSongFile(appContext.getActiveFile(), false);

            if (appContext.getSong() != null) {
                restoreTrackAndLine();
            }
        }
    }

    private void backupTrackAndLine() {
        trackNumber = null;
        lineNumber = null;
        if (appContext.getActiveTrack() != null) {
            trackNumber = appContext.getSong().indexOf(appContext.getActiveTrack());
            if (appContext.getActiveLine() != null) {
                lineNumber = appContext.getActiveTrack().indexOf(appContext.getActiveLine());
            }
        }
    }

    private void restoreTrackAndLine() {
        if (trackNumber != null && appContext.getSong().size() > trackNumber) {
            appContext.setActiveTrack(appContext.getSong().get(trackNumber));
            if (lineNumber != null && appContext.getActiveTrack().size() > lineNumber) {
                appContext.setActiveLine(appContext.getActiveTrack().get(lineNumber));
            }
        }
    }
}
