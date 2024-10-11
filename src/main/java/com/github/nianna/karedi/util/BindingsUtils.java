package com.github.nianna.karedi.util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.util.Collection;

public final class BindingsUtils {
	private BindingsUtils() {
	}

	public static <T> ObjectBinding<T> valueAt(ObservableList<T> list, int index) {
		return Bindings.createObjectBinding(() -> {
			if (index >= 0 && index < list.size()) {
				return list.get(index);
			}
			return null;
		}, list);
	}

	public static <T> IntegerBinding sizeOf(ObservableList<T> list) {
		return Bindings.createIntegerBinding(() -> {
			return list.size();
		}, list);
	}

	public static <T> BooleanBinding isEmpty(ObservableList<T> list) {
		return Bindings.createBooleanBinding(() -> {
			return list.isEmpty();
		}, list);
	}

	public static DoubleBinding multiply(ReadOnlyObjectProperty<Double> value1,
			ObservableNumberValue value2) {
		return Bindings.createDoubleBinding(() -> {
			return value1.get() * value2.doubleValue();
		}, value1, value2);
	}

	public static IntegerBinding emptyIntegerBinding() {
		return new IntegerBinding() {
			@Override
			protected int computeValue() {
				return 0;
			}
		};
	}

	public static <T> AutoCompletionBinding<T> bindAutoCompletion(TextField textField, Collection<T> possibleSuggestions) {
		if (!possibleSuggestions.isEmpty()) {
			return TextFields.bindAutoCompletion(textField, possibleSuggestions);
		}
		return null;
	}

}
