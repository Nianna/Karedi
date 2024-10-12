package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.control.RestrictedTextField;
import com.github.nianna.karedi.song.tag.FormatSpecification;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.song.tag.TagValueValidators;
import com.github.nianna.karedi.util.BindingsUtils;
import com.github.nianna.karedi.util.ValidationUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import org.controlsfx.validation.decoration.ValidationDecoration;

import java.util.Arrays;
import java.util.List;

public class EditTagDialog extends Dialog<Tag> {
	@FXML
	private TextField keyField;
	@FXML
	private RestrictedTextField valueField;

	private ValidationDecoration valueDecoration = new GraphicValidationDecoration();
	private ValidationDecoration keyDecoration = new GraphicValidationDecoration();
	private Validator<String> valueValidator = TagValueValidators.defaultValidator();
	private Validator<String> keyValidator = Validator
			.createEmptyValidator(I18N.get("dialog.tag.key_required"));

	private Node okButton;

	private final FormatSpecification formatSpecification;

	private AutoCompletionBinding<?> valueSuggestions;

	public EditTagDialog(FormatSpecification formatSpecification) {
		this.formatSpecification = formatSpecification;

		DialogUtils.loadPane(this, getClass().getResource("/fxml/EditTagDialogPaneLayout.fxml"));
		setTitle(I18N.get("dialog.new_tag.title"));

		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		okButton = getDialogPane().lookupButton(ButtonType.OK);

		setResultConverter(type -> {
			if (type == ButtonType.OK) {
				return new Tag(keyField.getText(), valueField.getText());
			}
			return null;
		});

	}

	@FXML
	public void initialize() {
		List<TagKey> suggestedKeys = Arrays.stream(TagKey.values())
				.filter(key -> FormatSpecification.supports(formatSpecification, key))
				.toList();
		BindingsUtils.bindAutoCompletion(keyField, suggestedKeys, false);
		keyField.textProperty().addListener(obs -> onKeyFieldTextChanged());
		valueField.textProperty().addListener(obs -> refreshValueFieldDecoration());

		Platform.runLater(() -> {
			refreshValueFieldDecoration();
			refreshKeyFieldDecoration();
		});
	}

	private void onKeyFieldTextChanged() {
		TagValueValidators.forbiddenCharacterRegex(keyField.getText()).ifPresent(regex -> {
			valueField.setForbiddenCharacterRegex(regex);
		});
		valueValidator = TagValueValidators.forKey(keyField.getText());
		refreshValueFieldDecoration();
		refreshKeyFieldDecoration();
		refreshValueFieldAutoCompletion();
	}

	private void refreshValueFieldDecoration() {
		refreshDecoration(valueValidator, valueDecoration, valueField);
	}

	private void refreshKeyFieldDecoration() {
		refreshDecoration(keyValidator, keyDecoration, keyField);
	}

	private void refreshDecoration(Validator<String> validator, ValidationDecoration decoration,
			TextField field) {
		ValidationResult result = validator.apply(field, field.getText());
		decoration.removeDecorations(field);
		ValidationUtils.getHighestPriorityMessage(result)
				.ifPresent(decoration::applyValidationDecoration);

		if (!result.getErrors().isEmpty()) {
			okButton.setDisable(true);
		} else {
			refreshOkButtonDisable();
		}
	}

	private void refreshValueFieldAutoCompletion() {
		if (valueSuggestions != null) {
			valueSuggestions.dispose();
		}
		valueSuggestions = TagKey.optionalValueOf(keyField.getText())
				.map(key -> BindingsUtils.bindAutoCompletion(
						valueField,
						key.suggestedValues(),
						FormatSpecification.supportsMultipleValues(formatSpecification, key))
				)
				.orElse(null);
	}

	private void refreshOkButtonDisable() {
		okButton.setDisable(errorsPresent());
	}

	private boolean errorsPresent() {
		return countErrors(keyValidator, keyField) + countErrors(valueValidator, valueField) > 0;
	}

	private int countErrors(Validator<String> validator, TextField field) {
		return validator.apply(field, field.getText()).getErrors().size();
	}
}
