package com.github.nianna.karedi.song.tag;

import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.util.Converter;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MultiplayerTags {

	private static final Pattern MULTIPLAYER_TAG_NAME_PATTERN = Pattern.compile("(DUETSINGERP|P)(\\d+)");

	private MultiplayerTags() {
	}

	public static boolean isANameTag(Tag tag) {
		return MULTIPLAYER_TAG_NAME_PATTERN.matcher(tag.getKey()).matches();
	}

	public static Optional<Tag> nameTagForTrack(int index, String name) {
		if (!name.equals(SongTrack.getDefaultTrackName(index + 1))) {
			return Optional.of(new Tag("DUETSINGERP" + (index + 1), name));
		}
		return Optional.empty();
	}

	public static Optional<Integer> getTrackNumber(Tag tag) {
		return getPlayerNumber(tag).map(player -> player - 1);
	}

	public static Optional<Integer> getPlayerNumber(Tag tag) {
		if (isANameTag(tag)) {
			Matcher matcher = MULTIPLAYER_TAG_NAME_PATTERN.matcher(tag.getKey());
			if (matcher.find()) {
				return Converter.toInteger(matcher.group(2));
			}
		}
		return Optional.empty();
	}
}
