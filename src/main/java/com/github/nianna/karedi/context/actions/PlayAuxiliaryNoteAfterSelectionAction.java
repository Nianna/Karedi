package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;

class PlayAuxiliaryNoteAfterSelectionAction extends PlayAuxiliaryNoteAction {

    PlayAuxiliaryNoteAfterSelectionAction(AppContext appContext) {
        super(appContext);
    }

    @Override
    protected int getAuxiliaryNoteStartBeat() {
        return appContext.selection.getLast().get().getEnd() - 1;
    }

}
