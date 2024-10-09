package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.context.AudioContext;
import com.github.nianna.karedi.control.ManageableGridPane;
import com.github.nianna.karedi.control.RestrictedTextField;
import com.github.nianna.karedi.dialog.EditFilenamesDialog.FilenamesEditResult;
import com.github.nianna.karedi.song.tag.FormatSpecification;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.song.tag.TagValueValidators;
import com.github.nianna.karedi.util.Utils;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.controlsfx.glyphfont.Glyph;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;

public class EditFilenamesDialog extends ValidatedDialog<FilenamesEditResult> {
	private static final String ARTIST_TITLE_SEPARATOR = " - ";
	private static final String BACKGROUND_SUFFIX = " [BG]";
	private static final String COVER_SUFFIX = " [CO]";

	private static final String DEFAULT_IMAGE_EXTENSION = "jpg";
	private static final String DEFAULT_AUDIO_EXTENSION = "mp3";
	private static final String DEFAULT_VIDEO_EXTENSION = "mp4";

	@FXML
	private ManageableGridPane artistTitleGridPane;
	@FXML
	private ManageableGridPane filenamesGridPane;

	@FXML
	private TextField artistField;
	@FXML
	private TextField titleField;

	@FXML
	private Glyph filenameLinkGlyph;
	@FXML
	private RestrictedTextField filenameField;

	@FXML
	private Glyph audioLinkGlyph;
	@FXML
	private TextField audioField;
	@FXML
	private ComboBox<String> audioExtensionField;

	@FXML
	private CheckBox addCoCheckBox;
	@FXML
	private Glyph coverLinkGlyph;
	@FXML
	private TextField coverField;
	@FXML
	private TextField coverExtensionField;

	@FXML
	private CheckBox includeVideoCheckBox;
	@FXML
	private Glyph videoLinkGlyph;
	@FXML
	private TextField videoField;
	@FXML
	private TextField videoExtensionField;

	@FXML
	private CheckBox includeBackgroundCheckBox;
	@FXML
	private Glyph backgroundLinkGlyph;
	@FXML
	private TextField backgroundField;
	@FXML
	private TextField backgroundExtensionField;
	@FXML
	private ChoiceBox<String> formatSpecificationChoiceBox;

	private boolean hideVideo = false;
	private boolean hideBackground = false;

	public EditFilenamesDialog() {
		setTitle(I18N.get("dialog.edit_filenames.dialog-title"));
		DialogUtils.loadPane(this,
				getClass().getResource("/fxml/EditFilenamesDialogPaneLayout.fxml"));

		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		setResultConverter(dialogButtonType -> {
			if (dialogButtonType == ButtonType.OK) {
				return new FilenamesEditResult();
			}
			return null;
		});
	}

	@FXML
	public void initialize() {
		setAndExecuteOnMouseClicked(filenameLinkGlyph, event -> toggleFilenameBinding());
		setAndExecuteOnMouseClicked(audioLinkGlyph,
				event -> toggleBinding(audioLinkGlyph, audioField, null));
		setAndExecuteOnMouseClicked(videoLinkGlyph,
				event -> toggleBinding(videoLinkGlyph, videoField, null));
		setAndExecuteOnMouseClicked(coverLinkGlyph, event -> {
			toggleBinding(coverLinkGlyph, coverField,
					addCoCheckBox.isSelected() ? COVER_SUFFIX : null);
		});
		setAndExecuteOnMouseClicked(backgroundLinkGlyph,
				event -> toggleBinding(backgroundLinkGlyph, backgroundField, BACKGROUND_SUFFIX));

		addAndExecuteInvalidationListener(includeVideoCheckBox.selectedProperty(),
				this::onIncludeVideoInvalidated);
		addAndExecuteInvalidationListener(includeBackgroundCheckBox.selectedProperty(),
				this::onIncludeBackgroundInvalidated);
		addAndExecuteInvalidationListener(addCoCheckBox.selectedProperty(),
				this::onAddCoCheckBoxInvalidated);

		videoExtensionField.setText(DEFAULT_VIDEO_EXTENSION);
		audioExtensionField.getItems().addAll(AudioContext.supportedAudioExtensions().stream().sorted().toList());
		audioExtensionField.getSelectionModel().select(DEFAULT_AUDIO_EXTENSION);
		coverExtensionField.setText(DEFAULT_IMAGE_EXTENSION);
		backgroundExtensionField.setText(DEFAULT_IMAGE_EXTENSION);

		formatSpecificationChoiceBox.setItems(supportedFormatSpecificationVersions());
		formatSpecificationChoiceBox.getSelectionModel().selectFirst();

		Platform.runLater(() -> {
			validationSupport.registerValidator(titleField, TagValueValidators.forKey(TagKey.TITLE));
			validationSupport.registerValidator(artistField, TagValueValidators.forKey(TagKey.ARTIST));
			validationSupport.registerValidator(coverExtensionField, TagValueValidators.defaultValidator());
			validationSupport.initInitialDecoration();
			includeBackgroundCheckBox.setSelected(!hideBackground);
			includeVideoCheckBox.setSelected(!hideVideo);
		});
	}

