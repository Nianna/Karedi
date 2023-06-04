package test.java.com.github.nianna.karedi.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.nianna.karedi.util.BeatMillisConverter;

public class BeatMillisConverterTest {
	private static final double BPM = 300;
	private static final int GAP = 10_000;
	private static final double BEAT_DURATION = 60_000 / (4 * BPM);
	private static final double EPSILON = 1e-15;

	private BeatMillisConverter converter;

	@Before
	public void setUp() {
		converter = new BeatMillisConverter(GAP, BPM);
	}

	@Test
	public void returnsCorrectBeatDuration() {
		Assert.assertEquals(BEAT_DURATION, converter.getBeatDuration(), EPSILON);
	}

	@Test
	public void convertsBeatsToMillisCorrectly() {
		int beat = 100;
		long millis = GAP + (long) (beat * BEAT_DURATION);
		Assert.assertEquals(millis, converter.beatToMillis(beat));
	}

	@Test
	public void millisToBeatConvertionReturnsZeroForGap() {
		Assert.assertEquals(0, converter.millisToBeat(GAP));
	}

	@Test
	public void convertsMillisToBeatsCorrectly() {
		long millis = (long) (GAP + BEAT_DURATION);
		Assert.assertEquals(1, converter.millisToBeat(millis));
	}

}
