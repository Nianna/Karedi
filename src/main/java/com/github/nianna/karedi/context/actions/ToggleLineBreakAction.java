package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.ToggleLineBreakCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

class ToggleLineBreakAction extends ContextfulKarediAction {

    ToggleLineBreakAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.selectionIsEmpty);
    }

    @Override
    protected void onAction(ActionEvent event) {
        Note splittingNote = appContext.selection.getFirst().get();
        Command cmd = new ToggleLineBreakCommand(splittingNote);
        appContext.execute(new ChangePostStateCommandDecorator(cmd, (command) -> {
            appContext.setActiveLine(splittingNote.getLine());
            appContext.selection.selectOnly(splittingNote);
        }));
    }
}
