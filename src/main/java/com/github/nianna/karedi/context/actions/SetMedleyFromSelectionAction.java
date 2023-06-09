package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.tag.ChangeMedleyCommand;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class SetMedleyFromSelectionAction extends ContextfulKarediAction {

    private final boolean setStartBeat;

    private final boolean setEndBeat;

    SetMedleyFromSelectionAction(AppContext appContext, boolean setStartBeat, boolean setEndBeat) {
        super(appContext);
        disableWhenSelectionEmpty();
        this.setEndBeat = setEndBeat;
        this.setStartBeat = setStartBeat;
    }

    @Override
    protected void onAction(ActionEvent event) {
        Integer startBeat = setStartBeat ? selectionContext.getSelectionBounds().getLowerXBound() : null;
        Integer endBeat = setEndBeat ? selectionContext.getSelectionBounds().getUpperXBound() : null;
        executeCommand(new ChangeMedleyCommand(appContext.getSong(), startBeat, endBeat));
    }

}
