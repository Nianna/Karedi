package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ConvertFormatDialog extends StyleableDialog<ButtonType> {

	private final List<TagRepresentation> allTags;
	private final List<TagRepresentation> tagsToBeRemoved;
	private final List<TagRepresentation> oldVersionsOfTagsToBeUpdated;
	private final List<TagRepresentation> tagsToBeAdded;
	private final List<TagRepresentation> tagsToBeUpdated;

	@FXML
	TableView<TagRepresentation> tagsChangesView;
	@FXML
	TableColumn<TagRepresentation, String> keyColumn;
	@FXML
	TableColumn<TagRepresentation, String> valueColumn;

	public ConvertFormatDialog(List<TagRepresentation> allTags,
							   List<TagRepresentation> tagsToBeRemoved,
							   List<TagRepresentation> tagsToBeAdded,
							   List<TagRepresentation> tagsToBeUpdated) {
		this.allTags = new ArrayList<>(allTags);
		this.tagsToBeRemoved = tagsToBeRemoved;
		this.oldVersionsOfTagsToBeUpdated = new ArrayList<>();
		this.tagsToBeAdded = tagsToBeAdded;
		this.tagsToBeUpdated = tagsToBeUpdated;

		DialogUtils.loadPane(this, getClass().getResource("/fxml/ConvertFormatDialogPaneLayout.fxml"));
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		setTitle(I18N.get("dialog.convert_format.title"));
	}

	@FXML
	public void initialize() {
		for (TagRepresentation tagToBeUpdated: tagsToBeUpdated) {
			TagRepresentation currentVersionOfThisTag = allTags.stream()
					.filter(tag -> tag.key.equals(tagToBeUpdated.key))
					.findFirst()
					.orElseThrow();
			oldVersionsOfTagsToBeUpdated.add(currentVersionOfThisTag);
			allTags.add(allTags.indexOf(currentVersionOfThisTag) + 1, tagToBeUpdated);
		}
		ObservableList<TagRepresentation> items = FXCollections.concat(
				FXCollections.observableList(allTags),
				FXCollections.observableList(tagsToBeAdded)
		);
		tagsChangesView.setItems(items);
		tagsChangesView.setSelectionModel(null);
		keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		tagsChangesView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(TagRepresentation item, boolean empty) {
                super.updateItem(item, empty);
                determineColor(item)
						.map(Color::valueOf)
                        .map(Background::fill)
                        .ifPresentOrElse(this::setBackground, () -> setBackground(null));
            }
        });
	}

	private Optional<String> determineColor(TagRepresentation item) {
		if (oldVersionsOfTagsToBeUpdated.contains(item)) {
			return Optional.of("#eeeeee");
		}
		if (tagsToBeUpdated.contains(item)) {
			return Optional.of("#fffec4");
		}
		if (tagsToBeAdded.contains(item)) {
			return Optional.of("#baffba");
		}
		if (tagsToBeRemoved.contains(item)) {
			return Optional.of("#ffd7d1");
		}
		return Optional.empty();
	}

	public static class TagRepresentation {

		private final String key;
		private final String value;

        public TagRepresentation(String key, String value) {
            this.key = key;
            this.value = value;
        }

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			TagRepresentation that = (TagRepresentation) o;
			return Objects.equals(key, that.key) && Objects.equals(value, that.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, value);
		}
	}
}
