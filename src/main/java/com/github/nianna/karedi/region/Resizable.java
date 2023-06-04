package com.github.nianna.karedi.region;

/**
 * A {@code Resizable} can be understood as a two-dimensional region whose size
 * can be decreased or increased by moving the borders.
 *
 * @param <T>
 *            the type of the units in which the borders can be moved
 */
public interface Resizable<T> {
	void resize(Direction direction, T by);
}
