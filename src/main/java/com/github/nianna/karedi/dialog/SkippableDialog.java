package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public abstract class SkippableDialog<T> extends StyleableDialog<Optional<T>> {
	protected static final ButtonType SKIP_TYPE = new ButtonType(I18N.get("common.skip"), ButtonData.NEXT_FORWARD);

	public SkippableDialog() {
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, SKIP_TYPE, ButtonType.CANCEL);

		setResultConverter(dialogButtonType -> {
			if (dialogButtonType == ButtonType.OK) {
				onOkClicked();
				return Optional.of(getData());
			}
			if (dialogButtonType == SKIP_TYPE) {
				return Optional.empty();
			}
			return null;
		});
	}

	protected void onOkClicked() {

	}

	protected abstract T getData();

	protected void setOkButtonDisable(boolean disable) {
		getDialogPane().getButtonTypes()
				.filtered(type -> type.getButtonData().equals(ButtonData.OK_DONE))
				.forEach(type -> getDialogPane().lookupButton(type).setDisable(disable));
	}

}
