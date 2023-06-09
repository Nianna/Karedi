package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;

class PlayAuxiliaryNoteBeforeSelectionAction extends PlayAuxiliaryNoteAction {

    PlayAuxiliaryNoteBeforeSelectionAction(AppContext appContext) {
        super(appContext);
    }

    @Override
    protected int getAuxiliaryNoteStartBeat() {
        return appContext.selection.getFirst().get().getStart() + 1 - getAuxiliaryNoteLength();
    }

}
