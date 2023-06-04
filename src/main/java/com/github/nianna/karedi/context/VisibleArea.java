package com.github.nianna.karedi.context;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import com.github.nianna.karedi.region.Area;
import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.util.MusicalScale;

public class VisibleArea implements IntBounded {
	public static final int MIN_WIDTH = 4;
	public static final int MIN_HEIGHT = 14;
	public static final int TOP_MARGIN = 2;
	public static final int BOTTOM_MARGIN = 2;
	public static final int LEFT_MARGIN = 1;
	public static final int RIGHT_MARGIN = 1;

	private BeatRange beatRange;
	private Area area;

	VisibleArea(BeatRange beatRange) {
		this.beatRange = beatRange;
		area = new Area(0, 20, -8, 8);
	}

	boolean assertBorderlessBoundsVisible(IntBounded bounds) {
		return assertBoundsVisible(addMargins(bounds));
	}

	void assertBoundsYVisible(IntBounded bounds) {
		if (bounds.isValid() && !area.inBoundsY(bounds)) {
			int lowerYBound = Math.min(bounds.getLowerYBound(), area.getLowerYBound());
			int upperYBound = Math.max(bounds.getUpperYBound(), area.getUpperYBound());
			setYBounds(lowerYBound, upperYBound);
		}
	}

	boolean assertBoundsVisible(IntBounded bounds) {
		assert (bounds.isValid());

		if (area.inBounds(bounds)) {
			return false;
		}

		int lowerXBound;
		int upperXBound;

		if (bounds.getUpperXBound() < area.getLowerXBound()
				|| bounds.getLowerXBound() >= area.getUpperXBound()) {
			// Intersection is empty
			int boundsWidth = bounds.getUpperXBound() - bounds.getLowerXBound();
			int visibleAreaWidth = area.getUpperXBound() - area.getLowerXBound();
			lowerXBound = bounds.getLowerXBound();
			upperXBound = bounds.getLowerXBound() + Math.max(boundsWidth, visibleAreaWidth);
		} else {
			lowerXBound = Math.min(bounds.getLowerXBound(), area.getLowerXBound());
			upperXBound = Math.max(bounds.getUpperXBound(), area.getUpperXBound());
		}

		if (!area.inBoundsX(bounds)) {
			setXBounds(lowerXBound, upperXBound);
		}

		assertBoundsYVisible(bounds);
		return true;
	}

	void adjustToBounds(IntBounded bounds) {
		assert (bounds.isValid());
		bounds = addMargins(bounds);
		setBounds(bounds);
	}

	boolean setBounds(IntBounded bounds) {
		if (bounds.isValid()) {
			return setBounds(bounds.getLowerXBound(), bounds.getUpperXBound(),
					bounds.getLowerYBound(), bounds.getUpperYBound());
		}
		return false;
	}

	boolean setBounds(int lowerXBound, int upperXBound, int lowerYBound, int upperYBound) {
		if (BoundingBox.boundsFrom(lowerXBound, upperXBound, lowerYBound, upperYBound)
				.compareTo(this) != 0) {
			area.freeze();
			boolean xBoundsChanged = setXBounds(lowerXBound, upperXBound);
			boolean yBoundsChanged = setYBounds(lowerYBound, upperYBound);
			area.unfreeze();
			return xBoundsChanged || yBoundsChanged;
		} else {
			return false;
		}
	}

	boolean setXBounds(int lowerXBound, int upperXBound) {
		IntBounded maxVisibleArea = getMaxVisibleArea();
		lowerXBound = Math.max(lowerXBound, maxVisibleArea.getLowerXBound());
		upperXBound = Math.max(upperXBound, lowerXBound + MIN_WIDTH);
		if (upperXBound > maxVisibleArea.getUpperXBound()) {
			upperXBound = maxVisibleArea.getUpperXBound();
			lowerXBound = Math.min(lowerXBound, upperXBound - MIN_WIDTH);
		}
		if (area.getLowerXBound() != lowerXBound || area.getUpperXBound() != upperXBound) {
			area.setXBounds(lowerXBound, upperXBound);
			return true;
		}
		return false;
	}

