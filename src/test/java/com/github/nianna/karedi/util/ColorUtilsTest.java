package test.java.com.github.nianna.karedi.util;

import javafx.scene.paint.Color;
import main.java.com.github.nianna.karedi.util.ColorUtils;
import org.junit.Assert;
import org.junit.Test;

public class ColorUtilsTest {

	@Test
	public void shouldDeserializeSerializedColor() {
		String serializedColor = ColorUtils.serialize(Color.ORANGE);
		Assert.assertEquals(ColorUtils.deserialize(serializedColor), Color.ORANGE);
	}

}
