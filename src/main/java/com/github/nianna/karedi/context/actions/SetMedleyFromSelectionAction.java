package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.tag.ChangeMedleyCommand;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class SetMedleyFromSelectionAction extends ContextfulKarediAction {

    private boolean setStartBeat;

    private boolean setEndBeat;

    SetMedleyFromSelectionAction(AppContext appContext, boolean setStartBeat, boolean setEndBeat) {
        super(appContext);
        setDisabledCondition(appContext.selectionIsEmpty);
        this.setEndBeat = setEndBeat;
        this.setStartBeat = setStartBeat;
    }

    @Override
    protected void onAction(ActionEvent event) {
        Integer startBeat = setStartBeat ? appContext.selectionBounds.getLowerXBound() : null;
        Integer endBeat = setEndBeat ? appContext.selectionBounds.getUpperXBound() : null;
        appContext.execute(new ChangeMedleyCommand(appContext.getSong(), startBeat, endBeat));
    }

}
