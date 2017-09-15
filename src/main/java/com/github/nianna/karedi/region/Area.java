package main.java.com.github.nianna.karedi.region;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import main.java.com.github.nianna.karedi.util.ListenersManager;

/**
 * {@code Area} is a two-dimensional region that is restricted by 4 integer
 * values. Boundaries can be either manually updated or by performing
 * {@code move} or {@code resize} operations.
 * <p>
 * It implements the {@link javafx.beans.Observable} interface.
 */
public class Area implements IntBounded, Movable<Integer>, Resizable<Integer> {
	private ReadOnlyObjectWrapper<Integer> lowerXBound = new ReadOnlyObjectWrapper<Integer>();
	private ReadOnlyObjectWrapper<Integer> upperXBound = new ReadOnlyObjectWrapper<Integer>();
	private ReadOnlyObjectWrapper<Integer> lowerYBound = new ReadOnlyObjectWrapper<Integer>();
	private ReadOnlyObjectWrapper<Integer> upperYBound = new ReadOnlyObjectWrapper<Integer>();
	private ListenersManager listenersManager = new ListenersManager(this);
	private boolean freezed = false;

	public Area(int lowerX, int upperX, int lowerY, int upperY) {
		setBounds(lowerX, upperX, lowerY, upperY);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		listenersManager.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		listenersManager.removeListener(listener);
	}

	public void invalidate() {
		if (!freezed) {
			listenersManager.invalidate();
		}
	}

	@Override
	public void resize(Direction direction, Integer by) {
		switch (direction) {
		case LEFT:
			lowerXBound.set(getLowerXBound() - by);
			break;
		case RIGHT:
			upperXBound.set(getUpperXBound() + by);
			break;
		case DOWN:
			lowerYBound.set(getLowerYBound() - by);
			break;
		case UP:
			upperYBound.set(getUpperYBound() + by);
			break;
		default:
		}
		invalidate();
	}

	@Override
	public boolean move(Direction direction, Integer by) {
		IntBounded oldBounds = BoundingBox.boundsFrom(this);
		switch (direction) {
		case LEFT:
			lowerXBound.set(getLowerXBound() - by);
			upperXBound.set(getUpperXBound() - by);
			break;
		case RIGHT:
			lowerXBound.set(getLowerXBound() + by);
			upperXBound.set(getUpperXBound() + by);
			break;
		case DOWN:
			lowerYBound.set(getLowerYBound() - by);
			upperYBound.set(getUpperYBound() - by);
			break;
		case UP:
			lowerYBound.set(getLowerYBound() + by);
			upperYBound.set(getUpperYBound() + by);
			break;
		default:
		}
		if (oldBounds.compareTo(this) != 0) {
			invalidate();
			return true;
		}
		return false;
	}

	@Override
	public boolean canMove(Direction direction, Integer by) {
		return true;
	}

	@Override
	public ReadOnlyObjectProperty<Integer> lowerXBoundProperty() {
		return lowerXBound.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Integer> upperXBoundProperty() {
		return upperXBound.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Integer> lowerYBoundProperty() {
		return lowerYBound.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Integer> upperYBoundProperty() {
		return upperYBound.getReadOnlyProperty();
	}

	public void setXBounds(int lowerX, int upperX) {
		lowerXBound.set(lowerX);
		upperXBound.set(upperX);
		invalidate();
	}

	public void setYBounds(int lowerY, int upperY) {
		lowerYBound.set(lowerY);
		upperYBound.set(upperY);
		invalidate();
	}

	public void setBounds(int lowerX, int upperX, int lowerY, int upperY) {
		lowerXBound.set(lowerX);
		upperXBound.set(upperX);
		lowerYBound.set(lowerY);
		upperYBound.set(upperY);
		invalidate();
	}

	public void setUpperXBound(int value) {
		upperXBound.set(value);
		invalidate();
	}

	public void setUpperYBound(int value) {
		upperYBound.set(value);
		invalidate();
	}

	public void setLowerXBound(int value) {
		lowerXBound.set(value);
		invalidate();
	}

	public void setLowerYBound(int value) {
		lowerYBound.set(value);
		invalidate();
	}

	public void freeze() {
		freezed = true;
	}

	public void unfreeze() {
		freezed = false;
		invalidate();
	}

	public boolean isFreezed() {
		return freezed;
	}
}