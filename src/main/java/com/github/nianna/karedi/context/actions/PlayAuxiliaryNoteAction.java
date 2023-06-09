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
                appContext.setVisibleAreaXBounds(oldLowerBound, oldUpperBound, false);
                obs.removeListener(statusListener);
            }
        };
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.player.stop();

        int auxiliaryNoteStartBeat = getAuxiliaryNoteStartBeat();
        int auxiliaryNoteEndBeat = auxiliaryNoteStartBeat + getAuxiliaryNoteLength();
        adjustVisibleArea(auxiliaryNoteStartBeat, auxiliaryNoteEndBeat);
        appContext.player.play(
                appContext.beatMillisConverter.beatToMillis(auxiliaryNoteStartBeat),
                appContext.beatMillisConverter.beatToMillis(auxiliaryNoteEndBeat), null, Player.Mode.AUDIO_ONLY);
    }

    private void adjustVisibleArea(int auxiliaryNoteStartBeat, int auxiliaryNoteEndBeat) {
        oldLowerBound = appContext.visibleArea.getLowerXBound();
        oldUpperBound = appContext.visibleArea.getUpperXBound();
        int newLowerBound = Math.min(oldLowerBound, auxiliaryNoteStartBeat);
        int newUpperBound = Math.max(oldUpperBound, auxiliaryNoteEndBeat);
        if (newLowerBound != oldLowerBound || newUpperBound != oldUpperBound) {
            appContext.setVisibleAreaXBounds(newLowerBound, newUpperBound, false);
            appContext.player.statusProperty().addListener(statusListener);
        }
    }

    protected int getAuxiliaryNoteLength() {
        return (int) (appContext.beatMillisConverter.getBpm() / 100) + 1;
    }

    protected abstract int getAuxiliaryNoteStartBeat();
}

