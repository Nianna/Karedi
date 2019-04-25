package main.java.com.github.nianna.karedi.control;

import javafx.scene.Group;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Pair;

public class Grid extends Group {
    private final Path minorVerticalLines = new Path();
    private final Path verticalLines = new Path();
    private final Path horizontalLines = new Path();
    private final double pixelOffset = 0.5;

    public Grid() {
        horizontalLines.getStyleClass().setAll("chart-horizontal-grid-lines");
        minorVerticalLines.getStyleClass().setAll("chart-vertical-grid-lines");
        verticalLines.getStyleClass().setAll("chart-bigger-vertical-grid-lines");
        this.getChildren().addAll(minorVerticalLines, verticalLines, horizontalLines);
    }

    public void drawVerticalLines(Iterable<Pair<Integer, Double>> marks, double length) {
        minorVerticalLines.getElements().clear();
        verticalLines.getElements().clear();
        for (Pair<Integer, Double> pair : marks) {
            if (pair.getKey() % 4 == 0) {
                verticalLines.getElements().add(new MoveTo(pixelOffset + pair.getValue(), 0));
                verticalLines.getElements().add(new LineTo(pixelOffset + pair.getValue(), length));
            } else {
                minorVerticalLines.getElements().add(new MoveTo(pixelOffset + pair.getValue(), 0));
                minorVerticalLines.getElements().add(new LineTo(pixelOffset + pair.getValue(), length));
            }
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
