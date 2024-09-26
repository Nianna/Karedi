package com.github.nianna.karedi.song.tag;

import java.util.Locale;
import java.util.Optional;

public enum TagKey {
	TITLE,
	ARTIST,
	YEAR,
	GENRE,
	EDITION,
	CREATOR,
	LANGUAGE,
	COVER,
	MP3,
	AUDIO,
	VOCALS,
	INSTRUMENTAL,
	VIDEO,
	BACKGROUND,
	BPM,
	GAP,
	START,
	END,
	VIDEOGAP,
	PREVIEWSTART,
	DUETSINGERP1,
	DUETSINGERP2,
	P1,
	P2,
	MEDLEYSTARTBEAT,
	MEDLEYENDBEAT,
	VERSION,
	TAGS,
	COMMENT,
	PROVIDEDBY;

	public static Optional<TagKey> optionalValueOf(String str) {
		str = str.trim().toUpperCase(Locale.ROOT);
		try {
			return Optional.of(valueOf(str));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	public static boolean expectsAnInteger(TagKey key) {
		switch (key) {
		case YEAR:
		case GAP:
		case MEDLEYSTARTBEAT:
		case MEDLEYENDBEAT:
		case END:
			return true;
		default:
			return false;
		}
	}

	public static boolean expectsADouble(TagKey key) {
		switch (key) {
		case START:
		case VIDEOGAP:
		case BPM:
			return true;
		default:
			return false;
		}
	}

	public static boolean expectsAFileName(TagKey key) {
		switch (key) {
			case MP3, COVER, AUDIO, VIDEO, BACKGROUND, VOCALS, INSTRUMENTAL:
				return true;
			default:
				return false;
		}
	}

}
