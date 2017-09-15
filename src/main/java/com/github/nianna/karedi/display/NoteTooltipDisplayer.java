package main.java.com.github.nianna.karedi.display;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.control.TitledKeyValueGrid;

class NoteTooltipDisplayer extends TitledKeyValueGrid {
	private Label lyricsLabel = new Label();
	private Label toneLabel = new Label();
	private Label lengthLabel = new Label();
	private Label startBeatLabel = new Label();
	private Label startTimeLabel = new Label();

	NoteTooltipDisplayer() {
		addTitle(lyricsLabel);

		addRow(I18N.get("note.tooltip.tone"), toneLabel);
		addRow(I18N.get("note.tooltip.length"), lengthLabel);
		addRow(I18N.get("note.tooltip.start_beat"), startBeatLabel);
		addRow(I18N.get("note.tooltip.start_time"), startTimeLabel);
	}

	StringProperty lyricsProperty() {
		return lyricsLabel.textProperty();
	}

	StringProperty lengthProperty() {
		return lengthLabel.textProperty();
	}

	StringProperty toneProperty() {
		return toneLabel.textProperty();
	}

	StringProperty startBeatProperty() {
		return startBeatLabel.textProperty();
	}

	StringProperty startTimeProperty() {
		return startTimeLabel.textProperty();
	}

}