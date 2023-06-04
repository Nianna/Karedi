package com.github.nianna.karedi.util;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public final class KeyCodeCombinations {
	public static final KeyCodeCombination CTRL_X = new KeyCodeCombination(KeyCode.X,
			KeyCombination.SHORTCUT_DOWN);
	public static final KeyCodeCombination CTRL_V = new KeyCodeCombination(KeyCode.V,
			KeyCombination.SHORTCUT_DOWN);

	private KeyCodeCombinations() {
	}
}