	boolean setYBounds(int lowerYBound, int upperYBound) {
		IntBounded maxVisibleArea = getMaxVisibleArea();
		lowerYBound = Math.max(lowerYBound, maxVisibleArea.getLowerYBound());
		upperYBound = Math.max(upperYBound, lowerYBound + MIN_HEIGHT);
		if (upperYBound > maxVisibleArea.getUpperYBound()) {
			upperYBound = maxVisibleArea.getUpperYBound();
			lowerYBound = Math.min(lowerYBound, upperYBound - MIN_HEIGHT);
		}
		if (area.getLowerYBound() != lowerYBound || area.getUpperYBound() != upperYBound) {
			area.setYBounds(lowerYBound, upperYBound);
			return true;
		}
		return false;
	}

	boolean increaseXBounds(int by) {
		int lowerBound = area.getLowerXBound() - by;
		int upperBound = area.getUpperXBound() + by;
		if (upperBound - lowerBound >= MIN_WIDTH) {
			return setXBounds(lowerBound, upperBound);
		}
		return false;
	}

	boolean increaseYBounds(int by) {
		int lowerBound = area.getLowerYBound() - by;
		int upperBound = area.getUpperYBound() + by;
		if (upperBound - lowerBound >= MIN_HEIGHT) {
			return setYBounds(lowerBound, upperBound);
		}
		return false;
	}

	IntBounded addMargins(IntBounded bounds) {
		if (bounds.isValid()) {
			int lowerXBound = bounds.getLowerXBound() == 0 ? 0
					: bounds.getLowerXBound() - LEFT_MARGIN;
			int upperXBound = bounds.getUpperXBound() + RIGHT_MARGIN;
			int lowerYBound = bounds.getLowerYBound() - BOTTOM_MARGIN;
			int upperYBound = bounds.getUpperYBound() + TOP_MARGIN;
			return BoundingBox.boundsFrom(lowerXBound, upperXBound, lowerYBound, upperYBound);
		}
		return bounds;
	}

	void setDefault() {
		setBounds(0, 5 * MIN_WIDTH, -MIN_HEIGHT / 2, MIN_HEIGHT / 2);
	}

	private IntBounded getMaxVisibleArea() {
		return addMargins(BoundingBox.boundsFrom(beatRange.getMinBeat(), beatRange.getMaxBeat(),
				MusicalScale.MIN_TONE, MusicalScale.MAX_TONE));
	}

	void move(Direction direction, Integer by) {
		if (by < 0) {
			move(Direction.opposite(direction), -by);
			return;
		}
		IntBounded maxVisibleArea = getMaxVisibleArea();
		switch (direction) {
		case UP:
			by = Math.min(by, maxVisibleArea.getUpperYBound() - area.getUpperYBound());
			break;
		case DOWN:
			by = Math.min(by, Math.abs(maxVisibleArea.getLowerYBound() - area.getLowerYBound()));
			break;
		case LEFT:
			by = Math.min(by, Math.abs(maxVisibleArea.getLowerXBound() - area.getLowerXBound()));
			break;
		case RIGHT:
			by = Math.min(by, maxVisibleArea.getUpperXBound() - area.getUpperXBound());
			break;
		}
		if (by > 0) {
			area.move(direction, by);
		}
	}

	@Override
	public ReadOnlyObjectProperty<Integer> lowerXBoundProperty() {
		return area.lowerXBoundProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Integer> upperXBoundProperty() {
		return area.upperXBoundProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Integer> lowerYBoundProperty() {
		return area.lowerYBoundProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Integer> upperYBoundProperty() {
		return area.upperYBoundProperty();
	}

	@Override
	public void addListener(InvalidationListener listener) {
		area.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		area.removeListener(listener);
	}

	public void invalidate() {
		area.invalidate();
	}
}
