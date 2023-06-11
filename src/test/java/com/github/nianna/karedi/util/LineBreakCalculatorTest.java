package com.github.nianna.karedi.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LineBreakCalculatorTest {
	private static final double BPM = 300;
	private static final int FORMER_END = 200;

	private int execute(int latterStartBeat) {
		return LineBreakCalculator.calculateBeat(FORMER_END, latterStartBeat, BPM);
	}

	@Test
	public void returnsFormerLineEndBeatIfThereIsOneBeatDifference() {
		assertEquals(FORMER_END, execute(FORMER_END + 1));
	}

	@Test
	public void returnsFormerLineEndBeatIfTheLinesTouch() {
		assertEquals(FORMER_END, execute(FORMER_END));
	}

	@Test
	public void returnsLatterLineStartBeatMinusTwoBeatsForDifferenceFromTwoToEight() {
		for (int i = 2; i <= 8; ++i) {
			int start = FORMER_END + i;
			assertEquals(start - 2, execute(start));
		}
	}

	@Test
	public void returnsLatterLineStartBeatMinusThreeBeatsForDifferenceFromNineToTwelve() {
		for (int i = 9; i <= 12; ++i) {
			int start = FORMER_END + i;
			assertEquals(start - 3, execute(start));
		}
	}

	@Test
	public void returnsLatterLineStartBeatMinusFourBeatsForDifferenceFromThirteenToSixteen() {
		for (int i = 13; i <= 16; ++i) {
			int start = FORMER_END + i;
			assertEquals(start - 4, execute(start));
		}
	}

	@Test
	public void placesBreakAfterTwoSecondsIfPauseLastsFourSecondsOrMore() {
		int pauseLength = 6_000; // ms
		int difference = (int) (pauseLength / getBeatDuration(BPM));
		int expectedBeat = FORMER_END + (int) (2_000 / getBeatDuration(BPM));
		assertEquals(expectedBeat, execute(FORMER_END + difference));
	}

	@Test
	public void placesBreakAfterOneSecondIfPauseLastsMoreThanTwoButLessThanFourSeconds() {
		int pauseLength = 3_000; // ms
		int difference = (int) (pauseLength / getBeatDuration(BPM));
		int expectedBeat = FORMER_END + (int) (1_000 / getBeatDuration(BPM));
		assertEquals(expectedBeat, execute(FORMER_END + difference));
	}

	@Test
	public void returnsFormerLineEndBeatPlusTenBeatsForBiggerDifferences() {
		// If it lasts less than two seconds
		int difference = 20; // with BPM 300 lasts 1 second
		assertEquals(FORMER_END + 10, execute(FORMER_END + difference));
	}

	private double getBeatDuration(double bpm) {
		return new BeatMillisConverter(0, bpm).getBeatDuration();
	}

}
