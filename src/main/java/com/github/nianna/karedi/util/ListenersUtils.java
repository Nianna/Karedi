package com.github.nianna.karedi.util;

import java.util.function.Consumer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ListenersUtils {

	private ListenersUtils() {
	}

	public static <T extends Observable> void addListElementListener(ObservableList<T> list,
			InvalidationListener listener) {
		list.addListener((ListChangeListener<? super T>) c -> {
			while (c.next()) {
				if (!c.wasPermutated() && !c.wasUpdated()) {
					for (T item : c.getRemoved()) {
						item.removeListener(listener);
					}
					for (T item : c.getAddedSubList()) {
						item.addListener(listener);
					}
				}
			}
		});
	}

	public static <T> ListChangeListener<? super T> createListChangeListener(
			Consumer<T> permutatedConsumer, Consumer<T> updatedConsumer, Consumer<T> addedConsumer,
			Consumer<T> removedConsumer) {
		return (c -> {
			while (c.next()) {
				if (c.wasPermutated()) {
					for (int i = 0; i < c.getList().size(); ++i) {
						if (i != c.getPermutation(i)) {
							permutatedConsumer.accept(c.getList().get(i));
						}
					}
				} else if (c.wasUpdated()) {
					for (int i = c.getFrom(); i < c.getTo(); ++i) {
						updatedConsumer.accept(c.getList().get(i));
					}
				} else {
					for (T remitem : c.getRemoved()) {
						removedConsumer.accept(remitem);
					}
					for (T additem : c.getAddedSubList()) {
						addedConsumer.accept(additem);
					}
				}
			}
		});
	}

	public static <T> ListChangeListener<? super T> createListContentChangeListener(
			Consumer<T> addedConsumer, Consumer<T> removedConsumer) {
		return createListChangeListener(ListenersUtils::pass, ListenersUtils::pass, addedConsumer,
				removedConsumer);
	}

	public static <T> void pass(T e) {
	}
}
