package main.java.com.github.nianna.karedi.region;

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
	public ReadOnlyObjectProperty<T> lowerXBoundProperty();
	public ReadOnlyObjectProperty<T> upperXBoundProperty();
	public ReadOnlyObjectProperty<T> lowerYBoundProperty();
	public ReadOnlyObjectProperty<T> upperYBoundProperty();

	default public int compareTo(Bounded<T> o) {
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

	default public T getLowerXBound() {
		return lowerXBoundProperty().get();
	}

	default public T getLowerYBound() {
		return lowerYBoundProperty().get();
	}

	default public T getUpperXBound() {
		return upperXBoundProperty().get();
	}

	default public T getUpperYBound() {
		return upperYBoundProperty().get();
	}

	default public boolean inBoundsX(T value) {
		return inRangeX(value) || value.compareTo(getUpperXBound()) == 0;
	}

	default public boolean inBoundsY(T value) {
		return inRangeY(value) || value.compareTo(getUpperYBound()) == 0;
	}

	default public boolean inRangeX(T value) {
		return (value.compareTo(getLowerXBound()) >= 0 && value.compareTo(getUpperXBound()) < 0);
	}

	default public boolean inRangeY(T value) {
		return (value.compareTo(getLowerYBound()) >= 0 && value.compareTo(getUpperYBound()) < 0);
	}

	default public boolean inBounds(T x, T y) {
		return inBoundsX(x) && inBoundsY(y);
	}

	default public boolean inBounds(Bounded<T> bounds) {
		return inBoundsX(bounds) && inBoundsY(bounds);
	}

	default public boolean inBoundsX(Bounded<T> bounds) {
		return inBoundsX(bounds.getLowerXBound()) && inBoundsX(bounds.getUpperXBound());
	}

	default public boolean inBoundsY(Bounded<T> bounds) {
		return inBoundsY(bounds.getLowerYBound()) && inBoundsY(bounds.getUpperYBound());
	}
}
