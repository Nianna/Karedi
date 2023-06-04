package com.github.nianna.karedi.audio;

import com.mpatric.mp3agic.MpegFrame;

/**
 * Contains standard SPF (samples per frame) values for
 * different versions and layers of MPEG.
 *
 */
public final class SamplesPerFrame {
	public static final int DEFAULT = 1152;

	private SamplesPerFrame() {
	}

	private static final int[][] samplesPerFrame = new int[][] {
		{   // MPEG Version 1
			384,    // Layer1
	        1152,   // Layer2
	        1152    // Layer3
	    },
	    {   // MPEG Version 2 & 2.5
	        384,    // Layer1
	        1152,   // Layer2
	        576     // Layer3
	    }
	};

	public static int get(String version, String layer) {
		if (version != null && layer != null) {
			switch (version) {
			case MpegFrame.MPEG_VERSION_1_0:
				return get(samplesPerFrame[0], layer);
			case MpegFrame.MPEG_VERSION_2_0:
				return get(samplesPerFrame[1], layer);
			case MpegFrame.MPEG_VERSION_2_5:
				return get(samplesPerFrame[1], layer);
			}
		}
		return DEFAULT;
	}

	private static int get(int[] spfForVersion, String layer) {
		switch (layer) {
		case MpegFrame.MPEG_LAYER_1:
			return spfForVersion[0];
		case MpegFrame.MPEG_LAYER_2:
			return spfForVersion[1];
		case MpegFrame.MPEG_LAYER_3:
			return spfForVersion[2];
		default:
			return DEFAULT;
		}
	}
}
