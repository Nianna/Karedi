package com.github.nianna.karedi.song.tag;

import com.github.nianna.karedi.Settings;
import com.github.nianna.karedi.util.Converter;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MultiplayerTags {

	private static final Pattern TAG_KEY_PATTERN = Pattern.compile("DUETSINGERP([12])|P([1-9]\\d*)");

	private MultiplayerTags() {
	}

	public static boolean isANameTag(Tag tag) {
		return isANameTagKey(tag.getKey());
	}

	public static boolean isANameTagKey(String tagKey) {
		return TAG_KEY_PATTERN.matcher(tagKey).matches();
	}

	public static String getTagKeyForTrackNumber(int index, FormatSpecification formatSpecification) {
		if (index < 2 && formatSpecification == null && Settings.isUseDuetSingerTags()) {
			return "DUETSINGERP" + (index + 1);
		}

		return "P" + (index + 1);
	}

	public static Optional<Integer> getTrackNumber(Tag tag) {
		return getTrackNumber(tag.getKey());
	}

	public static Optional<Integer> getTrackNumber(String tagKey) {
		return getPlayerNumber(tagKey).map(player -> player - 1);
	}

	private static Optional<Integer> getPlayerNumber(String tagKey) {
		Matcher matcher = TAG_KEY_PATTERN.matcher(tagKey);
		if (matcher.find()) {
			String playerNumber = Optional.ofNullable(matcher.group(1)).orElseGet(() -> matcher.group(2));
			return Converter.toInteger(playerNumber);
		}
		return Optional.empty();
	}
}
