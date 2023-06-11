package com.github.nianna.karedi.util;

import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiplayerTagsTest {

	@Test
	public void recognizesValidNameTagKeys() {
		Tag tag = new Tag("DUETSINGERP2", "");
		assertTrue(MultiplayerTags.isANameTag(tag));
	}

	@Test
	public void otherTagKeysAreNotRecognizedAsNameTags() {
		Tag tag = new Tag("ARTIST", "");
		assertFalse(MultiplayerTags.isANameTag(tag));
	}

	@Test
	public void createsNameTagsForTracksWithCustomizedNames() {
		assertTrue(MultiplayerTags.nameTagForTrack(0, "Custom name").isPresent());
	}

	@Test
	public void createsCorrectNameTagsForTracksWithCustomizedNames() {
		String name = "Foo";
		Tag result = MultiplayerTags.nameTagForTrack(0, name).get();
		assertEquals("Wrong key", "DUETSINGERP1", result.getKey());
		assertEquals("Wrong value", name, result.getValue());
	}

	@Test
	public void doesNotCreateNameTagsForTracksWithDefaultNames() {
		// Track number n is meant for player (n+1)
		int index = 3;
		SongTrack track = new SongTrack(index + 1);
		assertFalse(MultiplayerTags.nameTagForTrack(index, track.getName()).isPresent());
	}

	@Test
	public void extractsCorrectPlayerNumbersFromValidNameTags() {
		Tag tag = new Tag("DUETSINGERP3", "");
		Optional<Integer> result = MultiplayerTags.getPlayerNumber(tag);
		assertTrue( result.isPresent());
		assertEquals((Integer) 3, result.get());
	}

	@Test
	public void doesNotExtractPlayerNumbersFromOtherTags() {
		Tag tag = new Tag("ARTIST", "");
		assertFalse(MultiplayerTags.getPlayerNumber(tag).isPresent());
	}

	@Test
	public void extractsCorrectTrackNumbersFromValidNameTags() {
		Tag tag = new Tag("DUETSINGERP3", "");
		Optional<Integer> result = MultiplayerTags.getTrackNumber(tag);
		assertTrue(result.isPresent());
		assertEquals((Integer) 2, result.get());
	}

	@Test
	public void doesNotExtractTrackNumbersFromOtherTags() {
		Tag tag = new Tag("ARTIST", "");
		assertFalse(MultiplayerTags.getTrackNumber(tag).isPresent());
	}

}
