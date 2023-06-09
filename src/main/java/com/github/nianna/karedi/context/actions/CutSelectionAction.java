package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.command.ChangePreStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class CutSelectionAction extends ContextfulKarediAction {

    CutSelectionAction(AppContext appContext) {
        super(appContext);
        disableWhenSelectionEmpty();
    }

    @Override
    protected void onAction(ActionEvent event) {
        audioContext.stop();
        Command cmd = new ChangePreStateCommandDecorator(
                new DeleteSelectionAction(appContext, false).getCommand(),
                c -> executeAction(KarediActions.COPY)
        );
        cmd.setTitle(I18N.get("common.cut"));
        executeCommand(cmd);
    }
}
