package main.java.com.github.nianna.karedi.action;

import org.controlsfx.control.action.Action;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;

public abstract class KarediAction extends Action {

	public KarediAction() {
		super("");
		setEventHandler(event -> {
			if (!isDisabled()) {
				onAction(event);
			}
		});
	}

	protected abstract void onAction(ActionEvent event);

	protected final void setDisabledCondition(ObservableValue<? extends Boolean> condition) {
		if (condition != null) {
			disabledProperty().bind(condition);
		} else {
			disabledProperty().unbind();
		}
	};

	protected final void setDisabledCondition(boolean value) {
		disabledProperty().unbind();
		setDisabled(value);
	}

	public boolean wasFired(KeyEvent event) {
		return getAccelerator() != null && getAccelerator().match(event);
	}

}