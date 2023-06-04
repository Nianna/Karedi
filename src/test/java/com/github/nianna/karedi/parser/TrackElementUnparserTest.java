package com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.nianna.karedi.parser.element.TrackElement;
import com.github.nianna.karedi.parser.elementunparser.TrackElementUnparser;

public class TrackElementUnparserTest {
	private static TrackElementUnparser unparser;

	@BeforeClass
	public static void setUpClass() {
		unparser = new TrackElementUnparser();
	}

	@Test
	public void unparsesCorrectly() {
		TrackElement element = new TrackElement(2);
		Assert.assertEquals("P 2", unparser.unparse(element));
	}

}
