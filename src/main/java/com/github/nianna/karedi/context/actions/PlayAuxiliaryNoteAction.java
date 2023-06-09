package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.context.AppContext;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;

abstract class PlayAuxiliaryNoteAction extends ContextfulKarediAction {

    private int oldLowerBound;

    private int oldUpperBound;

    private ChangeListener<? super Player.Status> statusListener;

    PlayAuxiliaryNoteAction(AppContext appContext) {
        super(appContext);
        disableWhenSelectionEmptyOrActiveAudioNull();
        statusListener = (obs, oldStatus, newStatus) -> {
            if (oldStatus == Player.Status.PLAYING && newStatus == Player.Status.READY) {
                visibleAreaContext.setVisibleAreaXBounds(oldLowerBound, oldUpperBound, false);
                obs.removeListener(statusListener);
            }
        };
    }

    @Override
    protected void onAction(ActionEvent event) {
        playerContext.player.stop();

        int auxiliaryNoteStartBeat = getAuxiliaryNoteStartBeat();
        int auxiliaryNoteEndBeat = auxiliaryNoteStartBeat + getAuxiliaryNoteLength();
        adjustVisibleArea(auxiliaryNoteStartBeat, auxiliaryNoteEndBeat);
        playerContext.playGivenNotes(auxiliaryNoteStartBeat, auxiliaryNoteEndBeat, null, Player.Mode.AUDIO_ONLY);
    }

    private void adjustVisibleArea(int auxiliaryNoteStartBeat, int auxiliaryNoteEndBeat) {
        oldLowerBound = visibleAreaContext.getLowerXBound();
        oldUpperBound = visibleAreaContext.getUpperXBound();
        int newLowerBound = Math.min(oldLowerBound, auxiliaryNoteStartBeat);
        int newUpperBound = Math.max(oldUpperBound, auxiliaryNoteEndBeat);
        if (newLowerBound != oldLowerBound || newUpperBound != oldUpperBound) {
            visibleAreaContext.setVisibleAreaXBounds(newLowerBound, newUpperBound, false);
            playerContext.statusProperty().addListener(statusListener);
        }
    }

    protected int getAuxiliaryNoteLength() {
        return (int) (beatRangeContext.getBeatMillisConverter().getBpm() / 100) + 1;
    }

    protected abstract int getAuxiliaryNoteStartBeat();
}

