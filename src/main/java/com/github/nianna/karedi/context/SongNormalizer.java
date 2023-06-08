package com.github.nianna.karedi.context;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.song.tag.TagValidators;
import com.github.nianna.karedi.util.Converter;
import com.github.nianna.karedi.util.MultiplayerTags;

import java.util.logging.Logger;

class SongNormalizer {

	private static final Logger LOGGER = Logger.getLogger(SongNormalizer.class.getName());

	void normalize(Song song) {
		if (song != null) {
			normalizeGap(song);
			normalizeBpm(song);
			normalizeTracks(song);
		}
	}

	private void normalizeGap(Song song) {
		if (!song.hasTag(TagKey.GAP)) {
			setDefaultGap(song);
		} else {
			validateGap(song);
		}
	}

	private void normalizeBpm(Song song) {
		if (!song.hasTag(TagKey.BPM)) {
			setDefaultBpm(song);
		} else {
			validateBpm(song);
		}
	}

	private void normalizeTracks(Song song) {
		if (song.getTrackCount() == 0) {
			song.addTrack(new SongTrack(1));
		}
		extractTrackNamesFromTags(song);
	}

	private void setDefaultBpm(Song song) {
		LOGGER.warning(I18N.get("normalizer.bpm.missing", Song.DEFAULT_BPM));
		song.setTagValue(TagKey.BPM, Converter.toString(Song.DEFAULT_BPM));
	}

	private void setDefaultGap(Song song) {
		song.setTagValue(TagKey.GAP, Converter.toString(Song.DEFAULT_GAP));
		LOGGER.warning(I18N.get("normalizer.gap.missing", Song.DEFAULT_GAP));
	}

	private void validateBpm(Song song) {
		String value = song.getTagValue(TagKey.BPM).orElseThrow();
		if (TagValidators.hasValidationErrors(TagKey.BPM, value)) {
			LOGGER.severe(I18N.get("normalizer.bpm.invalid", value, Song.DEFAULT_BPM));
			song.getBeatMillisConverter().setBpm(Song.DEFAULT_BPM);
		}
	}

	private void validateGap(Song song) {
		String value = song.getTagValue(TagKey.GAP).orElseThrow();
		if (TagValidators.hasValidationErrors(TagKey.GAP, value)) {
			LOGGER.severe(I18N.get("normalizer.gap.invalid", value, Song.DEFAULT_GAP));
			song.getBeatMillisConverter().setGap(Song.DEFAULT_GAP);
		}
	}

	private static void extractTrackNamesFromTags(Song song) {
		song.getTags().stream()
				.filter(MultiplayerTags::isANameTag)
				.forEach(tag -> renameTrack(song, tag));
	}

	private static void renameTrack(Song song, Tag tag) {
		MultiplayerTags.getTrackNumber(tag).ifPresent(number -> {
			song.removeTag(tag);
			song.renameTrack(number, tag.getValue());
		});
	}

}
