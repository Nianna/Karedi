package com.github.nianna.karedi.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;

public final class NumericNodeUtils {
	public static final double BASIC_DOUBLE_STEP = 0.01;
	public static final double BASIC_INTEGER_STEP = 1;

	private NumericNodeUtils() {
	}

	public static EventHandler<ScrollEvent> createUpdateValueOnScrollHandler(
			Supplier<Optional<? extends Number>> supplier, Consumer<Double> consumer,
			double basicStep) {
		return (event -> {
			boolean wheelDown = event.getDeltaY() < 0 || event.getDeltaX() < 0;

			supplier.get().ifPresent(oldValue -> {
				double step = basicStep;
				if (event.isControlDown()) {
					step *= 10;
				}
				if (event.isShiftDown()) {
					step *= 100;
				}
				if (wheelDown) {
					consumer.accept(oldValue.doubleValue() - step);
				} else {
					consumer.accept(oldValue.doubleValue() + step);
				}
				event.consume();
			});
		});
	}

	public static EventHandler<ScrollEvent> createUpdateDoubleValueOnScrollHandler(
			Supplier<Optional<? extends Number>> supplier, Consumer<Double> consumer) {
		return createUpdateValueOnScrollHandler(supplier, consumer, BASIC_DOUBLE_STEP);
	}

	public static EventHandler<ScrollEvent> createUpdateIntValueOnScrollHandler(
			Supplier<Optional<? extends Number>> supplier, Consumer<Double> consumer) {
		return createUpdateValueOnScrollHandler(supplier, consumer, BASIC_INTEGER_STEP);
	}

}
