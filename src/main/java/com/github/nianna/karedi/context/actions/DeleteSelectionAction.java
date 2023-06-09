package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.DeleteNotesCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.region.IntBounded;
import javafx.event.ActionEvent;

class DeleteSelectionAction extends ContextfulKarediAction {

        private boolean keepLyrics;

        DeleteSelectionAction(AppContext appContext, boolean keepLyrics) {
            super(appContext);
            this.keepLyrics = keepLyrics;
            setDisabledCondition(appContext.selectionIsEmpty);
        }

        @Override
        protected void onAction(ActionEvent event) {
            appContext.player.stop();
            appContext.execute(getCommand());
        }

        Command getCommand() {
            Command cmd = new DeleteNotesCommand(appContext.getSelected(), keepLyrics);
            IntBounded bounds = BoundingBox.boundsFrom(appContext.getVisibleAreaBounds());
            return new ChangePostStateCommandDecorator(cmd, (command) -> {
                appContext.selection.clear();
                if (appContext.getActiveLine() != null && !appContext.getActiveLine().isValid()) {
                    appContext.setActiveLine(null);
                    appContext.visibleArea.setBounds(bounds);
                }
            });
        }

}