	public void hideFormatSpecificationChoiceBox() {
		int formatChoiceBoxRowIndex = artistTitleGridPane.getChildRowIndex(formatSpecificationChoiceBox);
		artistTitleGridPane.changeRowVisibility(formatChoiceBoxRowIndex, false);
		artistTitleGridPane.changeRowVisibility(formatChoiceBoxRowIndex - 1, false);
	}

	private static ObservableList<String> supportedFormatSpecificationVersions() {
		ObservableList<String> formatSpecifications = FXCollections
				.observableArrayList("None");
		Arrays.stream(FormatSpecification.values())
				.map(Enum::toString)
				.sorted(Comparator.reverseOrder())
				.forEach(formatSpecifications::add);
		return formatSpecifications;
	}

	public void initDataFromAudioFilename(String audioFileName) {
		audioExtensionField.getSelectionModel().select(Utils.getFileExtension(audioFileName));
		audioExtensionField.getStyleClass().setAll("no-arrow-combobox");
		audioExtensionField.setDisable(true);
		String audioNameWithoutExtension = Utils.trimExtension(audioFileName);
		Pair<String, String> artistTitlePair = ArtistTitleGuesser.guessFromFileName(audioNameWithoutExtension);
		artistField.setText(artistTitlePair.getKey());
		titleField.setText(artistTitlePair.getValue());
	}

	private void setAndExecuteOnMouseClicked(Node node, EventHandler<? super MouseEvent> handler) {
		node.setOnMouseClicked(handler);
		handler.handle(null);
	}

	private void addAndExecuteInvalidationListener(Observable obs, InvalidationListener listener) {
		obs.addListener(listener);
		listener.invalidated(obs);
	}

	private void toggleFilenameBinding() {
		boolean bind = !filenameField.textProperty().isBound();
		if (bind) {
			filenameField.textProperty().bind(Bindings.createStringBinding(() -> {
				return filenameField.filter(artistField.getText().concat(ARTIST_TITLE_SEPARATOR)
						.concat(titleField.getText()));
			}, artistField.textProperty(), titleField.textProperty()));
		} else {
			filenameField.textProperty().unbind();
		}
		filenameField.setDisable(bind);
		setLinkActive(filenameLinkGlyph, bind);
	}

	private void bindToFilename(TextField field, String suffix) {
		field.setDisable(true);
		if (suffix == null) {
			field.textProperty().bind(filenameField.textProperty());
		} else {
			field.textProperty().bind(filenameField.textProperty().concat(suffix));
		}
	}

	private boolean isBound(TextField field) {
		return field.textProperty().isBound();
	}

	private void bind(Glyph glyph, TextField field, String suffix) {
		bindToFilename(field, suffix);
		setLinkActive(glyph, true);
	}

	private void unbind(TextField field) {
		field.setDisable(false);
		field.textProperty().unbind();
	}

	private void unbind(Glyph glyph, TextField field) {
		unbind(field);
		setLinkActive(glyph, false);
	}

	private void toggleBinding(Glyph glyph, TextField field, String suffix) {
		if (isBound(field)) {
			unbind(glyph, field);
		} else {
			bind(glyph, field, suffix);
		}
	}

	private void setLinkActive(Glyph glyph, boolean active) {
		if (active) {
			glyph.setTextFill(Color.BLACK);
		} else {
			glyph.setTextFill(Color.LIGHTGREY);
		}
	}

	private void onIncludeVideoInvalidated(Observable obs) {
		filenamesGridPane.changeRowVisibility(filenamesGridPane.getChildRowIndex(videoField),
				includeVideoCheckBox.isSelected());
	}

	private void onIncludeBackgroundInvalidated(Observable obs) {
		boolean isSelected = includeBackgroundCheckBox.isSelected();
		filenamesGridPane.changeRowVisibility(filenamesGridPane.getChildRowIndex(backgroundField), isSelected);
		addCoCheckBox.setDisable(isSelected);
		if (isSelected) {
			addCoCheckBox.setSelected(true);
		}
	}

