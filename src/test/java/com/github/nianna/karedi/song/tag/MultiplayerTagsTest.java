package com.github.nianna.karedi.song.tag;

import com.github.nianna.karedi.Settings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiplayerTagsTest {

	@ParameterizedTest
	@ValueSource(strings = {"DUETSINGERP1", "DUETSINGERP2", "P1", "P2", "P3"})
	public void recognizesValidNameTagKeys(String key) {
		Tag tag = new Tag(key, "");
		assertTrue(MultiplayerTags.isANameTag(tag));
	}

	@Test
	public void otherTagKeysAreNotRecognizedAsNameTags() {
		Tag tag = new Tag("ARTIST", "");
		assertFalse(MultiplayerTags.isANameTag(tag));
	}

	@Test
	public void extractsCorrectTrackNumbersFromValidDuetSingerPTags() {
		Tag tag = new Tag("DUETSINGERP3", "");
		Optional<Integer> result = MultiplayerTags.getTrackNumber(tag);
		assertTrue(result.isPresent());
		assertEquals((Integer) 2, result.get());
	}

	@Test
	public void extractsCorrectTrackNumbersFromValidPTags() {
		Tag tag = new Tag("P3", "");
		Optional<Integer> result = MultiplayerTags.getTrackNumber(tag);
		assertTrue(result.isPresent());
		assertEquals((Integer) 2, result.get());
	}

	@Test
	public void doesNotExtractTrackNumbersFromOtherTags() {
		Tag tag = new Tag("ARTIST", "");
		assertFalse(MultiplayerTags.getTrackNumber(tag).isPresent());
	}

	@Test
	public void shouldCreatePTagKeyForTrackByDefault() {
		assertEquals("P2", MultiplayerTags.getTagKeyForTrackNumber(1, null));
	}

	@Test
	public void shouldCreateDuetSingerTagKeyForTrackIfSettingEnabled() {
		Settings.setUseDuetSingerTags(true);
		assertEquals("DUETSINGERP2", MultiplayerTags.getTagKeyForTrackNumber(1, null));
	}

	@ParameterizedTest
	@EnumSource(FormatSpecification.class)
	public void shouldCreatePTagKeyForTrackIfSettingEnabledButFormatSpecified(FormatSpecification version) {
		Settings.setUseDuetSingerTags(true);
		assertEquals("P2", MultiplayerTags.getTagKeyForTrackNumber(1, version));
	}

	@AfterEach
	public void cleanUp() {
		Settings.setUseDuetSingerTags(false);
	}
}
