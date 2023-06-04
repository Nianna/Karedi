package com.github.nianna.karedi.region;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * A bounded is an object that can be restricted in two dimensions by 4 extreme
 * values which may not be directly modifiable.
 * 
 * @param <T>
 *            the type of the extreme values
 */
public interface Bounded<T extends Comparable<T>> extends Observable {
	ReadOnlyObjectProperty<T> lowerXBoundProperty();
	ReadOnlyObjectProperty<T> upperXBoundProperty();
	ReadOnlyObjectProperty<T> lowerYBoundProperty();
	ReadOnlyObjectProperty<T> upperYBoundProperty();

	default int compareTo(Bounded<T> o) {
		int outcome = getLowerXBound().compareTo(o.getLowerXBound());
		if (outcome != 0) {
			return outcome;
		}
		outcome = getUpperXBound().compareTo(o.getUpperXBound());
		if (outcome != 0) {
			return outcome;
		}
		outcome = getLowerYBound().compareTo(o.getLowerYBound());
		if (outcome != 0) {
			return outcome;
		}
		return getUpperYBound().compareTo(o.getUpperYBound());
	}

	default T getLowerXBound() {
		return lowerXBoundProperty().get();
	}

	default T getLowerYBound() {
		return lowerYBoundProperty().get();
	}

	default T getUpperXBound() {
		return upperXBoundProperty().get();
	}

	default T getUpperYBound() {
		return upperYBoundProperty().get();
	}

	default boolean inBoundsX(T value) {
		return inRangeX(value) || value.compareTo(getUpperXBound()) == 0;
	}

	default boolean inBoundsY(T value) {
		return inRangeY(value) || value.compareTo(getUpperYBound()) == 0;
	}

	default boolean inRangeX(T value) {
		return (value.compareTo(getLowerXBound()) >= 0 && value.compareTo(getUpperXBound()) < 0);
	}

	default boolean inRangeY(T value) {
		return (value.compareTo(getLowerYBound()) >= 0 && value.compareTo(getUpperYBound()) < 0);
	}

	default boolean inBounds(T x, T y) {
		return inBoundsX(x) && inBoundsY(y);
	}

	default boolean inBounds(Bounded<T> bounds) {
		return inBoundsX(bounds) && inBoundsY(bounds);
	}

	default boolean inBoundsX(Bounded<T> bounds) {
		return inBoundsX(bounds.getLowerXBound()) && inBoundsX(bounds.getUpperXBound());
	}

	default boolean inBoundsY(Bounded<T> bounds) {
		return inBoundsY(bounds.getLowerYBound()) && inBoundsY(bounds.getUpperYBound());
	}
}
