package com.github.nianna.karedi.dialog;

import org.controlsfx.validation.ValidationSupport;

import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public abstract class ValidatedDialog<T> extends Dialog<T> {
	protected ValidationSupport validationSupport;

	public ValidatedDialog() {
		this(new ValidationSupport());
	}

	public ValidatedDialog(ValidationSupport validationSupport) {
		this.validationSupport = validationSupport;

		FilteredList<ButtonType> confirmTypes = getDialogPane().getButtonTypes()
				.filtered(type -> type.getButtonData().equals(ButtonData.OK_DONE));

		validationSupport.validationResultProperty().addListener((o, oldValue, newValue) -> {
			if (newValue.getErrors().size() > 0) {
				setDisable(confirmTypes, true);
			} else {
				setDisable(confirmTypes, false);
			}
		});
	}

	private void setDisable(FilteredList<ButtonType> confirmTypes, boolean disable) {
		confirmTypes.forEach(type -> getDialogPane().lookupButton(type).setDisable(disable));
	}

}
