package com.github.nianna.karedi.util;

import javafx.scene.paint.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorUtilsTest {

	@Test
	public void shouldDeserializeSerializedColor() {
		String serializedColor = ColorUtils.serialize(Color.ORANGE);
		assertEquals(ColorUtils.deserialize(serializedColor), Color.ORANGE);
	}

}
