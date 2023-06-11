package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.tag.ChangeBpmCommand;
import com.github.nianna.karedi.command.tag.RescaleSongToBpmCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.dialog.EditBpmDialog;
import com.github.nianna.karedi.dialog.ModifyBpmDialog;
import com.github.nianna.karedi.song.tag.TagKey;
import javafx.event.ActionEvent;

import java.util.Optional;

class EditBpmAction extends ContextfulKarediAction {

    private double scale;

    private boolean promptUser;

    EditBpmAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveSongIsNull();
        promptUser = true;
    }

    EditBpmAction(AppContext appContext, double scale) {
        this(appContext);
        this.scale = scale;
        promptUser = false;
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (promptUser) {
            double oldBpm = activeSongContext.getSong().getBpm();

            ModifyBpmDialog dialog = new EditBpmDialog();
            activeSongContext.getSong().getTagValue(TagKey.BPM).ifPresent(dialog::setBpmFieldText);
            Optional<ModifyBpmDialog.BpmEditResult> optionalResult = dialog.showAndWait();
            optionalResult.ifPresent(result -> {
                double newBpm = result.getBpm();
                if (result.shouldRescale()) {
                    executeCommand(new RescaleSongToBpmCommand(activeSongContext.getSong(), newBpm / oldBpm));
                } else {
                    executeCommand(new ChangeBpmCommand(activeSongContext.getSong(), newBpm));
                }
            });
        } else {
            executeCommand(new RescaleSongToBpmCommand(activeSongContext.getSong(), scale));
        }
    }

}
