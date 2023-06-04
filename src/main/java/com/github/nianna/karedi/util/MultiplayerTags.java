package com.github.nianna.karedi.util;

import java.util.Optional;

import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;

public final class MultiplayerTags {
	private MultiplayerTags() {
	}

	public static boolean isANameTag(Tag tag) {
		return tag.getKey().matches("DUETSINGERP[0-9]+");
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
			return Converter.toInteger(tag.getKey().substring("DUETSINGERP".length()));
		}
		return Optional.empty();
	}
}
