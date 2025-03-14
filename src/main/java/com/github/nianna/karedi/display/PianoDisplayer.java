package com.github.nianna.karedi.display;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.github.nianna.karedi.control.ResizableCanvas;
import com.github.nianna.karedi.util.MusicalScale;
import com.github.nianna.karedi.util.MusicalScale.Note;

class PianoDisplayer extends ResizableCanvas {
	private static final double DOT_DEFAULT_RADIUS = 5;
	static final double LINE_WIDTH = 0.3;
	private final GraphicsContext gc;
	private int notesCount;
	private double keyHeight;
	private double width;

	PianoDisplayer(double width) {
		setWidth(width);
		this.width = width;
		gc = getGraphicsContext2D();
	}

	void refresh(int lowerBound, int upperBound, Color whiteKeysColor) {
		notesCount = upperBound - lowerBound;
		keyHeight = getHeight() / notesCount;
		drawBorder(gc, width, getHeight(), whiteKeysColor, Color.BLACK);
		gc.setFill(Color.BLACK);
		for (int i = 0; i <= notesCount; ++i) {
			Note note = MusicalScale.getNote(upperBound - i);
			if (Note.isSharp(note)) {
				drawBlackKey(i);
				drawLineOnBlackKey(i);
			}
			if (note == Note.F) {
				divideWhiteKeys(i, LINE_WIDTH);
			}
			if (note == Note.C) {
				divideWhiteKeys(i, 2 * LINE_WIDTH);
				drawDot(i);
			}
		}
	}

	private void drawDot(int noteIndex) {
		gc.setFill(Color.RED);
		double dotSize = Math.min(DOT_DEFAULT_RADIUS, 0.5 * keyHeight);
		gc.fillOval(width * 0.85, noteIndex * keyHeight - dotSize / 2, dotSize, dotSize);
		gc.setFill(Color.BLACK);
	}

	private void drawBlackKey(int noteIndex) {
		gc.fillRect(0, -keyHeight / 2 + noteIndex * keyHeight, width * 0.75, keyHeight);
	}

	private void drawLineOnBlackKey(int noteIndex) {
		gc.fillRect(0, noteIndex * keyHeight, width, LINE_WIDTH);
	}

	private void divideWhiteKeys(int noteIndex, double lineWidth) {
		gc.fillRect(0, noteIndex * keyHeight + keyHeight * 0.5, width, lineWidth);
	}

	private void drawBorder(GraphicsContext gc2, double width, double height, Color fillColor,
			Color borderColor) {
		gc.setFill(borderColor);
		gc.fillRect(0, 0, width, height);
		gc.setFill(fillColor);
		gc.fillRect(LINE_WIDTH, LINE_WIDTH, width - 2 * LINE_WIDTH, height - 2 * LINE_WIDTH);
	}
}
