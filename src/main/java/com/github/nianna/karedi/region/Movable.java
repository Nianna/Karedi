package com.github.nianna.karedi.region;

/**
 * A class implements the {@code Movable} interface to indicate that it can be
 * moved in at least some directions.
 * 
 * @param <T>
 *            the type of the units in which the movement's length should be
 *            expressed
 */
public interface Movable<T> {
	public boolean move(Direction direction, T by);
	public boolean canMove(Direction direction, T by);
}
