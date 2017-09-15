package main.java.com.github.nianna.karedi.util;

public final class LineBreakCalculator {
	private LineBreakCalculator() {
	}

	/*
	 * Algorithm as in YASS & as expected by ultrastar-es.org
	 */
	public static int calculateBeat(int formerLineEndBeat, int latterLineStartBeat, double bpm) {

		double beatDuration = 60000 / (4 * bpm);
		int difference = latterLineStartBeat - formerLineEndBeat;

		// if pause >= 4 set break after 2 secs
		if (difference * beatDuration >= 4000) {
			return formerLineEndBeat + (int) (2000 / beatDuration);
		}

		// if pause >= 2 set break after 1 sec
		if (difference * beatDuration >= 2000) {
			return formerLineEndBeat + (int) (1000 / beatDuration);
		}

		if (difference < 2) {
			return formerLineEndBeat;
		}
		if (MathUtils.inRange(difference, 2, 9)) {
			return latterLineStartBeat - 2;
		}
		if (MathUtils.inRange(difference, 9, 13)) {
			return latterLineStartBeat - 3;
		}
		if (MathUtils.inRange(difference, 13, 17)) {
			return latterLineStartBeat - 4;
		}
		return formerLineEndBeat + 10;
	}
}
