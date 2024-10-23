package com.github.nianna.karedi.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeatMillisConverterTest {
	private static final double BPM = 300;
	private static final int GAP = 10_000;
	private static final double BEAT_DURATION = 60_000 / (4 * BPM);
	private static final double EPSILON = 1e-15;

	private BeatMillisConverter converter;

	@BeforeEach
	public void setUp() {
		converter = new BeatMillisConverter(GAP, BPM);
	}

	@Test
	public void returnsCorrectBeatDuration() {
		assertEquals(BEAT_DURATION, converter.getBeatDuration(), EPSILON);
	}

	@Test
	public void convertsBeatsToMillisCorrectly() {
		int beat = 100;
		long millis = GAP + (long) (beat * BEAT_DURATION);
		assertEquals(millis, converter.beatToMillis(beat));
	}

	@Test
	public void millisToBeatConvertionReturnsZeroForGap() {
		assertEquals(0, converter.millisToBeat(GAP));
	}

	@Test
	public void convertsMillisToBeatsCorrectly() {
		long millis = (long) (GAP + BEAT_DURATION);
		assertEquals(1, converter.millisToBeat(millis));
	}

	@Test
	public void conversionMethodsAreConsistent() {
		converter = new BeatMillisConverter(440, 223.94);
		int testBeat = 255;
		assertEquals(testBeat, converter.millisToBeat(converter.beatToMillis(testBeat)));
	}

}
