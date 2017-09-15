package main.java.com.github.nianna.karedi.controller;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.Node;
import main.java.com.github.nianna.karedi.context.AppContext;

public interface Controller {
	public void setAppContext(AppContext appContext);
	public Node getContent();

	default public boolean isDisabled() {
		return getContent().isDisabled();
	}

	default public void setDisable(boolean value) {
		getContent().setDisable(value);
	}

	default public Node getFocusableContent() {
		return getContent();
	}

	default public boolean isFocused() {
		return getFocusableContent().isFocused();
	}

	default public void requestFocus() {
		getFocusableContent().requestFocus();
	}

	default public ReadOnlyBooleanProperty focusedProperty() {
		return getFocusableContent().focusedProperty();
	}

}
