package com.github.nianna.karedi.display;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import com.github.nianna.karedi.audio.MidiPlayer;
import com.github.nianna.karedi.util.MusicalScale;

public class Piano extends Region {
	private static final double WIDTH = 70;
	private final PianoDisplayer keyBoard = new PianoDisplayer(WIDTH);
	private final Tooltip tooltip;

	private IntegerProperty upperBound = new SimpleIntegerProperty();
	private IntegerProperty lowerBound = new SimpleIntegerProperty();

	private InvalidationListener listener;

	public Piano() {
		listener = (obs -> keyBoard.refresh(getLowerBound(), getUpperBound()));

		upperBound.addListener(listener);
		lowerBound.addListener(listener);
		keyBoard.heightProperty().addListener(listener);
		keyBoard.heightProperty().bind(this.prefHeightProperty());

		this.setOnMouseClicked(this::onMouseClicked);
		this.setOnMouseMoved(this::onMouseMoved);

		tooltip = new Tooltip();
		Tooltip.install(this, tooltip);
	}

	public void show() {
		getChildren().clear();
		getChildren().add(keyBoard);
	}

	public void hide() {
		getChildren().clear();
	}

	private void onMouseMoved(MouseEvent event) {
		int note = noteFromEventY(event.getY());
		tooltip.setText(MusicalScale.getNote(note).toString());
	}

	public void toggle() {
		if (getChildren().isEmpty()) {
			show();
		} else {
			hide();
		}
	}

	private void onMouseClicked(MouseEvent event) {
		int note = noteFromEventY(event.getY());
		List<Integer> toPlay = new ArrayList<Integer>();
		toPlay.add(note);
		play(toPlay);
	}

	public void play(List<Integer> notes) {
		notes = notes.stream().map(note -> note + 60).collect(Collectors.toList());
		MidiPlayer.play(notes);
	}

	private int noteFromEventY(double y) {
		int size = getUpperBound() - getLowerBound();
		if (size <= 0) {
			return 0;
		}
		double unitHeight = keyBoard.getHeight() / size;
		Long note = getUpperBound() - Math.round(y / unitHeight);
		return note.intValue();
	}

	public IntegerProperty lowerBoundProperty() {
		return lowerBound;
	}

	public Integer getLowerBound() {
		return lowerBound.get();
	}

	public void setLowerBound(int value) {
		lowerBound.set(value);
	}

	public IntegerProperty upperBoundProperty() {
		return upperBound;
	}

	public Integer getUpperBound() {
		return upperBound.get();
	}

	public void setUpperBound(int value) {
		upperBound.set(value);
	}

}
