package test.java.com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.Test;

import main.java.com.github.nianna.karedi.parser.elementunparser.Utils;

public class UtilsTest {

	@Test
	public void joinsWithSpecifiedDelimiterCorrectly() {
		String delimiter = "$";
		String[] strings = { "Dummy ", "foo", " bar" };
		Assert.assertEquals(String.join(delimiter, strings), Utils.join(delimiter, strings));
	}

	@Test
	public void convertsIntegersToStringsCorrectly() {
		Assert.assertEquals("40", Utils.integerToString(40));
	}

	@Test
	public void returnsEmptyStringAsARepresentationOfNullInteger() {
		Assert.assertEquals("", Utils.integerToString(null));
	}

}
