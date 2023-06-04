package com.github.nianna.karedi.controller;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.Node;
import com.github.nianna.karedi.context.AppContext;

public interface Controller {
	void setAppContext(AppContext appContext);
	Node getContent();

	default boolean isDisabled() {
		return getContent().isDisabled();
	}

	default void setDisable(boolean value) {
		getContent().setDisable(value);
	}

	default Node getFocusableContent() {
		return getContent();
	}

	default boolean isFocused() {
		return getFocusableContent().isFocused();
	}

	default void requestFocus() {
		getFocusableContent().requestFocus();
	}

	default ReadOnlyBooleanProperty focusedProperty() {
		return getFocusableContent().focusedProperty();
	}

}
