package main.java.com.github.nianna.karedi.util;

import java.util.Optional;

import javafx.scene.input.KeyEvent;

public final class KeyEventUtils {
	private KeyEventUtils() {
	}

	public static boolean isAnyModifierDown(KeyEvent event) {
		return event.isAltDown() || event.isControlDown() || event.isMetaDown()
				|| event.isShiftDown() || event.isShortcutDown();
	}

	public static Optional<Integer> getPressedDigit(KeyEvent event) {
		if (event.getCode().isDigitKey()) {
			switch (event.getCode()) {
			case DIGIT0:
			case NUMPAD0:
				return Optional.of(0);
			case DIGIT1:
			case NUMPAD1:
				return Optional.of(1);
			case DIGIT2:
			case NUMPAD2:
				return Optional.of(2);
			case DIGIT3:
			case NUMPAD3:
				return Optional.of(3);
			case DIGIT4:
			case NUMPAD4:
				return Optional.of(4);
			case DIGIT5:
			case NUMPAD5:
				return Optional.of(5);
			case DIGIT6:
			case NUMPAD6:
				return Optional.of(6);
			case DIGIT7:
			case NUMPAD7:
				return Optional.of(7);
			case DIGIT8:
			case NUMPAD8:
				return Optional.of(8);
			case DIGIT9:
			case NUMPAD9:
				return Optional.of(9);
			default:
			}
		}
		return Optional.empty();
	}
}
