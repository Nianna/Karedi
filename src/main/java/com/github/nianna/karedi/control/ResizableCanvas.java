package main.java.com.github.nianna.karedi.control;

import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {
	public ResizableCanvas() {
		super();
	}

	public ResizableCanvas(double width, double height) {
		super(width, height);
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public double minHeight(double width) {
		return 0;
	}

}