	private void onAddCoCheckBoxInvalidated(Observable obs) {
		if (coverField.textProperty().isBound()) {
			if (addCoCheckBox.isSelected()) {
				bindToFilename(coverField, COVER_SUFFIX);
			} else {
				bindToFilename(coverField, null);
			}
		} else {
			boolean hasCoverSuffix = coverField.getText().endsWith(COVER_SUFFIX);
			if (addCoCheckBox.isSelected()) {
				if (!hasCoverSuffix) {
					coverField.setText(coverField.getText() + COVER_SUFFIX);
				}
			} else {
				if (hasCoverSuffix) {
					coverField.setText(coverField.getText().substring(0,
							coverField.getText().length() - COVER_SUFFIX.length()));
				}
			}
		}

	}

	private void setFilename(Glyph glyph, TextField field, TextField extensionField, String value) {
		setFilename(glyph, field, extensionField::setText, value);
	}

	private void setFilename(Glyph glyph, TextField field, ComboBox<String> extensionField, String value) {
		setFilename(glyph, field, extensionField.getSelectionModel()::select, value);
	}

	private void setFilename(Glyph glyph, TextField field, Consumer<String> extensionSetter, String value) {
		String extension = Utils.getFileExtension(value);
		String fileName = Utils.trimExtension(value);
		if (!extension.isEmpty()) {
			extensionSetter.accept(extension);
		}

		if (!fileName.equals(field.getText())) {
			if (isBound(field)) {
				unbind(glyph, field);
			}
			field.setText(fileName);
		}
	}

	public void setSongArtist(String value) {
		artistField.setText(value);
	}

	public void setSongTitle(String value) {
		titleField.setText(value);
	}

	public void setAudioFilename(String value) {
		setFilename(audioLinkGlyph, audioField, audioExtensionField, value);
	}

	public void setCoverFilename(String value) {
		setFilename(coverLinkGlyph, coverField, coverExtensionField, value);
		boolean hasCoverSuffix = coverField.getText().endsWith(COVER_SUFFIX);
		addCoCheckBox.setSelected(hasCoverSuffix);

		if (coverField.getText().equals(filenameField.getText())
				|| coverField.getText().equals(filenameField.getText() + COVER_SUFFIX)) {
			bind(coverLinkGlyph, coverField, addCoCheckBox.isSelected() ? COVER_SUFFIX : null);
		}
	}

	public void setVideoFilename(String value) {
		setFilename(videoLinkGlyph, videoField, videoExtensionField, value);
	}

	public void setBackgroundFilename(String value) {
		setFilename(backgroundLinkGlyph, backgroundField, backgroundExtensionField, value);
	}

	public void hideBackground() {
		if (this.isShowing()) {
			includeBackgroundCheckBox.setSelected(true);
		} else {
			hideBackground = true;
		}
	}

	public void hideVideo() {
		if (this.isShowing()) {
			includeVideoCheckBox.setSelected(true);
		} else {
			hideVideo = true;
		}
	}

	public class FilenamesEditResult {
		private String artist;
		private String title;
		private String audioFilename;
		private String coverFilename;
		private String backgroundFilename;
		private String videoFilename;
		private FormatSpecification formatSpecification;

		private FilenamesEditResult() {
			super();
			artist = artistField.getText();
			title = titleField.getText();
			audioFilename = generateFilename(audioField.getText(), audioExtensionField.getValue());
			coverFilename = generateFilename(coverField.getText(), coverExtensionField.getText());
			if (includeBackgroundCheckBox.isSelected()) {
				backgroundFilename = generateFilename(backgroundField.getText(),
						backgroundExtensionField.getText());
			}
			if (includeVideoCheckBox.isSelected()) {
				videoFilename = generateFilename(videoField.getText(),
						videoExtensionField.getText());
			}
			formatSpecification = FormatSpecification
					.tryParse(formatSpecificationChoiceBox.getSelectionModel().getSelectedItem())
					.orElse(null);
		}

		private String generateFilename(String name, String extension) {
			return String.join(".", name, extension);
		}

		public String getArtist() {
			return artist;
		}

		public String getTitle() {
			return title;
		}

		public String getAudioFilename() {
			return audioFilename;
		}

		public String getCoverFilename() {
			return coverFilename;
		}

		public Optional<String> getBackgroundFilename() {
			return Optional.ofNullable(backgroundFilename);
		}

		public Optional<String> getVideoFilename() {
			return Optional.ofNullable(videoFilename);
		}

		public Optional<FormatSpecification> getFormatSpecification() {
			return Optional.ofNullable(formatSpecification);
		}

	}

}
