package com.github.nianna.karedi.dialog;

import java.util.Optional;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import com.github.nianna.karedi.I18N;

public abstract class SkippableDialog<T> extends Dialog<Optional<T>> {
	protected final ButtonType SKIP_TYPE = new ButtonType(I18N.get("common.skip"), ButtonData.NEXT_FORWARD);

	public SkippableDialog() {
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, SKIP_TYPE, ButtonType.CANCEL);

		setResultConverter(dialogButtonType -> {
			if (dialogButtonType == ButtonType.OK) {
				return Optional.of(getData());
			}
			if (dialogButtonType == SKIP_TYPE) {
				return Optional.empty();
			}
			return null;
		});
	}

	protected abstract T getData();

	protected void setOkButtonDisable(boolean disable) {
		getDialogPane().getButtonTypes()
				.filtered(type -> type.getButtonData().equals(ButtonData.OK_DONE))
				.forEach(type -> getDialogPane().lookupButton(type).setDisable(disable));
	}

}
