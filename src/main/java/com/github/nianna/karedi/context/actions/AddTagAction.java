package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.dialog.EditTagDialog;
import com.github.nianna.karedi.song.tag.Tag;
import javafx.event.ActionEvent;

import java.util.Optional;

class AddTagAction extends TagAction {

    AddTagAction(AppContext appContext) {
        super(appContext);
    }

    @Override
    protected void onAction(ActionEvent event) {
        EditTagDialog dialog = new EditTagDialog(I18N.get("dialog.new_tag.title"));
        Optional<Tag> result = dialog.showAndWait();
        result.ifPresent(tag -> executeCommand(
                new ChangeTagValueCommand(activeSongContext.getSong(), tag.getKey(), tag.getValue())));
    }
}
