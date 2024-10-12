package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.Settings;
import com.github.nianna.karedi.control.NonNegativeIntegerTextField;
import com.github.nianna.karedi.song.tag.FormatSpecification;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.song.tag.TagValueValidators;
import com.github.nianna.karedi.util.BindingsUtils;
import com.github.nianna.karedi.util.NumericNodeUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AddSongInfoDialog extends Dialog<List<Tag>> {

	@FXML
	private NonNegativeIntegerTextField gapField;
	@FXML
	private NonNegativeIntegerTextField yearField;
	@FXML
	private TextField languageField;
	@FXML
	private TextField creatorField;
	@FXML
	private TextField genreField;
	@FXML
	private TextField editionField;

	private final FormatSpecification formatSpecification;
	
	private ValidationSupport validationSupport = new ValidationSupport();

	public AddSongInfoDialog(FormatSpecification formatSpecification) {
		this.formatSpecification = formatSpecification;
		setTitle(I18N.get("dialog.creator.add_info.title"));
		setHeaderText(I18N.get("dialog.creator.add_info.header"));

		DialogUtils.loadPane(this,
				getClass().getResource("/fxml/AddSongInfoDialogPaneLayout.fxml"));
		DialogUtils.addDescription(this, I18N.get("dialog.creator.add_info.description"), 10);

		getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
		setResultConverter(dialogButtonType -> {
			if (dialogButtonType == ButtonType.OK) {
				Settings.setNewSongWizardDefaultCreator(creatorField.getText());
				return generateListOfValidTags();
			}
			return null;
		});
	}

	@FXML
	private void initialize() {
		addSuggestions(languageField, TagKey.LANGUAGE);
		addSuggestions(genreField, TagKey.GENRE);

		gapField.setOnScroll(NumericNodeUtils.createUpdateIntValueOnScrollHandler(
				gapField::getValue, gapField::setValueIfLegal));
		yearField.setOnScroll(NumericNodeUtils.createUpdateIntValueOnScrollHandler(
				yearField::getValue, yearField::setValueIfLegal));

		Platform.runLater(() -> {
			registerValidators();
			validationSupport.initInitialDecoration();
		});

		Settings.getNewSongWizardDefaultCreator().ifPresent(creatorField::setText);
	}

	private void addSuggestions(TextField field, TagKey key) {
		BindingsUtils.bindAutoCompletion(
				field,
				key.suggestedValues(),
				FormatSpecification.supportsMultipleValues(formatSpecification, key)
		);
	}

	private void registerValidators() {
		registerValidator(gapField, TagKey.GAP);
		registerValidator(yearField, TagKey.YEAR);
		registerValidator(languageField, TagKey.LANGUAGE);
		registerValidator(creatorField, TagKey.CREATOR);
		registerValidator(genreField, TagKey.GENRE);
		registerValidator(editionField, TagKey.EDITION);
	}

	private void registerValidator(Control control, TagKey key) {
		validationSupport.registerValidator(control, TagValueValidators.forKey(key));
	}

	private List<Tag> generateListOfValidTags() {
		List<Tag> list = new ArrayList<>();
		List<Control> invalidFields = validationSupport.getValidationResult().getErrors().stream()
				.map(ValidationMessage::getTarget)
				.distinct()
				.collect(Collectors.toList());
		addTag(TagKey.GAP, gapField, invalidFields).ifPresent(list::add);
		addTag(TagKey.YEAR, yearField, invalidFields).ifPresent(list::add);
		addTag(TagKey.LANGUAGE, languageField, invalidFields).ifPresent(list::add);
		addTag(TagKey.CREATOR, creatorField, invalidFields).ifPresent(list::add);
		addTag(TagKey.EDITION, editionField, invalidFields).ifPresent(list::add);
		addTag(TagKey.GENRE, genreField, invalidFields).ifPresent(list::add);
		return list;
	}

	private Optional<Tag> addTag(TagKey key, TextField field, List<Control> invalidControls) {
		if (!invalidControls.contains(field)) {
			return Optional.of(new Tag(key, field.getText()));
		}
		return Optional.empty();
	}

}
