package main.java.com.github.nianna.karedi.control;

import javafx.scene.Group;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Grid extends Group {
	private final Path verticalLines = new Path();
	private final Path horizontalLines = new Path();
	private final double pixelOffset = 0.5;

	public Grid() {
		horizontalLines.getStyleClass().setAll("chart-horizontal-grid-lines");
		verticalLines.getStyleClass().setAll("chart-vertical-grid-lines");
		this.getChildren().addAll(verticalLines, horizontalLines);
	}

	public void drawVerticalLines(Iterable<Double> marks, double length) {
		verticalLines.getElements().clear();
		for (double mark : marks) {
			verticalLines.getElements().add(new MoveTo(pixelOffset + mark, 0));
			verticalLines.getElements().add(new LineTo(pixelOffset + mark, length));
		}
	}

	public void drawHorizontalLines(Iterable<Double> marks, double length) {
		horizontalLines.getElements().clear();
		for (double mark : marks) {
			horizontalLines.getElements().add(new MoveTo(0, pixelOffset + mark));
			horizontalLines.getElements().add(new LineTo(length, pixelOffset + mark));
		}
	}
}
