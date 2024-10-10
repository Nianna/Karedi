package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.tag.ClearFormatVersionCommand;
import com.github.nianna.karedi.command.tag.ConvertFormatVersionCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.dialog.ConvertFormatDialog;
import com.github.nianna.karedi.song.tag.FormatSpecification;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;

class ConvertFormatVersionAction extends ContextfulKarediAction {

    private final FormatSpecification targetFormatSpecification;

    ConvertFormatVersionAction(AppContext appContext, FormatSpecification targetFormatSpecification) {
        super(appContext);
        this.targetFormatSpecification = targetFormatSpecification;
        setDisabledCondition(true);
        activeSongContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null) {
                setDisabledCondition(true);
            } else {
                setDisabledCondition(
                        Bindings.createObjectBinding(newVal::getFormatSpecificationVersion, newVal.getTags())
                                .isEqualTo(targetFormatSpecification)
                );
            }
        });
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (activeSongContext.getSong().getFormatSpecificationVersion() == targetFormatSpecification) {
            return;
        }

        if (targetFormatSpecification == null) {
            executeCommand(new ClearFormatVersionCommand(activeSongContext.getSong()));
            return;
        }

        ConvertFormatVersionCommand cmd = new ConvertFormatVersionCommand(
                activeSongContext.getSong(),
                targetFormatSpecification
        );
        showConfirmationPopup(cmd)
                .filter(ButtonType.OK::equals)
                .ifPresent(ignored -> executeCommand(cmd));
    }

    private Optional<ButtonType> showConfirmationPopup(ConvertFormatVersionCommand cmd) {
        List<ConvertFormatDialog.TagRepresentation> allTags = activeSongContext.getSong().getTags().stream()
                .map(tag -> new ConvertFormatDialog.TagRepresentation(tag.getKey(), tag.getValue()))
                .toList();
        List<ConvertFormatDialog.TagRepresentation> tagsToBeRemoved = cmd.getTagsToBeRemoved().stream()
                .map(tag -> new ConvertFormatDialog.TagRepresentation(tag.getKey(), tag.getValue()))
                .toList();
        List<ConvertFormatDialog.TagRepresentation> tagsToBeAdded = cmd.getTagsToBeAdded().entrySet().stream()
                .map(entry -> new ConvertFormatDialog.TagRepresentation(entry.getKey().toString(), entry.getValue()))
                .toList();
        List<ConvertFormatDialog.TagRepresentation> tagsToBeUpdated = cmd.getTagsToBeUpdated().entrySet().stream()
                .map(entry -> new ConvertFormatDialog.TagRepresentation(entry.getKey().getKey(), entry.getValue()))
                .toList();
        return new ConvertFormatDialog(allTags, tagsToBeRemoved, tagsToBeAdded, tagsToBeUpdated).showAndWait();
    }

}
