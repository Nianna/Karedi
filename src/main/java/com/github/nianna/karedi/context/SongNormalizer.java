package com.github.nianna.karedi.context;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.song.tag.TagValidators;
import com.github.nianna.karedi.util.Converter;
import com.github.nianna.karedi.util.MultiplayerTags;

class SongNormalizer {
	private static final Logger LOGGER = Logger.getLogger(SongNormalizer.class.getName());
	private Song song;

	SongNormalizer(Song song) {
		this.song = song;
	}

	void normalize() {
		if (song != null) {
			normalizeGap();
			normalizeBpm();
			normalizeTracks();
		}
	}

	private void normalizeGap() {
		if (!song.hasTag(TagKey.GAP)) {
			setDefaultGap();
		} else {
			validateGap();
		}
	}

	private void normalizeBpm() {
		if (!song.hasTag(TagKey.BPM)) {
			setDefaultBpm();
		} else {
			validateBpm();
		}
	}

	private void normalizeTracks() {
		if (song.getTrackCount() == 0) {
			song.addTrack(new SongTrack(1));
		}
		extractTrackNamesFromTags(song);
	}

	private void setDefaultBpm() {
		LOGGER.warning(I18N.get("normalizer.bpm.missing", Song.DEFAULT_BPM));
		song.setTagValue(TagKey.BPM, Converter.toString(Song.DEFAULT_BPM));
	}

	private void setDefaultGap() {
		song.setTagValue(TagKey.GAP, Converter.toString(Song.DEFAULT_GAP));
		LOGGER.warning(I18N.get("normalizer.gap.missing", Song.DEFAULT_GAP));
	}

	private void validateBpm() {
		String value = song.getTagValue(TagKey.BPM).get();
		if (TagValidators.hasValidationErrors(TagKey.BPM, value)) {
			LOGGER.severe(I18N.get("normalizer.bpm.invalid", value, Song.DEFAULT_BPM));
			song.getBeatMillisConverter().setBpm(Song.DEFAULT_BPM);
		}
	}

	private void validateGap() {
		String value = song.getTagValue(TagKey.GAP).get();
		if (TagValidators.hasValidationErrors(TagKey.GAP, value)) {
			LOGGER.severe(I18N.get("normalizer.gap.invalid", value, Song.DEFAULT_GAP));
			song.getBeatMillisConverter().setGap(Song.DEFAULT_GAP);
		}
	}

	private static void extractTrackNamesFromTags(Song song) {
		getNameTags(song.getTags()).forEach(tag -> {
			MultiplayerTags.getTrackNumber(tag).ifPresent(number -> {
				String name = tag.getValue();
				song.removeTag(tag);
				song.renameTrack(number, name);
			});
		});
	}

	private static List<Tag> getNameTags(List<Tag> tags) {
		return tags.stream().filter(MultiplayerTags::isANameTag).collect(Collectors.toList());
	}
}
