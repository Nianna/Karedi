package com.github.nianna.karedi.region;

import java.util.List;

/**
 * A class implements the {@code MovableContainer} interface if it itself cannot
 * be moved, but its children can.
 *
 * @param <S>
 *            the type of the children
 * @param <T>
 *            the type of the units in which the length of the movement should
 *            be expressed
 */
public interface MovableContainer<S extends Movable<T>, T> extends Movable<T> {
	public List<S> getMovableChildren();

	@Override
	default public boolean move(Direction direction, T by) {
		boolean outcome = false;
		for (S child : getMovableChildren()) {
			outcome = child.move(direction, by) || outcome;
		}
		return outcome;
	}

	@Override
	default public boolean canMove(Direction direction, T by) {
		boolean outcome = false;
		for (S child : getMovableChildren()) {
			outcome = child.canMove(direction, by) || outcome;
		}
		return outcome;
	}
}
