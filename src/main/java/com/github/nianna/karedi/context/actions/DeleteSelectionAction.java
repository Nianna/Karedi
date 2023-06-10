package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.DeleteNotesCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.region.IntBounded;
import javafx.event.ActionEvent;

class DeleteSelectionAction extends ContextfulKarediAction {

        private final boolean keepLyrics;

        DeleteSelectionAction(AppContext appContext, boolean keepLyrics) {
            super(appContext);
            this.keepLyrics = keepLyrics;
            disableWhenSelectionEmpty();
        }

        @Override
        protected void onAction(ActionEvent event) {
            audioContext.stop();
            executeCommand(getCommand());
        }

        Command getCommand() {
            Command cmd = new DeleteNotesCommand(getSelectedNotes(), keepLyrics);
            IntBounded bounds = BoundingBox.boundsFrom(visibleAreaContext.getVisibleAreaBounds());
            return new ChangePostStateCommandDecorator(cmd, (command) -> {
                clearSelection();
                if (activeSongContext.getActiveLine() != null && !activeSongContext.getActiveLine().isValid()) {
                    activeSongContext.setActiveLine(null);
                    visibleAreaContext.setBounds(bounds);
                }
            });
        }

}
