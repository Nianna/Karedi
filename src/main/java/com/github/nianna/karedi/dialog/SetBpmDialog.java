package main.java.com.github.nianna.karedi.dialog;

import javafx.scene.control.ButtonType;
import main.java.com.github.nianna.karedi.I18N;

public class SetBpmDialog extends ModifyBpmDialog {

	public SetBpmDialog() {
		setTitle(I18N.get("dialog.creator.set_bpm.title"));
		setHeaderText(I18N.get("dialog.creator.set_bpm.header"));
		DialogUtils.addDescription(this, I18N.get("dialog.creator.set_bpm.description"), 10);
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		setResultConverter(dialogButtonType -> {
			if (dialogButtonType == ButtonType.OK) {
				return new BpmEditResult(false, bpmField.getValue());
			}
			return null;
		});
	}
}
