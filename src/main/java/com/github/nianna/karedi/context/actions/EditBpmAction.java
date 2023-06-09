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
        setDisabledCondition(appContext.activeSongIsNull);
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
            double oldBpm = appContext.getSong().getBpm();

            ModifyBpmDialog dialog = new EditBpmDialog();
            appContext.getSong().getTagValue(TagKey.BPM).ifPresent(dialog::setBpmFieldText);
            Optional<ModifyBpmDialog.BpmEditResult> optionalResult = dialog.showAndWait();
            optionalResult.ifPresent(result -> {
                double newBpm = result.getBpm();
                if (result.shouldRescale()) {
                    appContext.execute(new RescaleSongToBpmCommand(appContext.getSong(), newBpm / oldBpm));
                } else {
                    appContext.execute(new ChangeBpmCommand(appContext.getSong(), newBpm));
                }
            });
        } else {
            appContext.execute(new RescaleSongToBpmCommand(appContext.getSong(), scale));
        }
    }

}
