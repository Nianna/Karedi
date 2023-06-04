package com.github.nianna.karedi.display;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.github.nianna.karedi.region.Bounded;
import com.github.nianna.karedi.util.ListenersUtils;

public class FillBar<T extends Number & Comparable<T>> extends Pane {
	private Map<Bounded<T>, Rectangle> map = new HashMap<>();
	private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.GRAY);
	private DoubleProperty unitWidth = new SimpleDoubleProperty();

	private ObservableList<? extends Bounded<T>> boundItems;
	private ListChangeListener<? super Bounded<T>> contentListener;

	public FillBar() {
		FXMLLoader fxmlLoader = new FXMLLoader(FillBar.class.getResource("/fxml/FillBar.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		contentListener = ListenersUtils.createListContentChangeListener(this::addBounded,
				this::removeBounded);
	}

	public void addBounded(Bounded<T> bounds) {
		if (!map.containsKey(bounds)) {
			Rectangle rectangle = makeNewRectangle(bounds);
			getChildren().add(rectangle);
			map.put(bounds, rectangle);
		}
	}

	public void removeBounded(Bounded<T> bounds) {
		Rectangle rect = map.remove(bounds);
		getChildren().remove(rect);
	}

	private Rectangle makeNewRectangle(Bounded<T> bounds) {
		Rectangle rectangle = new Rectangle();

		DoubleBinding length = Bindings.createDoubleBinding(() -> {
			return bounds.getUpperXBound().doubleValue() - bounds.getLowerXBound().doubleValue();
		}, bounds.upperXBoundProperty(), bounds.lowerXBoundProperty());
		DoubleBinding position = Bindings.createDoubleBinding(() -> {
			return bounds.getLowerXBound().doubleValue() * unitWidth.get();
		}, bounds.lowerXBoundProperty(), unitWidth);

		rectangle.widthProperty().bind(Bindings.multiply(length, unitWidth));
		rectangle.heightProperty().bind(this.heightProperty());
		rectangle.translateXProperty().bind(position);
		rectangle.fillProperty().bind(color);

		return rectangle;
	}

	public void setAll(Iterable<? extends Bounded<T>> items) {
		getChildren().clear();
		map.clear();
		items.forEach(item -> addBounded(item));
	}

	public void bindContent(ObservableList<? extends Bounded<T>> items) {
		unbindContent();
		setAll(items);
		items.addListener(contentListener);
		boundItems = items;
	}

	public void unbindContent() {
		if (boundItems != null) {
			boundItems.removeListener(contentListener);
		}
		boundItems = null;
	}

	public DoubleProperty unitWidthProperty() {
		return unitWidth;
	}

	public final double getUnitWidth() {
		return unitWidth.get();
	}

	public final void setUnitWidth(double value) {
		unitWidth.set(value);
	}

	public ObjectProperty<Color> colorProperty() {
		return color;
	}

	public final void setColor(Color value) {
		color.set(value);
	}

	public final Color getColor() {
		return color.get();
	}

}
