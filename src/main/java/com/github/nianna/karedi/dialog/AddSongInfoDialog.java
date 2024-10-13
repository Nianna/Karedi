package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.Settings;
import com.github.nianna.karedi.control.ManageableGridPane;
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
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddSongInfoDialog extends ValidatedDialog<List<Tag>> {

	@FXML
	private ManageableGridPane gridPane;
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
	private TextField tagsField;

	private final FormatSpecification formatSpecification;
	
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
		addSuggestions(tagsField, TagKey.TAGS);

		gapField.setOnScroll(NumericNodeUtils.createUpdateIntValueOnScrollHandler(
				gapField::getValue, gapField::setValueIfLegal));
		yearField.setOnScroll(NumericNodeUtils.createUpdateIntValueOnScrollHandler(
				yearField::getValue, yearField::setValueIfLegal));

		if (!FormatSpecification.supports(formatSpecification, TagKey.TAGS)) {
			gridPane.hideRowWith(tagsField);
		}

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
		registerValidator(tagsField, TagKey.TAGS);
	}

	private void registerValidator(Control control, TagKey key) {
		Validator<String> tagValidator = TagValueValidators.forKey(key);
		Validator<String> allowEmptyInputWrapper = (c, value) ->
				value.isEmpty() ? new ValidationResult() : tagValidator.apply(c, value);
		validationSupport.registerValidator(control, allowEmptyInputWrapper);
	}

	private List<Tag> generateListOfValidTags() {
		List<Tag> list = new ArrayList<>();
		createTagIfDefined(TagKey.GAP, gapField).ifPresent(list::add);
		createTagIfDefined(TagKey.YEAR, yearField).ifPresent(list::add);
		createTagIfDefined(TagKey.LANGUAGE, languageField).ifPresent(list::add);
		createTagIfDefined(TagKey.CREATOR, creatorField).ifPresent(list::add);
		createTagIfDefined(TagKey.GENRE, genreField).ifPresent(list::add);
		createTagIfDefined(TagKey.TAGS, tagsField).ifPresent(list::add);
		return list;
	}

	private Optional<Tag> createTagIfDefined(TagKey key, TextField field) {
		return Optional.of(field.getText())
				.filter(value -> !value.isEmpty())
				.map(value -> new Tag(key, value));
	}

}
