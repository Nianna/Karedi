package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.tag.ChangeMedleyCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.dialog.EditMedleyDialog;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.TagKey;
import javafx.event.ActionEvent;
import javafx.stage.Modality;

class EditMedleyAction extends ContextfulKarediAction {

    EditMedleyAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeSongIsNull);
    }

    @Override
    protected void onAction(ActionEvent event) {
        EditMedleyDialog dialog = new EditMedleyDialog();

        appContext.getSong().getTagValue(TagKey.MEDLEYSTARTBEAT)
                .ifPresent(dialog::setStartBeat);
        appContext.getSong().getTagValue(TagKey.MEDLEYENDBEAT)
                .ifPresent(dialog::setEndBeat);
        dialog.initModality(Modality.NONE);
        dialog.show();

        dialog.resultProperty().addListener(obs -> {
            Song.Medley medley = dialog.getResult();
            if (medley != null) {
                executeCommand(new ChangeMedleyCommand(appContext.getSong(), medley.getStartBeat(),
                        medley.getEndBeat()));
            }
        });
    }
}
