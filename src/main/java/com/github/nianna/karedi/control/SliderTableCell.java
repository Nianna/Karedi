package main.java.com.github.nianna.karedi.control;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;

public class SliderTableCell<S> extends TableCell<S, DoubleProperty> {
	private DoubleProperty boundTo;
	private Slider slider;

	public SliderTableCell(Double min, Double max) {
		slider = new ScrollableSlider();
		slider.setMin(min);
		slider.setMax(max);

		slider.setMajorTickUnit((max - min) / 20);
		slider.setBlockIncrement(slider.getMajorTickUnit());
	}

	@Override
	protected void updateItem(DoubleProperty item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			clearContent();
		} else {
			addContent(item);
		}
	}

	private void addContent(DoubleProperty item) {
		if (boundTo != null) {
			slider.valueProperty().unbindBidirectional(boundTo);
		}
		slider.setValue(item.getValue());
		slider.valueProperty().bindBidirectional(item);
		boundTo = item;
		setText(null);
		setGraphic(slider);
	}

	private void clearContent() {
		setText(null);
		setGraphic(null);
		if (boundTo != null) {
			slider.valueProperty().unbindBidirectional(boundTo);
		}
		boundTo = null;
	}

}