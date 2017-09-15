package main.java.com.github.nianna.karedi.control;

import javafx.scene.control.Slider;

public class ScrollableSlider extends Slider {

	public ScrollableSlider() {
		setOnScroll(event -> {
			if (event.getDeltaY() > 0) {
				increment();
			} else {
				decrement();
			}
		});
	}
}
