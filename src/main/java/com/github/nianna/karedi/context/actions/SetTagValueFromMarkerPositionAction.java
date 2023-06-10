package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.util.Converter;
import com.github.nianna.karedi.util.MathUtils;
import javafx.event.ActionEvent;

class SetTagValueFromMarkerPositionAction extends TagAction {

    private TagKey key;

    SetTagValueFromMarkerPositionAction(AppContext appContext, TagKey key) {
        super(appContext);
        this.key = key;
    }

    @Override
    protected void onAction(ActionEvent event) {
        String value = null;
        if (TagKey.expectsADouble(key)) {
            value = Converter.toString(MathUtils.msToSeconds(audioContext.getMarkerTime()));
        } else {
            if (TagKey.expectsAnInteger(key)) {
                value = Converter.toString(audioContext.getMarkerTime());
            }
        }
        if (value != null) {
            executeCommand(new ChangeTagValueCommand(activeSongContext.getSong(), key, value));
        }
    }

}
