package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.CommandComposite;
import com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.dialog.EditFilenamesDialog;
import com.github.nianna.karedi.song.tag.TagKey;
import javafx.event.ActionEvent;

import java.util.Optional;

class RenameAction extends ContextfulKarediAction {

    RenameAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveSongIsNull();
    }

    @Override
    protected void onAction(ActionEvent event) {
        EditFilenamesDialog dialog = new EditFilenamesDialog();
        dialog.hideFormatSpecificationChoiceBox();

        activeSongContext.getSong().getTagValue(TagKey.ARTIST).ifPresent(dialog::setSongArtist);
        activeSongContext.getSong().getTagValue(TagKey.TITLE).ifPresent(dialog::setSongTitle);
        activeSongContext.getSong().getMainAudioTagValue().ifPresent(dialog::setAudioFilename);
        activeSongContext.getSong().getTagValue(TagKey.COVER).ifPresent(dialog::setCoverFilename);

        Optional<String> optVideoFilename = activeSongContext.getSong().getTagValue(TagKey.VIDEO);
        if (optVideoFilename.isPresent()) {
            dialog.setVideoFilename(optVideoFilename.get());
        } else {
            dialog.hideVideo();
        }

        Optional<String> optBackgroundFilename = activeSongContext.getSong().getTagValue(TagKey.BACKGROUND);
        if (optBackgroundFilename.isPresent()) {
            dialog.setBackgroundFilename(optBackgroundFilename.get());
        } else {
            dialog.hideBackground();
        }

        Optional<EditFilenamesDialog.FilenamesEditResult> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent(result -> executeCommand(commandFromResults(result)));
    }

    private Command commandFromResults(EditFilenamesDialog.FilenamesEditResult result) {
        return new CommandComposite(I18N.get("command.rename")) {

            @Override
            protected void buildSubCommands() {
                addSubCommand(new ChangeTagValueCommand(activeSongContext.getSong(), TagKey.ARTIST,
                        result.getArtist()));
                addSubCommand(new ChangeTagValueCommand(activeSongContext.getSong(), TagKey.TITLE, result.getTitle()));
                addSubCommand(new ChangeTagValueCommand(activeSongContext.getSong(), TagKey.MP3, result.getAudioFilename()));
                addSubCommand(new ChangeTagValueCommand(activeSongContext.getSong(), TagKey.COVER, result.getCoverFilename()));
                result.getBackgroundFilename()
                        .ifPresent(filename -> addSubCommand(
                                new ChangeTagValueCommand(activeSongContext.getSong(), TagKey.BACKGROUND, filename))
                        );
                result.getVideoFilename()
                        .ifPresent(filename -> addSubCommand(
                                new ChangeTagValueCommand(activeSongContext.getSong(), TagKey.VIDEO, filename))
                        );
            }
        };
    }

}
