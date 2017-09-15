package main.java.com.github.nianna.karedi.region;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import main.java.com.github.nianna.karedi.util.ListenersManager;
import main.java.com.github.nianna.karedi.util.MathUtils;

/**
 * A {@code BoundingBox} is the smallest rectangle in which all of the passed
 * elements can be fit.
 * 
 * @param <T>
 *            the type of the elements
 */
public class BoundingBox<T extends IntBounded> implements IntBounded {
	private ReadOnlyObjectWrapper<Integer> lowerXBound = new ReadOnlyObjectWrapper<Integer>();
	private ReadOnlyObjectWrapper<Integer> upperXBound = new ReadOnlyObjectWrapper<Integer>();
	private ReadOnlyObjectWrapper<Integer> lowerYBound = new ReadOnlyObjectWrapper<Integer>();
	private ReadOnlyObjectWrapper<Integer> upperYBound = new ReadOnlyObjectWrapper<Integer>();

	private ListenersManager listenerManager = new ListenersManager(this);

	private Iterable<? extends T> items;
	private IntBounded lastBounds;

	@Override
	public void addListener(InvalidationListener listener) {
		listenerManager.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		listenerManager.removeListener(listener);
	}

	private void invalidate() {
		if (lastBounds == null || lastBounds.compareTo(this) != 0) {
			listenerManager.invalidate();
			lastBounds = boundsFrom(this);
		}
	}

	private BoundingBox() {
		super();
		resetBounds();
	}

	public BoundingBox(Iterable<? extends T> items) {
		this();
		this.items = items;
		addAll(items);
	}

	public <S extends Observable & Iterable<? extends T>> BoundingBox(S items) {
		this((Iterable<? extends T>) items);
		items.addListener(invalidated -> {
			recalculateBounds();
			invalidate();
		});
	}

	public BoundingBox(ObservableList<? extends T> items) {
		this((Iterable<? extends T>) items);
		items.addListener((ListChangeListener<? super T>) c -> {
			IntBounded oldValues = BoundingBox.boundsFrom(this);
			while (c.next()) {
				if (!c.wasPermutated()) {
					if (c.wasUpdated()) {
						// Old values of updated items are unknown so
						// recalculation is necessary
						recalculateBounds();
					} else {
						removeAll(c.getRemoved());
						addAll(c.getAddedSubList());
					}
				}
			}
			if (this.compareTo(oldValues) != 0) {
				invalidate();
			}
		});
	}

	public static IntBounded boundsOf(ReadOnlyObjectProperty<Integer> lowerXBound,
			ReadOnlyObjectProperty<Integer> upperXBound,
			ReadOnlyObjectProperty<Integer> lowerYBound,
			ReadOnlyObjectProperty<Integer> upperYBound) {
		BoundingBox<IntBounded> box = new BoundingBox<>();
		box.bind(box.lowerXBound, lowerXBound);
		box.bind(box.upperXBound, upperXBound);
		box.bind(box.lowerYBound, lowerYBound);
		box.bind(box.upperYBound, upperYBound);
		return box;
	}

	private void bind(ReadOnlyObjectWrapper<Integer> target,
			ReadOnlyObjectProperty<Integer> source) {
		source.addListener(obs -> {
			target.set(source.get());
			invalidate();
		});
		target.set(source.get());
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

	public void recalculateBounds() {
		if (items != null) {
			resetBounds();
			addAll(items);
		}
	}

	private void resetBounds() {
		lowerXBound.set(null);
		upperXBound.set(null);
		lowerYBound.set(null);
		upperYBound.set(null);
	}

	private void addAll(Iterable<? extends T> items) {
		Integer minX = getLowerXBound();
		Integer maxX = getUpperXBound();
		Integer minY = getLowerYBound();
		Integer maxY = getUpperYBound();
		for (T item : items) {
			if (item.isValid()) {
				minX = MathUtils.min(minX, item.getLowerXBound());
				maxX = MathUtils.max(maxX, item.getUpperXBound());
				minY = MathUtils.min(minY, item.getLowerYBound());
				maxY = MathUtils.max(maxY, item.getUpperYBound());
			}
		}
		lowerXBound.set(minX);
		upperXBound.set(maxX);
		lowerYBound.set(minY);
		upperYBound.set(maxY);

	}

	private void removeAll(Iterable<? extends T> items) {
		items.forEach(this::remove);
	}

	private void remove(T item) {
		if (item.isValid()) {
			if ((item.getLowerXBound().equals(this.lowerXBound.get()))
					|| (item.getLowerYBound().equals(this.lowerYBound.get()))
					|| (item.getUpperXBound().equals(this.upperXBound.get()))
					|| (item.getUpperYBound().equals(this.upperYBound.get()))) {
				recalculateBounds();
			}
		}
	}

	public static IntBounded boundsFrom(int lowerXBound, int upperXBound, int lowerYBound,
			int upperYBound) {
		BoundingBox<IntBounded> box = new BoundingBox<>();
		box.lowerXBound.set(lowerXBound);
		box.upperXBound.set(upperXBound);
		box.lowerYBound.set(lowerYBound);
		box.upperYBound.set(upperYBound);
		return box;
	}

	public static IntBounded boundsFrom(IntBounded bounds) {
		BoundingBox<IntBounded> box = new BoundingBox<>();
		box.lowerXBound.set(bounds.getLowerXBound());
		box.upperXBound.set(bounds.getUpperXBound());
		box.lowerYBound.set(bounds.getLowerYBound());
		box.upperYBound.set(bounds.getUpperYBound());
		return box;
	}

}
