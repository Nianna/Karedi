package com.github.nianna.karedi.region;

import com.github.nianna.karedi.util.MathUtils;

/**
 * A non-generic version of {@link Bounded}, designed for regions whose borders
 * can be expressed as integer values.
 */
public interface IntBounded extends Bounded<Integer> {

	default boolean isValid() {
		return getLowerXBound() != null && getUpperXBound() != null && getLowerYBound() != null
				&& getUpperYBound() != null
				&& MathUtils.min(getLowerXBound(), getUpperXBound()).equals(getLowerXBound())
				&& MathUtils.min(getLowerYBound(), getUpperYBound()).equals(getLowerYBound());
	}

	default int compareTo(IntBounded o) {
		if (this.isValid()) {
			if (o.isValid()) {
				return compareTo((Bounded<Integer>) o);
			} else {
				return 1;
			}
		} else {
			if (o.isValid()) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
