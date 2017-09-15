package main.java.com.github.nianna.karedi.dialog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.control.NonNegativeIntegerTextField;
import main.java.com.github.nianna.karedi.song.Song.Medley;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.song.tag.TagValidators;
import main.java.com.github.nianna.karedi.util.NumericNodeUtils;

public class EditMedleyDialog extends ValidatedDialog<Medley> {

	@FXML
	private NonNegativeIntegerTextField startBeatField;
	@FXML
	private NonNegativeIntegerTextField endBeatField;

	public EditMedleyDialog() {
		setTitle(I18N.get("dialog.edit_medley.title"));
		setHeaderText(I18N.get("dialog.edit_medley.header"));

		DialogUtils.loadPane(this, getClass().getResource("/fxml/EditMedleyDialogPaneLayout.fxml"));

		ButtonType setButtonType = new ButtonType("Set", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(setButtonType, ButtonType.CANCEL);

		setResultConverter(dialogButtonType -> {
			if (dialogButtonType == setButtonType) {
				return new Medley(startBeatField.getValue().get(), endBeatField.getValue().get());
			}
			return null;
		});
	}

	@FXML
	private void initialize() {
		Platform.runLater(() -> {
			configureTagTextField(startBeatField, TagKey.MEDLEYSTARTBEAT);
			configureTagTextField(endBeatField, TagKey.MEDLEYENDBEAT);
		});
	}

	private void configureTagTextField(NonNegativeIntegerTextField textField, TagKey tagKey) {
		validationSupport.registerValidator(textField, TagValidators.forKey(tagKey));
		textField.setOnScroll(NumericNodeUtils.createUpdateIntValueOnScrollHandler(
				textField::getValue, textField::setValueIfLegal));
	}

	public void setEndBeat(String value) {
		endBeatField.setText(value);
	}

	public void setStartBeat(String value) {
		startBeatField.setText(value);
	}
}
