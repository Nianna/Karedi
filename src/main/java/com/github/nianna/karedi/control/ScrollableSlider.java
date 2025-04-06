package com.github.nianna.karedi.control;

import javafx.scene.control.Slider;

public class ScrollableSlider extends Slider {

	public ScrollableSlider() {
		setOnScroll(event -> {
			if (event.getDeltaY() > 0) {
				increment();
			} else if (event.getDeltaY() < 0) {
				decrement();
			}
		});
	}
}
