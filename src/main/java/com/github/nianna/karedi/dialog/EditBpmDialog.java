package main.java.com.github.nianna.karedi.dialog;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import main.java.com.github.nianna.karedi.I18N;

public class EditBpmDialog extends ModifyBpmDialog {

	public EditBpmDialog() {
		setTitle(I18N.get("dialog.edit_bpm.title"));
		setHeaderText(I18N.get("dialog.edit_bpm.header"));

		ButtonType rescaleButtonType = new ButtonType(I18N.get("common.rescale"), ButtonData.OK_DONE);
		ButtonType setButtonType = new ButtonType(I18N.get("common.set"), ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(setButtonType, rescaleButtonType,
				ButtonType.CANCEL);

		setResultConverter(dialogButtonType -> {
			if (dialogButtonType == rescaleButtonType) {
				return new BpmEditResult(true, bpmField.getValue());
			}
			if (dialogButtonType == setButtonType) {
				return new BpmEditResult(false, bpmField.getValue());
			}
			return null;
		});
	}
}
