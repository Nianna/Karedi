package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.CommandComposite;
import com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.dialog.EditFilenamesDialog;
import com.github.nianna.karedi.song.Song;
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

        activeSongContext.getSong().getTagValue(TagKey.VIDEO)
                .ifPresentOrElse(dialog::setVideoFilename, dialog::hideVideo);
        activeSongContext.getSong().getTagValue(TagKey.BACKGROUND)
                .ifPresentOrElse(dialog::setBackgroundFilename, dialog::hideBackground);
        activeSongContext.getSong().getTagValue(TagKey.INSTRUMENTAL)
                .ifPresentOrElse(dialog::setInstrumentalFilename, dialog::hideInstrumental);
        activeSongContext.getSong().getTagValue(TagKey.VOCALS)
                .ifPresentOrElse(dialog::setVocalsFilename, dialog::hideVocals);

        activeSongContext.getSong().formatSpecificationVersion().ifPresent(dialog::setFormatVersion);

        Optional<EditFilenamesDialog.FilenamesEditResult> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent(result -> executeCommand(commandFromResults(result)));
    }

    private Command commandFromResults(EditFilenamesDialog.FilenamesEditResult result) {
        return new CommandComposite(I18N.get("command.rename")) {

            @Override
            protected void buildSubCommands() {
                Song song = activeSongContext.getSong();
                addSubCommand(new ChangeTagValueCommand(song, TagKey.ARTIST,
                        result.getArtist()));
                addSubCommand(new ChangeTagValueCommand(song, TagKey.TITLE, result.getTitle()));
                addSubCommand(new ChangeTagValueCommand(song, TagKey.MP3, result.getAudioFilename()));
                if (song.hasTag(TagKey.AUDIO) || song.formatSpecificationVersion().isPresent()) {
                    addSubCommand(new ChangeTagValueCommand(song, TagKey.AUDIO, result.getAudioFilename()));
                }
                addSubCommand(new ChangeTagValueCommand(song, TagKey.COVER, result.getCoverFilename()));
                result.getBackgroundFilename()
                        .ifPresent(filename -> addSubCommand(
                                new ChangeTagValueCommand(song, TagKey.BACKGROUND, filename))
                        );
                result.getVideoFilename()
                        .ifPresent(filename -> addSubCommand(
                                new ChangeTagValueCommand(song, TagKey.VIDEO, filename))
                        );
                result.getInstrumentalFilename()
                        .ifPresent(filename -> addSubCommand(
                                new ChangeTagValueCommand(song, TagKey.INSTRUMENTAL, filename))
                        );
                result.getVocalsFilename()
                        .ifPresent(filename -> addSubCommand(new ChangeTagValueCommand(song, TagKey.VOCALS, filename)));
            }
        };
    }

}
